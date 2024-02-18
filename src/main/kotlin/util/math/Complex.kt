package util.math

import kotlinx.serialization.Serializable
import kotlin.math.*

operator fun Double.plus(c: Complex) = Complex(this).plus(c)
operator fun Double.minus(c: Complex) = Complex(this).minus(c)
operator fun Double.times(c: Complex) = Complex(this).times(c)
operator fun Double.div(c: Complex) = Complex(this).div(c)

@Serializable
class Complex(var real: Double = 0.0, var imag: Double = 0.0) {

    companion object {
        val j = Complex(0.0, 1.0)
        fun fromMagArg(mag: Double, arg: Double) = Complex(cos(arg) * mag, sin(arg) * mag)
        fun fromAmpPhase(amplitude: Double, phase: Double) = fromMagArg(amplitude, phase)
        fun exp(arg: Double) = fromMagArg(1.0, arg) // e^i*arg
    }

    fun set(real: Double, imag: Double) {
        this.real = real
        this.imag = imag
    }
    fun set(c: Complex) = set(c.real, c.imag)
    fun clone() = Complex(real, imag)

    operator fun unaryPlus() = this
    operator fun unaryMinus() = Complex(-real, -imag)
    operator fun not() = real == 0.0 && imag == 0.0

    operator fun inc() = Complex(real + 1.0, imag)
    operator fun dec() = Complex(real - 1.0, imag)
    operator fun plus(o: Complex) = Complex(real + o.real, imag + o.imag)
    operator fun minus(o: Complex) = Complex(real - o.real, imag - o.imag)
    operator fun times(o: Complex) = Complex(real * o.real - imag * o.imag, imag * o.real + real * o.imag) // (a + bi) * (c + di) = ac + bci + adi - bd
    operator fun div(o: Complex) = Complex( // TODO: unit test all of this
            (real * o.real + imag * o.imag) / (o.real.pow(2) + o.imag.pow(2)),
            (imag * o.real - real * o.imag) / (o.real.pow(2) + o.imag.pow(2))
        ) // https://mathworld.wolfram.com/ComplexDivision.html

    operator fun plusAssign(o: Complex) = set(plus(o))
    operator fun minusAssign(o: Complex) = set(minus(o))
    operator fun timesAssign(o: Complex) = set(times(o))
    operator fun divAssign(o: Complex) = set(div(o))

    operator fun plus(s: Double) = plus(Complex(s))
    operator fun minus(s: Double) = minus(Complex(s))
    operator fun times(s: Double) = times(Complex(s))
    operator fun div(s: Double) = div(Complex(s))

    override fun equals(other: Any?): Boolean {
        return other != null && other is Complex && other.real == this.real && other.imag == this.imag
    }

    fun magnitude() = sqrt(real.pow(2) + imag.pow(2))
    fun amplitude() = magnitude()
    val abs: Double get() = magnitude()
    val mag: Double get() = magnitude()
    fun argument() = atan2(imag, real)
    val arg: Double get() = argument()
    val phase: Double get() = argument()

    fun conj() = Complex(real, -imag) // conjugate

    override fun toString() = "${real} + ${imag}i"
}