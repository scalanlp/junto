package junto

import junto.graph._

/**
 * A class that takes all the information needed to run the modified adsorption
 * algorithm and provides an apply method that actually runs it and returns the
 * estimated labels at all the nodes.
 */
class ModifiedAdsorption(
    lpgraph: LabelPropGraph,
    parameters: AdsorptionParameters,
    rwprobs: Seq[RandomWalkProbabilities],
    normalizationConstants: Seq[Double]
) {

  private[this] val numLabels = lpgraph.numLabels
  private[this] val injectedLabels = lpgraph.injectedLabels

  // A function to calculate the norm2squared difference between two label distributions.
  private[this] val squaredNormDiff = differenceNorm2Squared(numLabels)_

  /**
   * Run the label propagation algorithm.
   */
  def apply(maxIter: Int, tolerance: Double = .00001) = {
    val numNodes = lpgraph.nodes.size
    val zeros = Seq.fill(numLabels)(0.0)
    var estimatedLabels = for (i <- (0 until numNodes)) yield if (injectedLabels.isDefinedAt(i)) injectedLabels(i) else zeros

    val globalStartTime = System.currentTimeMillis

    var deltaLabelDiffPerNode = Double.MaxValue
    var iter = 1
    while (iter < maxIter && deltaLabelDiffPerNode > tolerance) {
      // compute weighted neighborhood label distribution
      val updatesUnordered = for (node <- lpgraph.nodes.par) yield {
        val RandomWalkProbabilities(continue, inject, abandon) = rwprobs(node)

        // A place to store the accumulations for each label.
        val update = Array.fill(numLabels)(0.0)

        for (edge <- node.edges) {
          val neighbor = otherNode(edge.toOuter, node)

          // multiplier for MAD update: (p_v_cont * w_vu + p_u_cont * w_uv) where u is neighbor
          // #CHECKTHIS: need to look at edge.rweight for neighbor since it might be wrong.
          val mult =
            (continue * edge.rweight + rwprobs(neighbor).continue * edge.rweight)
          assert(mult > 0.0)

          val mu2Mult = mult * parameters.mu2
          val neighborLabels = estimatedLabels(neighbor)
          var labelIndex = 0
          while (labelIndex < numLabels) {
            update(labelIndex) += neighborLabels(labelIndex) * mu2Mult
            labelIndex += 1
          }
        }

        // add injection probability
        if (injectedLabels.isDefinedAt(node)) {
          val nodeInject = injectedLabels(node)
          val mu1Mult = inject * parameters.mu1
          var labelIndex = 0
          while (labelIndex < numLabels) {
            update(labelIndex) += nodeInject(labelIndex) * mu1Mult
            labelIndex += 1
          }
        }

        // add dummy label update
        update(0) += abandon * parameters.mu3

        val nodeConstant = normalizationConstants(node)
        var labelIndex = 0
        while (labelIndex < numLabels) {
          update(labelIndex) /= nodeConstant
          labelIndex += 1
        }

        val normalized = update.toSeq
        val deltaLabelDiff = squaredNormDiff(estimatedLabels(node), normalized)

        (node.value, normalized, deltaLabelDiff)
      }
      val (nodeIndices, updatedEstimatedLabels, deltas) =
        updatesUnordered.toIndexedSeq.sortBy(_._1).unzip3

      estimatedLabels = updatedEstimatedLabels

      deltaLabelDiffPerNode = deltas.sum / numNodes
      val objective = graphObjective(estimatedLabels)

      println(s"$iter: delta:$deltaLabelDiffPerNode obj:$objective")
      iter += 1
    }
    val globalEndTime = System.currentTimeMillis
    println("TIME: " + (globalEndTime - globalStartTime) / 1000.0)

    estimatedLabels
  }

  /**
   * Calculate the objective value of the graph given the current estimated labels.
   */
  private def graphObjective(estimatedLabels: Seq[Seq[Double]]): Double = {

    val dummyDist = Seq(1.0) ++ Seq.fill(numLabels - 1)(0.0)
    lpgraph.nodes.par.foldLeft(0.0) { (obj, node) =>
      {

        val nodeEstimated = estimatedLabels(node)

        // difference with injected labels
        val seedObjective = if (!injectedLabels.isDefinedAt(node)) 0.0 else
          (parameters.mu1 * rwprobs(node).inject *
            squaredNormDiff(injectedLabels(node), nodeEstimated))

        // difference with labels of neighbors
        val neighObjective = (for (edge <- node.edges) yield {
          val neighbor = otherNode(edge.toOuter, node)
          (parameters.mu2 * edge.rweight *
            squaredNormDiff(nodeEstimated, estimatedLabels(neighbor)))
        }).sum

        // difference with dummy labels
        val dummyDistMult = dummyDist.map(rwprobs(node).abandon*)
        val dummyObjective = parameters.mu3 * squaredNormDiff(dummyDistMult, nodeEstimated)

        obj + seedObjective + neighObjective + dummyObjective
      }
    }
  }

}

