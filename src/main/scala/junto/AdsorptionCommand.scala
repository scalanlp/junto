package junto

import org.rogach.scallop._

class AdsorptionCommand(arguments: Seq[String]) extends ScallopConf(arguments) {

  // The name of the directory containing input graph files.
  lazy val inputDir = opt[String]()

  // The file prefix for the input graph files we are working with.
  lazy val prefix = opt[String]()

  // The name of the file to save output to.
  lazy val outputFile = opt[String]()

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
