package util.math

class Rectangle(var pos: Vector, var width: Double, var height: Double) {

    operator fun contains(v: Vector) : Boolean {
        val vrel = v - pos
        return vrel.x >= 0.0 && vrel.y >= 0.0 && vrel.x <= width && vrel.y <= height
    }

    val center: Vector get() = pos + Vector(width / 2.0, height / 2.0)
}