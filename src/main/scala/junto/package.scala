package object junto {

  protected[junto] def differenceNorm2Squared(length: Int)(
    dist1: Seq[Double], dist2: Seq[Double]
  ) = {
    var index = 0
    var squaredDiffsSum = 0.0
    while (index < length) {
      val diff = dist1(index) - dist2(index)
      squaredDiffsSum += diff * diff
      index += 1
    }
    math.sqrt(squaredDiffsSum)
  }

  protected[junto] lazy val DUMMY_LABEL = "__DUMMY__"

}
