package junto



/**
 * An object that provides common functions needed for using Junto.
 *
 */
object JuntoContext {

  import junto.graph.Vertex
  import junto.config.Label
  import scala.collection.JavaConversions._


  /**
   * After running LP, each vertex has an estimated set of scores for each
   * label. This method extracts those in a convenient manner by
   *   (a) excising the DUMMY label
   *   (b) getting only the labels better than DUMMY if requested (false by default)
   *   (c) normalizing the scores of all labels extracted (true by default)
   */
  def getEstimatedLabels(
    vertex: Vertex, cutAtDummy: Boolean = false, normalize: Boolean = true
  ): Seq[(String,Double)] = {
    val sorted = sortLabels(vertex.estimatedLabels)
    val noDummy = removeDummy(sorted, cutAtDummy)
    if (!normalize) noDummy else normalizeScores(noDummy)
  }

  def getVertices(graph: junto.graph.Graph, vertexIds: Seq[String]) =
    for (id <- vertexIds) yield graph.vertices.get(id)

  /**
   * After running LP and given a vertex, sort the labels, remove the DUMMY label,
   * take the top N labels, and normalize the scores of those top N.
   */ 
  def getTopLabels(vertex: Vertex, numToKeep: Int = 1): Seq[(String,Double)] =
    normalizeScores(removeDummy(sortLabels(vertex.estimatedLabels)).take(numToKeep))

  def topLabel(vertex: Vertex, defaultLabel: String): String = {
    val labels = removeDummy(sortLabels(vertex.estimatedLabels))
    if (labels.length == 0) defaultLabel else labels.head._1
  }

  /**
   * Returns the accuracy of the graph for a given set of vertices and their
   * corresponding gold labels.
   */ 
  //def score(evalLabels: Seq[Label], graph: junto.graph.Graph, defaultLabel: String): Double = {
  //  val evalVertexIds = evalLabels.map(_.vertex)
  //  val predictions = 
  //    getVertices(graph, evalVertexIds).map(vertex => topLabel(vertex, defaultLabel))
  //  val paired = predictions.zip(evalLabels.map(_.label))
  //  val numCorrect = paired.filter { case(p,g) => p==g }.length
  //  numCorrect/paired.length.toDouble
  //}

  def score(
    evalLabels: Seq[Label], graph: junto.graph.Graph, defaultLabel: String): (Double,Double) = {
    val evalIds = evalLabels.map(_.vertex)
    val predictions = getVertices(graph, evalIds).map(vertex => getEstimatedLabels(vertex, normalize=false))
    val best = predictions.map(vlabels => if (vlabels.length>0) vlabels.head._1 else defaultLabel)

    val vertexMrr = for ((sortedLabels, gold) <- predictions.zip(evalLabels.map(_.label))) yield {
      val goldRank = sortedLabels.indexWhere(_._1 == gold)
      if (goldRank > -1) 1.0/(goldRank + 1.0) else 0
    }

    val avgMrr = vertexMrr.sum/evalIds.length
    val paired = best.zip(evalLabels.map(_.label))
    val numCorrect = paired.filter { case(p,g) => p==g }.length
    val accuracy = numCorrect/paired.length.toDouble

    (accuracy, avgMrr)
  }


  /**
   * Sort the labels contained in a Trove map, and return as a Seq of (String, Double)
   * tuples.
   */ 
  import gnu.trove.map.hash.TObjectDoubleHashMap
  def sortLabels(labelMap: TObjectDoubleHashMap[String]): Seq[(String,Double)] =
    (for (key <- labelMap.keys) yield (key.toString, labelMap.get(key))).sortBy(-_._2)


  private def removeDummy(labels: Seq[(String,Double)], cutAtDummy: Boolean = false) = {
    val indexOfDummy = labels.indexWhere(_._1 == "__DUMMY__")
    if (cutAtDummy) labels.take(indexOfDummy)
    else {
      val (front,back) = labels.splitAt(indexOfDummy)
      front ++ (back.drop(1))
    }
  }

  private def normalizeScores(labels: Seq[(String,Double)]) = {
    val sum = labels.unzip._2.sum
    for ((label,score) <- labels) yield (label,score/sum)
  }

}
