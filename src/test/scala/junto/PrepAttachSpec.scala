/**
 * Copyright 2013 ScalaNLP
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
package junto

import org.scalatest.FunSpec

import java.io._

import junto._
import junto.graph._
import junto.util.Evaluator
import collection.JavaConversions._

/**
 * Test Junto on the Prepositional Phrase Attachment Dataset from:
 *
 * Ratnaparkhi, Reynar, & Roukos. "A Maximum Entropy Model for Prepositional
 *   "Phrase Attachment". ARPA HLT 1994.
 */
class PrepAttachSpec extends FunSpec {

  import PrepAttachSpec._

  describe("Prepositional Phrase Attachment") {
    it("should construct the graph, propagate labels, and evaluate") {

      // Convert files to PrepInfo lists
      val ppadir = "/data/ppa"
      val trainInfo = getInfo(ppadir + "/training")
      val devInfo = getInfo(ppadir + "/devset", trainInfo.length)
      val testInfo = getInfo(ppadir + "/test", trainInfo.length + devInfo.length)

      // Create the edges and seeds
      val edges = createEdges(trainInfo) ++ createEdges(devInfo) ++ createEdges(testInfo)
      val seeds = createLabels(trainInfo)
      val devLabels = (for {
        LabelSpec(nodeName, label, strength) <- createLabels(devInfo)
      } yield (nodeName -> label)).toMap

      // Create the graph and run label propagation
      val (nodeNames, labelNames, estimatedLabels) = Junto(edges, seeds)

      val (accuracy, meanReciprocalRank) =
        Evaluator.score(nodeNames, labelNames, estimatedLabels,
          "V", devLabels)

      //println("Accuracy: " + accuracy)
      //println("MRR: " + meanReciprocalRank)

      assert(math.round(accuracy * 1000) == 782)
      assert(math.round(meanReciprocalRank * 1000) == 897)
    }

  }

  // Edges connect the instance nodes to their feature nodes (e.g.,
  // "VERB::bought").
  def createEdges(info: Seq[PrepInfo]): Seq[Edge[String]] = {
    (for (item <- info) yield Seq(
      Edge(item.idNode, item.verbNode),
      Edge(item.idNode, item.nounNode),
      Edge(item.idNode, item.prepNode),
      Edge(item.idNode, item.pobjNode)
    )).flatten
  }

  // Pull out label information for each instance node.
  def createLabels(info: Seq[PrepInfo]): Seq[LabelSpec] =
    info.map(item => LabelSpec(item.idNode, item.label))

  // Read the info for each instance and create a PrepInfo object.
  def getInfo(inputFile: String, startIndex: Int = 0) = {
    val prepAttachStream = this.getClass.getResourceAsStream(inputFile)
    val info =
      scala.io.Source.fromInputStream(prepAttachStream).getLines.toList

    for {
      (line, id) <- info.zip(Stream.from(startIndex))
    } yield PrepInfoFromLine(id, line)

  }

}

object PrepAttachSpec {

  // A case class to store everything for an instance.
  case class PrepInfo(
      id: String, verb: String, noun: String, prep: String, pobj: String, label: String
  ) {

    // Helpers for creating nodes of different types.
    lazy val idNode = VertexName(id, "ID").toString
    lazy val verbNode = VertexName(verb, "VERB").toString
    lazy val nounNode = VertexName(noun, "NOUN").toString
    lazy val prepNode = VertexName(prep, "PREP").toString
    lazy val pobjNode = VertexName(pobj, "POBJ").toString

  }

  // Read a PrepInfo object from a line.
  object PrepInfoFromLine extends ((Int, String) => PrepInfo) {
    def apply(id: Int, line: String) = {
      val Array(sentenceId, verb, noun, prep, pobj, label) = line.split(" ")
      PrepInfo(id.toString, verb, noun, prep, pobj, label)
    }
  }

}
