package com.act.reachables;

import act.installer.bing.BingSearchRanker;
import act.server.NoSQLAPI;
import act.shared.Reaction;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConditionalReachabilityInterpreter {

  private static final NoSQLAPI db = new NoSQLAPI("actv01", "actv01");
  private static final String BLACKLISTED_ROOT_INCHI = "InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/t4-,6-,7-,10-/m1/s1";
  public static final String OPTION_OUTPUT_FILEPATH = "o";
  public static final String OPTION_INPUT_ACT_FILEPATH = "i";

  public static final String HELP_MESSAGE = StringUtils.join(new String[]{
      "This class is used to deserialize a reachable forest and output bing search results of all chemicals within each root",
      "of the forest along with it's root associate."
  }, " ");

  public static final List<Option.Builder> OPTION_BUILDERS = new ArrayList<Option.Builder>() {{
    add(Option.builder(OPTION_OUTPUT_FILEPATH)
        .argName("OUTPUT_FILEPATH")
        .desc("The full path to the output file")
        .hasArg()
        .required()
        .longOpt("output_filepath")
        .type(String.class)
    );
    add(Option.builder(OPTION_INPUT_ACT_FILEPATH)
        .argName("INPUT_FILEPATH")
        .desc("The full path to the input file")
        .hasArg()
        .required()
        .longOpt("input_filepath")
        .type(String.class)
    );
    add(Option.builder("h")
        .argName("help")
        .desc("Prints this help message")
        .longOpt("help")
    );
  }};

  public static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

  static {
    HELP_FORMATTER.setWidth(100);
  }

  // Instance variables
  private ActData actData;
  private String outputFilePath;

  public ConditionalReachabilityInterpreter(ActData actData, String outputFilePath) {
    this.actData = actData;
    this.outputFilePath = outputFilePath;
  }

  public static void main(String[] args) throws Exception {

    // Parse the command line options
    Options opts = new Options();
    for (Option.Builder b : OPTION_BUILDERS) {
      opts.addOption(b.build());
    }

    CommandLine cl = null;
    try {
      CommandLineParser parser = new DefaultParser();
      cl = parser.parse(opts, args);
    } catch (ParseException e) {
      System.err.format("Argument parsing failed: %s\n", e.getMessage());
      HELP_FORMATTER.printHelp(BingSearchRanker.class.getCanonicalName(), HELP_MESSAGE, opts, null, true);
      System.exit(1);
    }

    if (cl.hasOption("help")) {
      HELP_FORMATTER.printHelp(BingSearchRanker.class.getCanonicalName(), HELP_MESSAGE, opts, null, true);
      return;
    }

    String inputPath = cl.getOptionValue(OPTION_INPUT_ACT_FILEPATH);
    String outputPath = cl.getOptionValue(OPTION_OUTPUT_FILEPATH);

    ActData.instance().deserialize(inputPath);
    ActData actData = ActData.instance();
    ConditionalReachabilityInterpreter conditionalReachabilityInterpreter =
        new ConditionalReachabilityInterpreter(actData, outputPath);

    conditionalReachabilityInterpreter.run();
  }

  private void run() throws IOException {
    Set<Long> rootChemicals = new HashSet<>();
    Map<Long, Set<Long>> parentToChildrenAssociations = new HashMap<>();

    // Create parent to child associations
    for (Map.Entry<Long, Long> childIdToParentId : this.actData.getActTree().parents.entrySet()) {
      Long parentId = childIdToParentId.getValue();
      Long childId = childIdToParentId.getKey();

      // If the parentId is null, that means the node is one of the roots of the forest.
      if (parentId == null) {
        rootChemicals.add(childId);
        continue;
      }

      Set<Long> childIds = parentToChildrenAssociations.get(parentId);
      if (childIds == null) {
        childIds = new HashSet<>();
        parentToChildrenAssociations.put(parentId, childIds);
      }
      childIds.add(childId);
    }

    // Cache chem ids to their inchis
    Map<Long, String> chemIdToInchi = new HashMap<>();

    // Record the depth of each (Root,Descendant) pair combination
    Map<Pair<String, String>, Integer> rootDescendantPairToDepth = new HashMap<>();

    // Construct trees from the root chemicals
    Map<Long, Set<Long>> rootToSetOfDescendants = new HashMap<>();
    for (Long rootId : rootChemicals) {

      // Record depth of each tree
      int depth = 1;
      String rootInchi = db.readChemicalFromInKnowledgeGraph(rootId < 0 ? Reaction.reverseNegativeId(rootId) : rootId).getInChI();
      chemIdToInchi.put(rootId, rootInchi);

      Set<Long> children = parentToChildrenAssociations.get(rootId);
      while (children != null && children.size() > 0) {

        Set<Long> descendants = rootToSetOfDescendants.get(rootId);
        if (descendants == null) {
          descendants = new HashSet<>();
          rootToSetOfDescendants.put(rootId, descendants);
        }
        descendants.addAll(children);

        Set<Long> newChildren = new HashSet<>();
        for (Long child : children) {
          String childInchi = db.readChemicalFromInKnowledgeGraph(child < 0 ? Reaction.reverseNegativeId(child) : child).getInChI();
          chemIdToInchi.put(child, childInchi);

          rootDescendantPairToDepth.put(Pair.of(rootInchi, childInchi), depth);

          // If all the children of this child and add it to the new set of children
          Set<Long> res = parentToChildrenAssociations.get(child);
          if (res != null) {
            newChildren.addAll(res);
          }
        }

        children = newChildren;
        depth++;
      }
    }

    Map<String, String> childInchiToRootInchi = new HashMap<>();

    for (Map.Entry<Long, Set<Long>> entry : rootToSetOfDescendants.entrySet()) {
      String rootInchi = chemIdToInchi.get(entry.getKey());

      if (rootInchi.equals(BLACKLISTED_ROOT_INCHI)) {
        continue;
      }

      for (Long descendant : entry.getValue()) {
        childInchiToRootInchi.put(chemIdToInchi.get(descendant), rootInchi);
      }
    }

    Set<String> allInchis = new HashSet<>(chemIdToInchi.values());

    // Update the Bing Search results in the Installer database
    BingSearchRanker bingSearchRanker = new BingSearchRanker();
    bingSearchRanker.addBingSearchResults(allInchis);
    bingSearchRanker.writeBingSearchRanksAsTSVUsingConditionalReachabilityFormat(
        childInchiToRootInchi,
        rootDescendantPairToDepth,
        this.outputFilePath);
  }
}