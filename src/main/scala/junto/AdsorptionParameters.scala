package junto

/**
 * The mu parameters for modified adsorption.
 */
case class AdsorptionParameters(
  mu1: Double = 1.0, // Seed label fidelity
  mu2: Double = 0.01, // Neighbor label fidelity
  mu3: Double = 0.01 // Dummy label weight (discount high-degree nodes)
)
