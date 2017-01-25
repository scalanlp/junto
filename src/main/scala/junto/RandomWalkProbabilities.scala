package junto

/**
 * The three random walk probabilities associated with each node.
 */
case class RandomWalkProbabilities(
  continue: Double,
  inject: Double,
  abandon: Double
)
