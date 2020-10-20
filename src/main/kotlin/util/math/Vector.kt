package util.math

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class Vector(var x: Double = 0.0, var y: Double = 0.0) {

    fun add(v: Vector) = Vector(v.x + x, v.y + y)
    fun sub(v: Vector) = Vector(x - v.x, y - v.y)
    fun scale(s: Double) = Vector(x * s, y * s)
    fun negate() = Vector(-x, -y)
    fun lengthSquared() = x.pow(2) + y.pow(2)
    fun length() = sqrt(lengthSquared())
    fun normalise() = scale(1.0 / length())
    fun clone() = Vector(x, y)
    fun dot(v: Vector) = v.x * x + v.y * y
    fun floor() = Vector(kotlin.math.floor(x), kotlin.math.floor(y))
    fun ceil() = Vector(kotlin.math.ceil(x), kotlin.math.ceil(y))
    fun round() = Vector(kotlin.math.round(x), kotlin.math.round(y))


    operator fun not() = (x == 0.0 && y == 0.0)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = negate()
    operator fun plus(v: Vector) = add(v)
    operator fun plus(s: Double) = add(Vector(s, s))
    operator fun minus(v: Vector) = sub(v)
    operator fun minus(s: Double) = sub(Vector(s, s))
    operator fun times(v: Vector) = dot(v)
    operator fun times(s: Double) = scale(s)
    operator fun div(s: Double) = scale(1.0 / s)
    override fun equals(other: Any?): Boolean {
        return (other as? Vector)?.let { it.x == this.x && it.y == this.y } ?: false // wtf kotlin :)
        ///return other != null && other is Vector && other.x == this.x && other.y == this.y
    }
    // TODO: contained in rect
}