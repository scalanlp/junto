package junto

import org.rogach.scallop._

class JuntoOptions(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("scalanlp-junto 0.0.1")
  banner("""Minimalist Scala-based Label Propagation implementation""")
  footer("\nSee https://github.com/scalanlp/junto for more details")
  // The name of the file to save output to.
  lazy val outputFile = opt[String](descr="Output file where the predictions go")

  // The name of the file containing seed labels for some nodes.
  lazy val seedLabelFile = opt[String](descr = "File with seed information: <node_id>\t<label_id>\t<weight>")

  // The name of the file containing test labels for some nodes.
  lazy val evalLabelFile = opt[String](descr = "File in the same format as seedLabelFile, but used in evaluation")

  // The name of the file containing edges between nodes.
  lazy val edgeFile = opt[String](descr = "A file with a weighted graph in edge-factored form: <source>\t<target>\t<weight>")

  // If true, the input files have tabs. If false, they have commas.
  lazy val tabSeparated = opt[Boolean](default = Some(false), descr = "If set, assumes input files are tab-seperated instead of commas")

  // How many iterations to run.
  lazy val iterations = opt[Int](default = Some(20), descr = "Number of iterations/steps of the random walk")

  // Show verbose output, e.g. model training traces.
  lazy val verbose = opt[Boolean](default = Some(false), descr = "If set, this will enable traces for model training")

  // Seed label fidelity
  lazy val mu1 = opt[Double](default = Some(1.0), descr = "See the MAD paper for details")

  // Neighbor label fidelity
  lazy val mu2 = opt[Double](default = Some(1.0), descr = "See the MAD paper for details")

  // Dummy label weight (penalize high-degree nodes)
  lazy val mu3 = opt[Double](default = Some(1.0), descr = "See the MAD paper for details")

  verify()
}
