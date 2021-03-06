##########################################################################
#                                                                        #
#  This file is part of the 20n/act project.                             #
#  20n/act enables DNA prediction for synthetic biology/bioengineering.  #
#  Copyright (C) 2017 20n Labs, Inc.                                     #
#                                                                        #
#  Please direct all queries to act@20n.com.                             #
#                                                                        #
#  This program is free software: you can redistribute it and/or modify  #
#  it under the terms of the GNU General Public License as published by  #
#  the Free Software Foundation, either version 3 of the License, or     #
#  (at your option) any later version.                                   #
#                                                                        #
#  This program is distributed in the hope that it will be useful,       #
#  but WITHOUT ANY WARRANTY; without even the implied warranty of        #
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         #
#  GNU General Public License for more details.                          #
#                                                                        #
#  You should have received a copy of the GNU General Public License     #
#  along with this program.  If not, see <http://www.gnu.org/licenses/>. #
#                                                                        #
##########################################################################

# LCMS visualisation single trace module

# Module Input function
lcmsSingleTraceInput <- function(id, label = "LCMS single trace") {
  # Create a namespace function using the provided id
  ns <- NS(id)
  
  tagList(
    h3("Scans selection"),
    textInput(ns("filename"), label = "File name", value = "Plate_jaffna3_B1_0815201601.nc"),
    sliderInput(ns("retention.time.range"), label = "Retention Time range",
                min = 0, max = 450, value = c(130, 160), step = 5),
    actionButton(ns("load"), "Refresh scans!", icon("magic"), width = "100%", 
                 style="color: #fff; background-color: #337ab7; border-color: #2e6da4"),
    mzScopeInput(ns("mz.scope")),
    plotParametersInput(ns("plot.parameters"))
  )
}

# module UI function
lcmsSingleTraceUI <- function(id) {
  ns <- NS(id)
  tagList(
    em("Disclaimer: the peak detection will only detect peaks of intensity more than 1e4 and can't (by design) detect more than 2 peaks."),
    h4("Target m/z value"),
    textOutput(ns("target.mz")),
    h4("Detected peaks"),
    tableOutput(ns("detected.peaks")),
    h4("3D scatterplot of the raw data"),
    lcmsPlotOutput(ns("plot"), height = "700px")
  )
}

# Module server function
lcmsSingleTrace <- function(input, output, session) {
  mzScopeId <- "mz.scope"
  mz.scope <- callModule(mzScope, mzScopeId)
  
  target.mz <- reactive({
    shiny::validate(
      need(length(mz.scope) == 3, "m/z scope input was not as expected")
    )
    target.mass <- mz.scope$target.monoisotopic.mass
    if (mz.scope$mode == "M (use mass as target mz value)") {
      target.mass
    } else {
      getIonMz(target.mass, mz.scope$mode)
    }
  })
  
  plot.data <- callModule(lcmsSingleTracePeaks, "trace", reactive(input$filename), 
                          reactive(input$retention.time.range), target.mz, 
                          reactive(mz.scope$mz.band.halfwidth), reactive(input$load))
  plot.parameters <- callModule(plotParameters, "plot.parameters")
  callModule(lcmsPlot, "plot", plot.data, plot.parameters)
  
  output$target.mz <- renderText({
    sprintf("Target m/z value (computed from input mass and mode): %s", target.mz())
  })
  
  output$detected.peaks <- renderTable({
    plot.data <- plot.data()
    detectPeaks(plot.data$peaks)
  }, digits = c(0, 6, 2, 0))
  
  observe({
    query <- parseQueryString(session$clientData$url_search)
    filename <- query[['filename']]
    mode <- query[['mode']]
    target.mz <- query[['target.mz']]
    mz.band <- query[['mz.band']]
    rt.min <- query[['rt.min']]
    rt.max <- query[['rt.max']]
    if (!is.null(filename)) {
      updateTextInput(session, "filename", value = filename)
    }
    if (!is.null(mode)) {
      updateSelectInput(session, paste(mzScopeId, "mode", sep = "-"), selected = mode)
    }
    if (!is.null(target.mz)) {
      target.mz <- as.double(target.mz)
      updateNumericInput(session, paste(mzScopeId, "target.monoisotopic.mass", sep = "-"), value = target.mz)
    }
    if (!is.null(mz.band)) {
      mz.band <- as.double(mz.band)
      updateNumericInput(session, paste(mzScopeId, "mz.band.halfwidth", sep = "-"), value = mz.band)
    }
    if (!is.null(rt.min) && !is.null(rt.max)) {
      rt.min <- as.double(rt.min)
      rt.max <- as.double(rt.max)
      updateSliderInput(session, "retention.time.range", value = c(rt.min, rt.max))      
    }
  })
}
