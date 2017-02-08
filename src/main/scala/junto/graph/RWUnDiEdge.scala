package junto.graph

import scalax.collection.GraphEdge._

class RWUnDiEdge[N](
    nodes: Product, val rweight: Double
) extends UnDiEdge[N](nodes) with EdgeCopy[RWUnDiEdge] {

  override def copy[NN](newNodes: Product) =
    new RWUnDiEdge[NN](newNodes, rweight)

  override protected def attributesToString =
    s" ${RWUnDiEdge.prefix} $rweight"

}

object RWUnDiEdge {

  def apply(nodes: Product, rweight: Double) =
    new RWUnDiEdge[Int](nodes, rweight)

  def unapply(e: RWUnDiEdge[Int]): Option[(Int, Int, Double)] =
    if (e eq null) None else Some((e._1, e._2, e.rweight))

  val prefix = "^"

}
