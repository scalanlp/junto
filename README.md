# The Junto Label Propagation Toolkit

Label propagation is a popular approach to semi-supervised learning in which nodes in a graph represent features or instances to be classified and the labels for a classification task are pushed around the graph from nodes that have initial label assignments to their neighbors and beyond.

This package provides an implementation of the Adsorption and  Modified Adsorption (MAD) algorithms described in the following papers.

* Talukdar et al. [Weakly Supervised Acquisition of Labeled Class Instances using Graph Random Walks](http://aclweb.org/anthology/D/D08/D08-1061.pdf). EMNLP-2008.
* Talukdar and Crammer (2009). [New Regularized Algorithms for Transductive Learning](http://talukdar.net/papers/adsorption_ecml09.pdf). ECML-PKDD 2009.
* Talukdar and Pereira (2010). [Experiments in Graph-based Semi-Supervised Learning Methods for Class-Instance Acquisition](http://aclweb.org/anthology/P/P10/P10-1149.pdf). Partha Pratim Talukdar, Fernando Pereira, ACL 2010

Please cite Talukdar and Crammer (2009) and/or Talukdar and Pereira (2010) if you use this library.

Additionally, LP_ZGL, one of the first label propagation algorithms is also implemented.

* Xiaojin Zhu and Zoubin Ghahramani. Learning from labeled and unlabeled data with label propagation.  Technical Report CMU-CALD-02-107, Carnegie Mellon University, 2002.

This file contains the configuration and build instructions. 

Why is the toolkit named Junto? The core code was written while Partha Talukdar was at the University of Pennsylvania, and Ben Franklin (the founder of the University) established [a club called Junto](http://en.wikipedia.org/wiki/Junto_(club)) that provided a structured forum for him and his friends to debate and exchange knowledge. This has a nice parallel with how label propagation works: nodes are connected and influence each other based on their connections. Also "junto" means "along" and "together" in a number of Latin languages, and carries the connotation of cooperation---also a good fit for label propagation.

## Using Junto

In SBT:

    libraryDependencies += "org.scalanlp" % "junto" % "1.5.0"

In Maven:

    <dependency>
       <groupId>org.scalanlp</groupId>
       <artifactId>junto</artifactId>
       <version>1.5.0</version>
    </dependency>


## Requirements

* Version 1.6 of the Java 2 SDK (http://java.sun.com)


## Configuring your environment variables

The easiest thing to do is to set the environment variables JAVA_HOME and JUNTO_DIR to the relevant locations on your system. Set JAVA_HOME to match the top level directory containing the Java installation you want to use.

For example, on Windows:

```
C:\> set JAVA_HOME=C:\Program Files\jdk1.5.0_04
```

or on Unix:

```
% setenv JAVA_HOME /usr/local/java
  (csh)
> export JAVA_HOME=/usr/java
  (ksh, bash)
```

Next, likewise set JUNTO_DIR to be the top level directory where you unzipped the download. In Unix, type 'pwd' in the directory where this file is and use the path given to you by the shell as JUNTO_DIR.  You can set this in the same manner as for JAVA_HOME above.

Next, add the directory `JUNTO_DIR/bin` to your path. For example, you can set the path in your `.bashrc` file as follows:

```
export PATH="$PATH:$JUNTO_DIR/bin"
```

Once you have taken care of these three things, you should be able to
build and use the Junto Library.

## Building the system from source

Junto uses SBT (Simple Build Tool) with a standard directory structure.  To build Junto, go to JUNTO_DIR and type:

```
$ bin/build update compile
```

This will compile the source files and put them in ./target/classes. If this is your first time running it, you will see messages about Scala being dowloaded -- this is fine and expected. Once that is over, the Junto code will be compiled.

To try out other build targets, do:

```
$ bin/build
```

This will drop you into the SBT interface.  Many [other build targets](https://github.com/harrah/xsbt/wiki/Getting-Started-Running) are supported.

If you wish to use Junto as an API, you can create a self-contained
assembly jar by using the "assembly" action in SBT. Also, you can just do:

```
$ bin/build assembly
```

Maven style dependencies are [coming soon](https://github.com/scalanlp/junto/issues/1).


## Trying it out

If you've managed to configure and build the system, you should be  able to go to $JUNTO_DIR/examples/simple and run:

```
$ junto config simple_config
```

Please look into the `examples/simple/simple_config` file for various options available. Sample (dummy) data is made available in the  examples/simple/data directory.

A more extensive example on prepositional phrase attachment is in `examples/ppa`. See the README in that directory for more details.

## Hadoop

If you are interested in trying out the Hadoop implementations, then please look into `examples/hadoop/README`.


## Getting help


Documentation is admittedly thin. If you get stuck, you can get help by posting questions to [the junto-open group](http://groups.google.com/group/junto-open). 

Also, if you find what you believe is a bug or have a feature request, you can create [an issue](https://github.com/scalanlp/junto/issues).
