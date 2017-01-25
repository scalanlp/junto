package junto

/**
 * A simple example for setting up a small graph with two labels and running MAD.
 * Should be turned into a unit test.
 */
object LabelPropExample {

  import junto._

  def main(args: Array[String]) {

    val edges = List(
      Edge("a1", "9"),
      Edge("a2", "9"),
      Edge("a1", "42"),
      Edge("a2", "42"),
      Edge("a3", "42"),
      Edge("a1", "7"),
      Edge("a2", "7"),
      Edge("a3", "7"),
      Edge("b2", "42"),
      Edge("b1", "7"),
      Edge("b2", "7"),
      Edge("b3", "7"),
      Edge("b1", "5"),
      Edge("b2", "5"),
      Edge("b3", "5")
    )

    val labels = List(
      LabelSpec("a1", "A"),
      LabelSpec("a2", "A"),
      LabelSpec("a3", "A"),
      LabelSpec("b1", "B"),
      LabelSpec("b2", "B"),
      LabelSpec("b3", "B")
    )

    val aparams = AdsorptionParameters(mu1 = 100.0, mu3 = 0.01)
    val (nodeNames, labelNames, estimatedLabels) = AdsorptionRunner(edges, labels, aparams)
    labelNames.foreach(println)
    nodeNames.zip(estimatedLabels).foreach(println)
  }

}
