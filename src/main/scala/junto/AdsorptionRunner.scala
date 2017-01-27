package junto

import java.io._
import junto.io._

/**
 * Given the edge and seed descriptions, create the graph and run modified adsorption.
 */
object AdsorptionRunner {

  def main(args: Array[String]) {

    val conf = new AdsorptionCommand(args)

    val inputDir = new File(conf.inputDir())
    val prefix = conf.prefix()

    val labelNameFile = new File(inputDir, s"$prefix-labels.csv.gz")
    val nodeFile = new File(inputDir, s"$prefix-nodes.csv.gz")
    val edgeFile = new File(inputDir, s"$prefix-edges.csv.gz")

    val labelNames = getSource(labelNameFile).getLines.toSeq

    val nodeInfo = collection.mutable.ListBuffer.empty[(Int, String)]
    val seedSpecs = collection.mutable.ListBuffer.empty[LabeledNode[Int]]
    for {
      line <- getSource(nodeFile).getLines
      idString :: name :: nodetype :: weights = line.split(",").toList
      id = idString.toInt
    } {
      nodeInfo += ((id, name))
      seedSpecs += LabeledNode(id, weights.map(_.toDouble))
    }

    val nodeNames = nodeInfo.toSeq.sortBy(_._1).unzip._2

    val edgeSpecs = (for {
      line <- getSource(edgeFile).getLines
      Array(source, target, weight) = line.split(",")
    } yield Edge(source.toInt, target.toInt, weight.toDouble)).toSeq

    val lpgraph = LabelPropGraph.fromIndexed(
      labelNames, nodeNames, edgeSpecs, seedSpecs.toSeq
    )

    val parameters = AdsorptionParameters(conf.mu1(), conf.mu2(), conf.mu3())
    val beta = 2.0
    val numIterations = conf.iterations()
    val estimatedLabels = ModifiedAdsorption(lpgraph, parameters, beta)(numIterations)

    val out = createWriter(conf.outputFile())

    out.write("id," + lpgraph.labelNames.mkString(",") + "\n")
    for ((name, distribution) <- lpgraph.nodeNames.zip(estimatedLabels))
      out.write(name + "," + distribution.mkString(",") + "\n")
    out.close
  }

  def apply(
    edges: TraversableOnce[Edge[String]],
    seedSpecs: TraversableOnce[LabelSpec],
    parameters: AdsorptionParameters = AdsorptionParameters(),
    numIterations: Int = 10,
    beta: Double = 2.0,
    isDirected: Boolean = false
  ) = {
    val lpgraph = LabelPropGraph(edges, seedSpecs, isDirected)
    val estimatedLabels = ModifiedAdsorption(lpgraph, parameters, beta)(numIterations)
    (lpgraph.nodeNames, lpgraph.labelNames, estimatedLabels)
  }

}
