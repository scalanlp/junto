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

import junto._
import junto.graph._

/**
 * Test Junto on a small network battling for Emacs vs VI supremacy!
 */
class EmacsViBattle extends FunSpec {

  // The e's are the Emacs evangelists, the v's are Vi evangelists. (The
  // n's are the newbies.)
  val edges = List(
    Edge("e1", "e2"), // Emacs folks stay together
    Edge("e1", "e3"),
    Edge("e2", "e3"),
    Edge("v1", "v2"), // Vi folks stay together
    Edge("v1", "v3"),
    Edge("v2", "v3"),
    Edge("n3", "n1"), // Newbie 3 connects to the other newbies.
    Edge("n3", "n2"),
    Edge("n4", "n1"), // Newbie 4 only connects to newbies.
    Edge("n4", "n2"),
    Edge("n4", "n3"),
    Edge("e1", "n1"), // Emacs folks preach to newbie 1.
    Edge("e2", "n1"),
    Edge("e3", "n1"),
    Edge("v1", "n2"), // Vi folks preach to newbie 2.
    Edge("v2", "n2"),
    Edge("v3", "n2"),
    Edge("e1", "n3"), // Two Emacs folks preach to newbie 3.
    Edge("e2", "n3"),
    Edge("e3", "n3"),
    Edge("v1", "n3") // One Vi person preaches to newbie 3.
  )

  val seeds = List(
    LabelSpec("e1", "emacs"),
    LabelSpec("e2", "emacs"),
    LabelSpec("e3", "emacs"),
    LabelSpec("v1", "vi"),
    LabelSpec("v2", "vi"),
    LabelSpec("v3", "vi")
  )

  describe("EmacsViBattle") {
    it("should propagate labels and check predicted labels and scores") {

      // Run label propagation.
      val (nodeNames, labelNames, estimatedLabels) = Junto(edges, seeds)

      // Associate the nodes with their max scoring labels.
      val nodesToLabels = (for {
        (nodeName, labelScores) <- nodeNames.zip(estimatedLabels)
        (maxLabel, maxLabelScore) = labelNames.zip(labelScores).maxBy(_._2)
      } yield (nodeName, (maxLabel, maxLabelScore))).toMap

      // Check output for n2.
      val (n2Label, n2Score) = nodesToLabels("n2")
      assert(n2Label == "vi")
      assert(math.round(n2Score * 1000) == 548)

      // Check output for n4.
      val (n4Label, n4Score) = nodesToLabels("n4")
      assert(n4Label == "emacs")
      assert(math.round(n4Score * 1000) == 300)

    }

  }

}
