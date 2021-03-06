/*************************************************************************
*                                                                        *
*  This file is part of the 20n/act project.                             *
*  20n/act enables DNA prediction for synthetic biology/bioengineering.  *
*  Copyright (C) 2017 20n Labs, Inc.                                     *
*                                                                        *
*  Please direct all queries to act@20n.com.                             *
*                                                                        *
*  This program is free software: you can redistribute it and/or modify  *
*  it under the terms of the GNU General Public License as published by  *
*  the Free Software Foundation, either version 3 of the License, or     *
*  (at your option) any later version.                                   *
*                                                                        *
*  This program is distributed in the hope that it will be useful,       *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
*  GNU General Public License for more details.                          *
*                                                                        *
*  You should have received a copy of the GNU General Public License     *
*  along with this program.  If not, see <http://www.gnu.org/licenses/>. *
*                                                                        *
*************************************************************************/

package com.act.biointerpretation.rsmiles

import java.io.File

import com.act.analysis.chemicals.molecules.MoleculeFormat
import com.act.biointerpretation.rsmiles.chemicals.abstract_chemicals.AbstractChemicalsToReactions
import com.act.biointerpretation.rsmiles.cluster_sar_construction.{ConstructSarsFromPredictionCorpus, ReactionRoAssignment}
import com.act.workflow.tool_manager.jobs.Job
import com.act.workflow.tool_manager.tool_wrappers.{ScalaJobWrapper, SparkWrapper}
import com.act.workflow.tool_manager.workflow.Workflow
import org.apache.commons.cli.{CommandLine, Options, Option => CliOption}
import org.apache.log4j.LogManager
import org.apache.spark


class AbstractReactionsToL3ProjectionWorkflow extends Workflow {

  val DEFAULT_SPARK_MASTER = "spark://spark-master:7077"
  val LOCAL_JAR_PATH = "target/scala-2.10/reachables-assembly-0.1.jar"

  val OPTION_USE_CACHED_RESULTS = "c"
  val OPTION_DATABASE = "d"
  val OPTION_METABOLITE_FILE = "f"
  val OPTION_CHEMAXON_LICENSE = "l"
  val OPTION_SPARK_MASTER = "m"
  val OPTION_SUBSTRATE_COUNTS = "s"
  val OPTION_VALID_CHEMICAL_TYPE = "v"
  val OPTION_WORKING_DIRECTORY = "w"

  private val LOGGER = LogManager.getLogger(getClass)

  override def getCommandLineOptions: Options = {
    val options = List[CliOption.Builder](

      CliOption.builder(OPTION_DATABASE).
        hasArg.
        longOpt("database").
        desc("The database to connect to.  This is where we will find the abstract chemicals and reactions.  " +
          "By default uses the \"marvin\" database."),

      CliOption.builder(OPTION_WORKING_DIRECTORY).
        hasArg.
        longOpt("working-directory").
        required.
        desc("The directory in which to run and create all intermediate files. This directory will be created if it " +
          "does not already exist."),

      CliOption.builder(OPTION_SUBSTRATE_COUNTS).
        hasArgs.
        valueSeparator(',').
        longOpt("substrate-counts").
        required.
        desc("A list of numbers.  This list will inform which reactions will be written to a file by " +
          "filtering the reactions by substrate.  For example, \"1,2\" would mean that 1 and 2 " +
          "substrate reactions will be written to a file."),

      CliOption.builder(OPTION_CHEMAXON_LICENSE).
        longOpt("chemaxon-license-file").
        hasArg.
        required.
        desc("Location of the \"CHEMAXON-LICENSE.cxl\" file."),

      CliOption.builder(OPTION_METABOLITE_FILE)
        .hasArg
        .longOpt("metabolite-file")
        .required
        .desc("The absolute path to the metabolites file."),

      CliOption.builder(OPTION_SPARK_MASTER).
        longOpt("spark-master").
        desc(s"Where to look for the spark master connection. " +
          s"Uses '$DEFAULT_SPARK_MASTER' by default."),

      CliOption.builder(OPTION_USE_CACHED_RESULTS).
        longOpt("use-cached-results").
        desc("If this flag is enabled, we will check if files that would be " +
          "made currently exist and use those files whenever possible."),

      CliOption.builder(OPTION_VALID_CHEMICAL_TYPE).
        longOpt("valid-chemical-types").
        hasArg.
        desc("A molecule string format. Currently valid types are inchi, stdInchi, smiles, and smarts.  " +
          s"By default uses the format '${MoleculeFormat.getExportString(MoleculeFormat.strictNoStereoInchi)}'.  " +
          s"Only InChI based formats are allowed for this workflow." +
          s"Possible values are: \n${MoleculeFormat.listPossibleFormatStrings().mkString("\n")}"),

      CliOption.builder("h").argName("help").desc("Prints this help message").longOpt("help")
    )

    val opts: Options = new Options()
    for (opt <- options) {
      opts.addOption(opt.build)
    }
    opts
  }

