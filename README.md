# The Junto Label Propagation Toolkit

Label propagation is a popular approach to semi-supervised learning in which nodes in a graph represent features or instances to be classified and the labels for a classification task are pushed around the graph from nodes that have initial label assignments to their neighbors and beyond.

This package provides an implementation of the Adsorption and  Modified Adsorption (MAD) algorithms described in the following papers.

* Talukdar et al. [Weakly Supervised Acquisition of Labeled Class Instances using Graph Random Walks](http://aclweb.org/anthology/D/D08/D08-1061.pdf). EMNLP-2008.
* Talukdar and Crammer (2009). [New Regularized Algorithms for Transductive Learning](http://talukdar.net/papers/adsorption_ecml09.pdf). ECML-PKDD 2009.
* Talukdar and Pereira (2010). [Experiments in Graph-based Semi-Supervised Learning Methods for Class-Instance Acquisition](http://aclweb.org/anthology/P/P10/P10-1149.pdf). Partha Pratim Talukdar, Fernando Pereira, ACL 2010

Please cite Talukdar and Crammer (2009) and/or Talukdar and Pereira (2010) if you use this library.

Why is the toolkit named Junto? The core code was written while Partha Talukdar was at the University of Pennsylvania, and Ben Franklin (the founder of the University) established [a club called Junto](http://en.wikipedia.org/wiki/Junto_(club)) that provided a structured forum for him and his friends to debate and exchange knowledge. This has a nice parallel with how label propagation works: nodes are connected and influence each other based on their connections. Also "junto" means "along" and "together" in a number of Latin languages, and carries the connotation of cooperation---also a good fit for label propagation.

## What's inside

The latest stable release of Junto is 1.6.0. The current version is a drastically simplified code base that is pure Scala and has few dependencies. It is currently undergoing substantially changes while we work toward a 2.0 version.

See the [CHANGELOG](https://github.com/scalanlp/junto/wiki/CHANGELOG) for changes in previous versions.

## Using Junto

**NOTE**: This is for a different version and very different API than the code that is in the repository at present.

In SBT:

    libraryDependencies += "org.scalanlp" % "junto" % "1.6.0"

In Maven:

    <dependency>
       <groupId>org.scalanlp</groupId>
       <artifactId>junto</artifactId>
       <version>1.6.0</version>
    </dependency>


## Requirements

* Version 1.7 of the Java 2 SDK (http://java.sun.com)

## Building the system from source

First, set JAVA_HOME to match the top level directory containing the Java installation you want to use.  Junto Library using `sbt`.

Junto uses SBT (Simple Build Tool) with a standard directory structure.  To build Junto, install `sbt` and go to JUNTO_DIR and type:

```
$ sbt compile
```

This will compile the source files and put them in ./target/classes. If this is your first time running it, you will see messages about Scala being dowloaded -- this is fine and expected. Once that is over, the Junto code will be compiled.

To try out other build targets, do:

```
$ sbt
```

This will drop you into the SBT interface.  Many [other build targets](https://github.com/harrah/xsbt/wiki/Getting-Started-Running) are supported.

For command line use, compile the package using `sbt stage` and add the following to your `.bash_profile`:

```
export JUNTO_DIR=<your-path-to-junto>
export PATH=$PATH:$JUNTO_DIR/target/universal/stage/bin/
```

(Adapt as necessary for other shells.)

You can then run the main Junto app:

```
junto --help
```

## Trying it out

If you've managed to configure and build the system, you should be able to go to $JUNTO_DIR/examples/minimal and run the following command and get the same output:

```
$ junto --tab-separated --edge-file edges.minimal.tsv --seed-label-file seed_labels.minimal.tsv --eval-label-file test_labels.minimal.tsv --output-file output_predictions.minimal.csv  --mu1 1.0 --mu2 .01 --mu3 .01
Number of nodes: 4
Number of edges: 5
1: delta:0.15833908915683234 obj:0.08013428514645574
2: delta:0.04759638215742747 obj:0.07677391666501679
3: delta:0.01850404042378978 obj:0.0761157867035102
4: delta:0.0072043634149645125 obj:0.07579866440446062
5: delta:0.0028034124542580387 obj:0.07571427318621111
6: delta:0.0010914904258965965 obj:0.07566963785749868
7: delta:4.2476484252594213E-4 obj:0.07565703078738015
8: delta:1.653845297211106E-4 obj:0.07565041388419924
9: delta:6.436571964520653E-5 obj:0.07564848037536726
10: delta:2.506173650420982E-5 obj:0.0756474905058922
11: delta:9.754328923841513E-6 obj:0.0756471932173569
TIME: 0.039
Accuracy: 1.0
MRR: 1.0
```

Check that the output predictions are the same by doing a diff against the reference output.

```
$ diff reference.output_predictions.minimal.csv output_predictions.minimal.csv
```

A more extensive example on prepositional phrase attachment is in `src/test/scala/junto/PrepAttachSpech.scala`. Look at that file for an example of using Junto as an API to construct a graph and run label propagation.

## Getting help

Documentation is admittedly thin. If you get stuck, you can get help by posting questions to [the junto-open group](http://groups.google.com/group/junto-open).

Also, if you find what you believe is a bug or have a feature request, you can create [an issue](https://github.com/scalanlp/junto/issues).
