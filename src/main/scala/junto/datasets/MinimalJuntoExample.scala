package junto.datasets

import java.io._
import junto._
import junto.io._
import junto.graph._

object MinimalJuntoExample {

  def main(args: Array[String]) {

    val conf = new JuntoOptions(args)

    val inputDir = new File(conf.inputDir())

    val seedLabelFile = new File(conf.seedLabelFile())
    val evalLabelFile = new File(conf.evalLabelFile())

    val edges = getEdges(conf.edgeFile())
    val seeds = getLabels(conf.seedLabelFile())
    val evalLabels = getLabels(conf.seedLabelFile())

    val parameters = AdsorptionParameters(conf.mu1(), conf.mu2(), conf.mu3())
    val beta = 2.0
    val numIterations = conf.iterations()

    val (nodeNames, labelNames, estimatedLabels) =
      Junto(edges, seeds, parameters, numIterations, beta)

    // TODO: Add code to extract predictions and check output against gold.

  }

}
