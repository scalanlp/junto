package junto

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

class LabelPropGraph(
    val graph: Graph[Int, RWUnDiEdge],
    val nodeNames: Seq[String],
    val labelNames: Seq[String],
    val injectedLabels: Map[Int, Seq[Double]]
) {
  val numLabels = labelNames.length
  val nodes = graph.nodes
}

object LabelPropGraph {

  import junto.util.GrowableIndex

  // Create a graph
  def fromIndexed(
    labelNames: Seq[String],
    nodeNames: Seq[String],
    edgeSpecs: TraversableOnce[Edge[Int]],
    seedSpecs: TraversableOnce[LabeledNode[Int]],
    isDirected: Boolean = false
  ) = {

    val edges = for (e <- edgeSpecs.toList) yield {
      e.source ~ e.target ^ e.weight
    }

    val nodeIds = (0 until nodeNames.length).toSet
    val labelNamesWithDummy = DUMMY_LABEL +: labelNames
    val injectedLabels =
      seedSpecs.map(seed => (seed.node, 0.0 +: seed.weights)).toMap
    val graph = Graph.from(nodeIds.toIterable, edges.toIterable)

    println(s"Number of nodes: ${nodeNames.length}")
    println(s"Number of edges: ${graph.edges.size}")

    new LabelPropGraph(graph, nodeNames, labelNamesWithDummy, injectedLabels)
  }

  // Create a graph
  def apply(
    edgeSpecs: TraversableOnce[Edge[String]],
    seedSpecs: TraversableOnce[LabelSpec],
    isDirected: Boolean = false
  ) = {

    val nodeIndexer = new GrowableIndex[String]()
    val edges = for (e <- edgeSpecs.toList) yield nodeIndexer(e.source) ~ nodeIndexer(e.target) ^ e.weight

    // Maps from node names to their indices
    val nodeIndex = nodeIndexer.toMap

    // Access node names by their index
    val nodeNames = nodeIndex.toIndexedSeq.sortBy(_._2).unzip._1

    // Construct the graph
    val nodeIds = Stream.from(0).take(nodeNames.length)
    val graph = Graph.from(nodeIds, edges.toIterable)

    val labelIndexer = new GrowableIndex[String]()
    labelIndexer(DUMMY_LABEL)

    // Inject seed labels
    val (numLabels, injectedLabels) = {
      val injectedLabelBuilder = scala.collection.mutable.HashMap[Int, Map[Int, Double]]()
      for (seed <- seedSpecs; nodeId <- nodeIndex.get(seed.vertex)) {
        val labelId = labelIndexer(seed.label)
        if (!injectedLabelBuilder.isDefinedAt(nodeId))
          injectedLabelBuilder.put(nodeId, Map(labelId -> seed.score))
        else {
          val curr = injectedLabelBuilder(nodeId)
          injectedLabelBuilder(nodeId) = (curr ++ Map(labelId -> seed.score))
        }
      }
      val numLabels = labelIndexer.size
      val injectionsAsSequences = (for ((node, distmap) <- injectedLabelBuilder) yield {
        val distarray = Array.fill(numLabels)(0.0)
        for ((labelId, score) <- distmap) distarray(labelId) = score
        (node -> distarray.toIndexedSeq)
      }).toMap
      (numLabels, injectionsAsSequences)
    }

    // Access label names by their indices
    val labelNames = labelIndexer.toMap.toIndexedSeq.sortBy(_._2).unzip._1

    println(s"Number of nodes: ${nodeNames.length}")
    println(s"Number of edges: ${graph.edges.size}")

    new LabelPropGraph(graph, nodeNames, labelNames, injectedLabels)
  }

}
