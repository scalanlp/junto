package junto

import junto.io._
import junto.graph._
import junto.util.Evaluator

/**
 * Given the edge and seed descriptions, create the graph and run modified adsorption.
 */
object Junto {

  def main(args: Array[String]) {

    val conf = new JuntoOptions(args)

    val separator = if (conf.tabSeparated()) '\t' else ','
    val edges = getEdges(conf.edgeFile(), separator)
    val seeds = getLabels(conf.seedLabelFile(), separator)

    val parameters = AdsorptionParameters(conf.mu1(), conf.mu2(), conf.mu3())
    val beta = 2.0
    val numIterations = conf.iterations()

    val graph = LabelPropGraph(edges, seeds, false)

    val (nodeNames, labelNames, estimatedLabels) =
      Junto(graph, parameters, numIterations, beta)

    conf.evalLabelFile.toOption match {

      case Some(evalLabelFile) => {
        val evalLabelSequence = getLabels(evalLabelFile, skipHeader = true)

        val evalLabels = (for {
          LabelSpec(nodeName, label, strength) <- evalLabelSequence
        } yield (nodeName -> label)).toMap

        val (accuracy, meanReciprocalRank) =
          Evaluator.score(nodeNames, labelNames, estimatedLabels, "L1", evalLabels)

        println("Accuracy: " + accuracy)
        println("MRR: " + meanReciprocalRank)
      }
      case None => ; // ignore evaluation when evalLabelFile is not specified
    }

    // Output predictions if an output file is specified.
    // output predictions are comma seperated
    conf.outputFile.toOption match {
      case Some(outputFile) =>

        val out = createWriter(outputFile)

        out.write("id," + graph.labelNames.mkString(",") + "\n")
        for ((name, distribution) <- graph.nodeNames.zip(estimatedLabels))
          out.write(name + "," + distribution.mkString(",") + "\n")
        out.close
    }
  }

  def apply(
    graph: LabelPropGraph,
    parameters: AdsorptionParameters = AdsorptionParameters(),
    numIterations: Int = 10,
    beta: Double = 2.0,
    isDirected: Boolean = false
  ) = {
    val estimatedLabels = ModifiedAdsorption(graph, parameters, beta)(numIterations)
    (graph.nodeNames, graph.labelNames, estimatedLabels)
  }

}
