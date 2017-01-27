package junto.datasets

import java.io._
import junto._
import junto.io._
import junto.graph._

object MinimalJuntoExample {

  def main(args: Array[String]) {

    val conf = new JuntoOptions(args)

    val separator = if (conf.tabSeparated()) '\t' else ','
    val edges = getEdges(conf.edgeFile(), separator)
    val seeds = getLabels(conf.seedLabelFile(), separator)
    val evalLabels = getLabels(conf.seedLabelFile(), separator)

    val parameters = AdsorptionParameters(conf.mu1(), conf.mu2(), conf.mu3())
    val beta = 2.0
    val numIterations = conf.iterations()

    val (nodeNames, labelNames, estimatedLabels) =
      Junto(edges, seeds, parameters, numIterations, beta)

    // TODO: Add code to extract predictions and check output against gold.

  }

}
