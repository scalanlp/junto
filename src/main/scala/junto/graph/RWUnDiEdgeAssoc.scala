package junto.graph

import scalax.collection.GraphEdge._

final class RWUnDiEdgeAssoc[N](val e: UnDiEdge[N]) {

  def ^(rweight: Double) =
    new RWUnDiEdge[N](e.nodes, rweight)

}
