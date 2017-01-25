import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

package object junto {

  /**
   * Returns the accuracy and average mean reciprocal rank of the graph for a given
   * set of vertices and their corresponding gold labels.
   */
  def score(
    evalLabels: Seq[LabelSpec], nodeNames: Seq[String], labelNames: Seq[String],
    estimatedLabels: Seq[Seq[Double]], defaultLabel: String
  ): (Double, Double) = {

    val nodeIndex = nodeNames.zipWithIndex.toMap
    val evalIds = evalLabels.map(_.vertex).map(nodeIndex)

    // Zip the label names with the values for each eval node, drop the dummy, and sort.
    val predictions = for (id <- evalIds) yield labelNames.zip(estimatedLabels(id)).drop(1).sortBy(-_._2)

    val best = for (nodePrediction <- predictions) yield {
      val (highestLabel, highestScore) = nodePrediction.head
      if (highestScore > 0.0) highestLabel else defaultLabel
    }

    val goldLabels = evalLabels.map(_.label)

    val vertexMrr = for ((sortedLabels, goldForNode) <- predictions.zip(goldLabels)) yield {
      val goldRank = sortedLabels.indexWhere(_._1 == goldForNode)
      if (goldRank > -1) 1.0 / (goldRank + 1.0) else 0.0
    }

    val avgMrr = vertexMrr.sum / evalIds.length
    val paired = best.zip(goldLabels)
    val numCorrect = paired.filter { case (p, g) => p == g }.length
    val accuracy = numCorrect / paired.length.toDouble

    (accuracy, avgMrr)
  }

  implicit def edge2RWUnDiEdgeAssoc[N](e: UnDiEdge[N]) =
    new RWUnDiEdgeAssoc[N](e)

  protected[junto] def otherNode(edge: RWUnDiEdge[Int], node: Int) =
    if (edge._1 == node) edge._2 else edge._1

  protected[junto] def differenceNorm2Squared(length: Int)(
    dist1: Seq[Double], dist2: Seq[Double]
  ) = {
    var index = 0
    var squaredDiffsSum = 0.0
    while (index < length) {
      val diff = dist1(index) - dist2(index)
      squaredDiffsSum += diff * diff
      index += 1
    }
    math.sqrt(squaredDiffsSum)
  }

  protected[junto] lazy val DUMMY_LABEL = "__DUMMY__"

}
