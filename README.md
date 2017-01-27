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
export PATH=$PATH:JUNTO_DIR/target/universal/stage/bin/
```

(Adapt as necessary for other shells.)

You can then run the main Junto app:

```
junto --help
```


## Trying it out


**NOTE**: This example is currently broken.

If you've managed to configure and build the system, you should be  able to go to $JUNTO_DIR/examples/simple and run:

```
$ junto config simple_config
```

Please look into the `examples/simple/simple_config` file for various options available. Sample (dummy) data is made available in the `examples/simple/data` directory.

A more extensive example on prepositional phrase attachment is in `src/test/scala/junto/prepattach.scala`. Look at that file for an example of using Junto as an API to construct a graph and run label propagation.

## Getting help

Documentation is admittedly thin. If you get stuck, you can get help by posting questions to [the junto-open group](http://groups.google.com/group/junto-open).

Also, if you find what you believe is a bug or have a feature request, you can create [an issue](https://github.com/scalanlp/junto/issues).
