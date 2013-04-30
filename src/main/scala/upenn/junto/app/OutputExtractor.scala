package upenn.junto.app

/**
 * Copyright 2011 Jason Baldridge
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.io.Source
import java.io._
import com.typesafe.scalalogging.log4j.Logging


/** 
 * Read Junto output to get distributions for just words, and only
 * for labels that have higher probability than __DUMMY__.
 */
object OutputExtractor extends Logging {

  val NodeRE = """([^_]+)_(.+)""".r

  // Set up the options parser

  /**
   * Main method -- do the work. It might be good eventually to have the values for
   * each node type dumped to a node-specific file. 
   */
  def main(args: Array[String]) = {

    val opts = OutputExtractorOpts(args)

    val typesToExtract = opts.typeToExtract().toSet
    val doAllNodeTypes = typesToExtract.isEmpty

    val cutAtDummy = opts.cutAtDummy()

    val outputFile = new FileWriter(new File(opts.output()))
    
    for (line <- Source fromFile(new File(opts.input())) getLines) {
      
      val Array(nodename, gold, injected, estimated, isTestNode, mrr) = line split('\t')
      val NodeRE(nodetype,nodeval) = nodename
      
      if ( doAllNodeTypes || (typesToExtract(nodetype) && estimated != "") ) {
        val estimatedList = estimated split(" ")
        val (tags,probs) = 
          (for (i <- List.range(0,estimatedList.length,2)) 
             yield Pair(estimatedList(i),estimatedList(i+1).toDouble)) unzip
        
        var dummyIndex = tags indexOf("__DUMMY__")
	if ( !cutAtDummy || dummyIndex == -1 )
	  dummyIndex = tags length

        val activeTags = tags slice(0,dummyIndex)
        var activeProbs = probs slice(0,dummyIndex)

        activeProbs = activeProbs map{_/activeProbs.sum}
        
        outputFile write(nodeval)

        for ( (tag,prob) <- activeTags zip(activeProbs) )
          outputFile write(" " + tag + " " + prob)

        outputFile write("\n")
        
      }
    }
    outputFile.flush
    outputFile.close
  }
}




/**
 * An object that sets up the configuration for command-line options using
 * Scallop and returns the options, ready for use.
 *
 */
object OutputExtractorOpts {

  import org.rogach.scallop._
  
  def apply(args: Array[String]) = new ScallopConf(args) {
    banner("""
Read Junto output to get distributions for just words, and only for labels that have higher probability than __DUMMY__.

For usage see below.
	     """)
    val typeToExtract = 
      opt[List[String]]("type", descr="The type of node to extract information for. E.g. if you have WORD_hello, then use -t WORD to get the distribution for WORD_hello and not FOO_hello. If no type is given, then all node types are extracted.")


    val cutAtDummy = opt[Boolean]("dummy", descr="Truncate distributions at the __DUMMY__ label, and renormalize.")

    val input = trailArg[String]("input", descr="Input file to read")
    val output = trailArg[String]("output", descr="The file to save the predictions of the evaluation events.")


    val cost = opt[Double]("cost", default=Some(1.0), validate = (0<), descr="The cost parameter C. Bigger values means less regularization (more fidelity to the training set).")
  }
}
