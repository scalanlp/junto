package junto

import org.rogach.scallop._

class JuntoOptions(arguments: Seq[String]) extends ScallopConf(arguments) {

  // The name of the file to save output to.
  lazy val outputFile = opt[String]()

  // The name of the file containing seed labels for some nodes.
  lazy val seedLabelFile = opt[String]()

  // The name of the file containing test labels for some nodes.
  lazy val evalLabelFile = opt[String]()

  // The name of the file containing edges between nodes.
  lazy val edgeFile = opt[String]()

  // If true, the input files have tabs. If false, they have commas.
  lazy val tabSeparated = opt[Boolean](default = Some(false))

  // How many iterations to run.
  lazy val iterations = opt[Int](default = Some(20))

  // Show verbose output, e.g. model training traces.
  lazy val verbose = opt[Boolean](default = Some(false))

  // Seed label fidelity
  lazy val mu1 = opt[Double](default = Some(1.0))

  // Neighbor label fidelity
  lazy val mu2 = opt[Double](default = Some(1.0))

  // Dummy label weight (penalize high-degree nodes)
  lazy val mu3 = opt[Double](default = Some(1.0))

  verify()
}