/**
 * Companion class that computes the random walk probabilities and normalization constants
 * and then constructs a ModifiedAdsorption instance.
 */
object ModifiedAdsorption {
  import math.{ log, sqrt, max }
  lazy val log2 = log(2)

  def apply(lpgraph: LabelPropGraph, parameters: AdsorptionParameters, beta: Double) = {
    val numLabels = lpgraph.numLabels
    val rwprobs = calculateRandomWalkProbabilities(lpgraph, beta)
    val normalizationConstants = computeNormalizationConstants(lpgraph, rwprobs, parameters)
    new ModifiedAdsorption(lpgraph, parameters, rwprobs, normalizationConstants)
  }

  /**
   * Calculate the random walk probabilities i.e. injection, continuation and
   * termination probabilities for each node.
   */
  private def calculateRandomWalkProbabilities(lpgraph: LabelPropGraph, beta: Double) = {

    val probs = for (node <- lpgraph.nodes.par) yield {
      val weights = node.incoming.toSeq.map(_.rweight)
      val totalWeight = weights.sum
      val normalizedWeights = weights.map(_ / totalWeight)
      val entropy = normalizedWeights.fold(0.0)((accum, curr) => accum - curr * log(curr) / log2)
      val cvTmp = log(beta) / log(beta + entropy)

      val (cv, jv) = if (lpgraph.injectedLabels.isDefinedAt(node)) {
        val jvTmp = (1 - cvTmp) * sqrt(entropy)
        if (jvTmp != 0.0) (cvTmp, jvTmp) else (0.01, 0.99)
      } else {
        (cvTmp, 0.0)
      }

      val zv = max(cv + jv, 1.0)
      val pcontinue = cv / zv
      val pinject = jv / zv
      val pabandon = max(0.0, 1 - pcontinue - pinject)
      (node.value, RandomWalkProbabilities(pcontinue, pinject, pabandon))
    }

    // Make sure the probabilities are stacked up in node-indexed order.
    probs.toIndexedSeq.sortBy(_._1).unzip._2
  }

  /**
   * Precomputes M_ii normalization (see algorithm in Talukdar and Crammer 2009).
   */
  def computeNormalizationConstants(
    lpgraph: LabelPropGraph,
    rwprobs: Seq[RandomWalkProbabilities],
    parameters: AdsorptionParameters
  ): Seq[Double] = {

    val AdsorptionParameters(mu1, mu2, mu3) = parameters
    val normsUnordered = for (node <- lpgraph.nodes.par) yield {
      val nodeRwProbs = rwprobs(node)
      val nodeContinue = nodeRwProbs.continue

      var totalNeighWeight = 0.0
      for (edge <- node.edges) {
        val neighbor = otherNode(edge.toOuter, node)
        totalNeighWeight += nodeContinue * edge.rweight
        // The following should be grabbing the weight in the neighbor to reach "node",
        // but now everything is symmetrical. Need to change this once directed graphs
        // are supported.
        totalNeighWeight += rwprobs(neighbor).continue * edge.rweight
      }

      //mii = mu1 x p^{inj} + mu2 x \sum_j (p_{i}^{cont} W_{ij} + p_{j}^{cont} W_{ji}) + mu3
      val mii = mu1 * nodeRwProbs.inject + mu2 * totalNeighWeight + mu3

      (node.value, mii)
    }

    normsUnordered.toIndexedSeq.sortBy(_._1).unzip._2
  }

}
