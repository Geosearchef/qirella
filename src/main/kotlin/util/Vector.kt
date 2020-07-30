package util

import kotlin.math.pow
import kotlin.math.sqrt

data class Vector(var x: Double = 0.0, var y: Double = 0.0) {

    fun add(v: Vector) = Vector(v.x + x, v.y + y)
    fun sub(v: Vector) = Vector(v.x - x, v.y - y)
    fun scale(s: Double) = Vector(x * s, y * s)
    fun negate() = Vector(-x, -y)
    fun lengthSquared() = x.pow(2) + y.pow(2)
    fun length() = sqrt(lengthSquared())
    fun normalise() = scale(1.0 / length())
    fun clone() = Vector(x, y)
    fun dot(v: Vector) = v.x * x + v.y * y

    // operator overloading
    operator fun not() = (x == 0.0 && y == 0.0)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = negate()
    operator fun plus(v: Vector) = add(v)
    operator fun minus(v: Vector) = sub(v)
    operator fun times(s: Double) = scale(s)
    operator fun times(v: Vector) = dot(v)
    operator fun div(s: Double) = scale(1.0 / s)
    
    // TODO: contained in rect
}