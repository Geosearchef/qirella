package util.math

class Rectangle(var x: Double, var y: Double, var width: Double, var height: Double) {

    val pos: Vector get() = Vector(x, y)
    constructor(pos: Vector, width: Double, height: Double) : this(pos.x, pos.y, width, height)

    operator fun contains(v: Vector) : Boolean {
        val vrel = v - pos
        return vrel.x >= 0.0 && vrel.y >= 0.0 && vrel.x <= width && vrel.y <= height
    }

    val center: Vector get() = pos + Vector(width / 2.0, height / 2.0)
}



fun rectangleOf(v1: Vector, v2: Vector): Rectangle {
    val rec = Rectangle(v1, v2.x - v1.x, v2.y - v1.y)
    if(rec.width < 0.0) {
        rec.x += rec.width;
        rec.width *= -1.0;
    }
    if(rec.height < 0.0) {
        rec.y += rec.height;
        rec.height *= -1.0;
    }
    return rec
}