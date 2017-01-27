package junto

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

package object graph {
  case class Node(ntype: String = "default", name: String) {
    lazy val key = s"$ntype:$name".replaceAll(",", "COMMA")
  }

  case class Edge[EdgeType](source: EdgeType, target: EdgeType, weight: Double = 1.0) {
    override def toString = source + "\t" + target + "\t" + weight
  }

  case class LabelSpec(vertex: String, label: String, score: Double = 1.0) {
    override def toString = vertex + "\t" + label + "\t" + score
  }

  case class LabeledNode[NodeType](node: NodeType, weights: Seq[Double]) {
    lazy val toCsv = node + "," + weights.mkString(",")
  }

  implicit def edge2RWUnDiEdgeAssoc[N](e: UnDiEdge[N]) =
    new RWUnDiEdgeAssoc[N](e)

  protected[junto] def otherNode(edge: RWUnDiEdge[Int], node: Int) =
    if (edge._1 == node) edge._2 else edge._1

}
