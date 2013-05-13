package junto



/**
 * An object that provides common functions needed for using Junto.
 *
 */
object JuntoContext {

  import junto.graph.Vertex
  import scala.collection.JavaConversions._

  def getEstimatedLabels(vertex: Vertex, normalize: Boolean = true, onlyBetterThanDummy: Boolean = false) = {
    val fullScores = vertex.estimatedLabels
    val sorted = 
      (for (key <- fullScores.keys) 
       yield (key.toString, fullScores.get(key))
       ).sortBy(-_._2)

    val indexOfDummy = sorted.indexWhere(_._1 == "__DUMMY__")
    
    val noDummy = 
      if (onlyBetterThanDummy) sorted.take(indexOfDummy)
      else {
        val (front,back) = sorted.splitAt(indexOfDummy)
        front ++ (back.drop(1))
      }
    
    val finalized =
      if (!normalize) noDummy
      else {
        val sum = noDummy.unzip._2.sum
        for ((label,score) <- noDummy) yield (label,score/sum)
      }

    finalized
  }





}
