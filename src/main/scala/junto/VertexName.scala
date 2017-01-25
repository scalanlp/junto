package junto

class VertexName(name: String, vtype: String = "VERTEX") {
  override val toString = vtype + "::" + name
}