  override def defineWorkflow(cl: CommandLine): Job = {
    /*
      Setup Files
     */
    val chemaxonLicense = new File(cl.getOptionValue(OPTION_CHEMAXON_LICENSE))
    require(chemaxonLicense.exists() && chemaxonLicense.isFile,
      "Chemaxon license does not exist as the supplied location. " +
        s"File path supplied was ${chemaxonLicense.getAbsolutePath}")

    val outputDirectory = new File(cl.getOptionValue(OPTION_WORKING_DIRECTORY))
    require(!outputDirectory.isFile, "Working directory must be a directory, not a file.")
    if (!outputDirectory.exists()) outputDirectory.mkdirs()

    val metaboliteFile = new File(cl.getOptionValue(OPTION_METABOLITE_FILE))
    require(metaboliteFile.exists() && metaboliteFile.isFile,
      s"Metabolite file must exist. File path supplied was ${metaboliteFile.getAbsolutePath}")

    /*
     Setup database
     */
    val database = cl.getOptionValue(OPTION_DATABASE, "marvin")


    /*
     Setup Spark
     */
    val singleSubstrateRoProjectorClassPath = "com.act.biointerpretation.l2expansion.SparkSingleSubstrateROProjector"
    val sparkMaster = cl.getOptionValue(OPTION_SPARK_MASTER, DEFAULT_SPARK_MASTER)

    /*
      Format currently used for the molecular transitions
     */
    val moleculeFormatString = cl.getOptionValue(OPTION_VALID_CHEMICAL_TYPE, MoleculeFormat.strictNoStereoInchi.toString)
    require(moleculeFormatString.toLowerCase.contains("inchi"), "Format type for this is required to be InChI.")

    val moleculeFormat = MoleculeFormat.getName(moleculeFormatString)

    /*
      Setup the options for which substrate counts
      we'll be looking at and partially applies the abstract reaction function.
     */
    val substrateCounts: List[Int] = cl.getOptionValues(OPTION_SUBSTRATE_COUNTS).map(_.toInt).toList
    require(substrateCounts.nonEmpty,
      s"Please supply one or more substrate counts with option '$OPTION_SUBSTRATE_COUNTS'.  0 supplied.")
    val individualSubstrateFunction = AbstractChemicalsToReactions.calculateAbstractSubstrates(moleculeFormat)(database) _

    // Create all the jobs for all the substrates
    val jobs = substrateCounts.map(count => {
      val runId = s"Abstract.db.$database.subCount.$count"

      /*
        Step 1: Abstract chemicals => Abstract reactions substrate list
       */
      val substratesOutputFileName = s"$runId.Substrates.txt"
      val reactionsOutputFileName = s"$runId.txt"

      val substrateListOutputFile = new File(outputDirectory, substratesOutputFileName)
      val reactionListOutputFile = new File(outputDirectory, reactionsOutputFileName)

      val appliedFunction: () => Unit = individualSubstrateFunction(substrateListOutputFile, reactionListOutputFile, count)

      val abstractChemicalsToSubstrateListJob = if (cl.hasOption(OPTION_USE_CACHED_RESULTS) && substrateListOutputFile.exists()) {
        LOGGER.info(s"Using cached file ${substrateListOutputFile.getAbsolutePath}")
        ScalaJobWrapper.wrapScalaFunction("Using cached substrate list", () => Unit)
      } else {
        ScalaJobWrapper.wrapScalaFunction("Abstract chemicals to substrate list", appliedFunction)
      }

      /*
        Step 2: Spark submit substrate list => RO projection
       */
      val projectionDir = new File(outputDirectory, "ProjectionResults")
      if (!projectionDir.exists()) projectionDir.mkdirs()

      val roProjectionsOutputFileDirectory = new File(projectionDir, s"$runId.AbstractReactionRoProjections")
      if (!roProjectionsOutputFileDirectory.exists()) {
        roProjectionsOutputFileDirectory.mkdirs()
      } else {
        LOGGER.info(s"Removing previous directory ${roProjectionsOutputFileDirectory.getAbsolutePath} " +
          s"and recreating as an empty folder.")
        roProjectionsOutputFileDirectory.delete()
        roProjectionsOutputFileDirectory.mkdirs()
      }

      val roProjectionArgs = List(
        "--substrates-list", substrateListOutputFile.getAbsolutePath,
        "-o", roProjectionsOutputFileDirectory.getAbsolutePath,
        "-l", chemaxonLicense.getAbsolutePath,
        "-v", moleculeFormat.toString
      )

      // We assume files in = previous run
      val hasCachedResultsAbstractRoProjection =
        roProjectionsOutputFileDirectory.isDirectory &&
          roProjectionsOutputFileDirectory.list() != null &&
          roProjectionsOutputFileDirectory.list().length > 0

      val sparkRoProjection = if (cl.hasOption(OPTION_USE_CACHED_RESULTS) && hasCachedResultsAbstractRoProjection) {
        ScalaJobWrapper.wrapScalaFunction("Using cached spark abstract reaction RO projections", () => Unit)
      } else {
        SparkWrapper.runClassPath(
          LOCAL_JAR_PATH,
          singleSubstrateRoProjectorClassPath)(sparkMaster, roProjectionArgs)(memory = "4G")
      }

      abstractChemicalsToSubstrateListJob.thenRun(sparkRoProjection)

      /*
        Step 3: Spark submit match projections to input reactions
       */
      val roAssignmentDirectory = new File(outputDirectory, "RoAssignment")
      if (!roAssignmentDirectory.exists()) roAssignmentDirectory.mkdirs()

      val roAssignmentOutputFileName = new File(roAssignmentDirectory, s"$runId.RoAssignments.json")

      val reactionAssignJob = if (cl.hasOption(OPTION_USE_CACHED_RESULTS) && roAssignmentOutputFileName.exists()) {
        ScalaJobWrapper.wrapScalaFunction("Using cached ro assignments", () => Unit)
      } else {
        val reactionAssigner = ReactionRoAssignment.assignRoToReactions(roProjectionsOutputFileDirectory, reactionListOutputFile, roAssignmentOutputFileName) _
        ScalaJobWrapper.wrapScalaFunction("Ro Assignment to Reactions", reactionAssigner)
      }

      abstractChemicalsToSubstrateListJob.thenRun(reactionAssignJob)
      /*
        Step 4: Construct SARs from matching reactions
       */
      val sarCorpusDirectory = new File(outputDirectory, "SarCorpus")
      if (!sarCorpusDirectory.exists()) sarCorpusDirectory.mkdirs()
      val sarCorpusOutputFileName = s"$runId.sarCorpusOutput.json"
      val sarCorpusOutputFile = new File(sarCorpusDirectory, sarCorpusOutputFileName)
      val constructSars =
        ConstructSarsFromPredictionCorpus.sarConstructor(moleculeFormat)(roAssignmentOutputFileName, sarCorpusOutputFile) _

      val constructedSarJob =
        if (cl.hasOption(OPTION_USE_CACHED_RESULTS) && sarCorpusOutputFile.exists()) {
          ScalaJobWrapper.wrapScalaFunction("Using cached SAR corpus.", () => Unit)
        } else {
          ScalaJobWrapper.wrapScalaFunction("Sar Constructor", constructSars)
        }

      abstractChemicalsToSubstrateListJob.thenRun(constructedSarJob)

      /*
        Step 5: Project RO + SAR over L3

        Don't cache this step as it is the last one and would make everything pointless otherwise.
       */
      val l3ProjectionOutputDirectory = new File(outputDirectory, "L3Projections")
      if (!l3ProjectionOutputDirectory.exists()) {
        l3ProjectionOutputDirectory.mkdirs()
      } else {
        l3ProjectionOutputDirectory.delete()
        l3ProjectionOutputDirectory.mkdirs()
      }
      val l3ProjectionArgs = List(
        "--substrates-list", metaboliteFile.getAbsolutePath,
        "-o", l3ProjectionOutputDirectory.getAbsolutePath,
        "-l", chemaxonLicense.getAbsolutePath,
        "-v", moleculeFormat.toString,
        "-c", sarCorpusOutputFile.getAbsolutePath
      )

      val l3RoPlusSarProjection = SparkWrapper.runClassPath(
        LOCAL_JAR_PATH,
        singleSubstrateRoProjectorClassPath)(sparkMaster, l3ProjectionArgs)(memory = "4G")

      abstractChemicalsToSubstrateListJob.thenRun(l3RoPlusSarProjection)


      abstractChemicalsToSubstrateListJob
    })

    headerJob.thenRunBatch(jobs)
  }
}
