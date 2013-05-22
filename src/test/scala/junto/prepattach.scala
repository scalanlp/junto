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
package junto

import org.scalatest.FunSpec
import io.Source

import java.io._
import junto.app._
import junto.config._

class PrepAttachSpec extends FunSpec {

  describe("Prepositional Phrase Attachment") {
    it ("should construct the graph, propagate labels, and evaluate") {

      // Convert files to PrepInfo lists
      val ppadir = "/data/ppa"
      val trainInfo = getInfo(ppadir+"/training")
      val devInfo = getInfo(ppadir+"/devset")
      val testInfo = getInfo(ppadir+"/test")

      // Create the edges and seeds
      val edges = createEdges(trainInfo) ++ createEdges(devInfo) ++ createEdges(testInfo)
      val seeds = createLabels(trainInfo)
      val gold = createLabels(devInfo)

      // Create the graph and run label propagation
      val graph = GraphBuilder(edges, seeds, gold)
      JuntoRunner(graph)
    }

  }

  def createEdges (info: Seq[PrepInfo]): Seq[Edge] = {
    (for (item <- info) yield
      Seq(Edge(item.idNode, item.verbNode),
          Edge(item.idNode, item.nounNode),
          Edge(item.idNode, item.prepNode),
          Edge(item.idNode, item.pobjNode))
     ).flatten
  }

  def createLabels (info: Seq[PrepInfo]): Seq[Label] =
    info.map(item => Label(item.idNode, item.label))

  def getInfo(inputFile: String) = io.Source
    .fromInputStream(this.getClass.getResourceAsStream(inputFile))
    .getLines
    .toList
    .map(PrepInfoFromLine)
  
}

case class PrepInfo (
  id: String, verb: String, noun: String, prep: String, pobj: String, label: String) {

  // Helpers for creating nodes of different types.
  def node(feature: String, nodeType: String) = s"$nodeType::$feature"
  lazy val idNode = node(id,"ID")
  lazy val verbNode = node(verb,"VERB")
  lazy val nounNode = node(noun,"NOUN")
  lazy val prepNode = node(prep,"PREP")
  lazy val pobjNode = node(pobj,"POBJ")

}

object PrepInfoFromLine extends (String => PrepInfo) {
  def apply (line: String) = {
    val Array(id, verb, noun, prep, pobj, label) = line.split(" ")
    PrepInfo(id, verb, noun, prep, pobj, label)
  }
}

