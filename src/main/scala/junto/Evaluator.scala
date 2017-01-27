package junto

import scala.collection.mutable.ListBuffer

object Evaluator {

  /**
   * Returns the accuracy and average mean reciprocal rank for given
   * set of vertices and their corresponding gold labels.
   */
  def score(
    nodeNames: Seq[String],
    labelNames: Seq[String],
    estimatedLabels: Seq[Seq[Double]],
    defaultLabel: String,
    evalLabels: Map[String, String]
  ): (Double, Double) = {

    val pairedPredictionAndGold = ListBuffer.empty[(String, String)]
    val reciprocalRanks = ListBuffer.empty[Double]

    for {
      (nodeName, labelScores) <- nodeNames.zip(estimatedLabels)
      goldLabel <- evalLabels.get(nodeName)
      (maxLabel, maxLabelScore) = labelNames.zip(labelScores).maxBy(_._2)
    } {
      pairedPredictionAndGold.append((maxLabel, goldLabel))
      val reciprocalRank = getReciprocalRank(labelNames, labelScores, goldLabel)
      reciprocalRanks.append(reciprocalRank)
    }

    val avgMrr = reciprocalRanks.sum / reciprocalRanks.length
    val numCorrect = pairedPredictionAndGold.filter { case (p, g) => p == g }.length
    val accuracy = numCorrect / pairedPredictionAndGold.length.toDouble

    (accuracy, avgMrr)
  }

  /**
   * Get the rank of a label in the predicted scores.
   */
  private def getReciprocalRank(
    labelNames: Seq[String],
    scores: Seq[Double],
    queryLabel: String,
    pruneDummy: Boolean = true
  ) = {
    val sortedLabels =
      labelNames.zip(scores).sortBy(-_._2).filter(_._1 != DUMMY_LABEL).unzip._1
    val queryLabelRank = sortedLabels.indexWhere(queryLabel==)
    if (queryLabelRank > -1) 1.0 / (queryLabelRank + 1) else 0
  }

}
