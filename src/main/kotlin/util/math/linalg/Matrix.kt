package util.math.linalg

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import util.math.Complex
import util.math.Complex.Companion.j
import util.math.times
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun matrixOf(vararg rows: Array<Complex>) = Matrix(arrayOf(*rows)) // spread operator as vararg is read-only (out)
fun kroneckerOf(vararg gates: Matrix) = gates.reduce { l, r -> l.kronecker(r) }
fun complexArrayOf(vararg array: Any): Array<Complex> {
    require(array.all { it is Double || it is Complex })
    return array.map { if(it is Double) Complex(it) else it as Complex }.toTypedArray()
}

operator fun Double.times(m: Matrix) = m.times(this)
operator fun Complex.times(m: Matrix) = m.times(this)

@Serializable(with = MatrixSerializer::class)
open class Matrix(var m: Array<Array<Complex>> = arrayOf(
                complexArrayOf(1.0, 0.0),
                complexArrayOf(0.0, 1.0))) {

    companion object {
        val I = Matrix()
        val Q0 = columnVectorOf(1.0, 0.0)
        val Q1 = columnVectorOf(0.0, 1.0)
        val Qplus = columnVectorOf(1.0/sqrt(2.0), +1.0/sqrt(2.0))
        val Qminus = columnVectorOf(1.0/sqrt(2.0), -1.0/sqrt(2.0))
        val PAULI_X = matrixOf(
            complexArrayOf(0.0, 1.0),
            complexArrayOf(1.0, 0.0)
        )
        val PAULI_Y = matrixOf(
            complexArrayOf(0.0*j, -1.0*j),
            complexArrayOf(1.0*j, 0.0*j)
        )
        val PAULI_Z = matrixOf(
            complexArrayOf(1.0, 0.0),
            complexArrayOf(0.0, -1.0)
        )
        val AXIS_X = columnVectorOf(1.0, 0.0, 0.0)
        val AXIS_Y = columnVectorOf(0.0, 1.0, 0.0)
        val AXIS_Z = columnVectorOf(0.0, 0.0, 1.0)
    }

    constructor (rows: Int, columns: Int) : this(Array(rows) { Array(columns) { Complex() } })

    val dim: IntArray get() = intArrayOf(m.size, if(m.isNotEmpty()) m[0].size else 0)
    val rows: Int get() = m.size
    val columns: Int get() = m[0].size

    fun isVector() = dim[0] == 1 || dim[1] == 1
    fun isScalar() = dim[0] == 1 && dim[1] == 1
    fun asScalar(): Complex {
        check(isScalar()) { "Not a scalar" }
        return m[0][0]
    }

    fun set(m: Array<Array<Complex>>) {
        this.m = m
    }
    fun set(src: Matrix) = set(clone().m)
    fun clone() = Matrix(m.map { row -> row.map { e -> e.clone() }.toTypedArray() }.toTypedArray())

    operator fun unaryPlus() = this
    operator fun unaryMinus() = times(Complex(-1.0))

    // NO SIZE CHECKING
    operator fun plus(o: Matrix) = Matrix(indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] + o.m[row][col] }.toTypedArray() }.toTypedArray())
    operator fun minus(o: Matrix) = Matrix(indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] - o.m[row][col] }.toTypedArray() }.toTypedArray())
    operator fun times(s: Complex) = Matrix(indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] * s }.toTypedArray() }.toTypedArray())
    operator fun div(s: Complex) = times(Complex(1.0) / s)

    operator fun times(s: Double) = times(Complex(s))
    operator fun div(s: Double) = div(Complex(s))

    //TODO: could be way more effective, especially in case of 2^k x 2^k matricies
    operator fun times(o: Matrix): Matrix {
        require(this.columns == o.rows) { "Incompatible matrix sizes: ${this.shapeString()} and ${o.shapeString()}" }
        val mat = Matrix(rows = this.rows, columns = o.columns)

        for(i in 0 until this.rows) {
            for(j in 0 until o.columns) {
                var sum = Complex(0.0, 0.0)
                for(k in 0 until this.columns) {
                    sum = sum + (this.m[i][k] * o.m[k][j])
                }
                mat.m[i][j] = sum
            }
        }

        return mat
    }

    operator fun plusAssign(o: Matrix) = set(plus(o))
    operator fun minusAssign(o: Matrix) = set(minus(o))
    operator fun timesAssign(s: Complex) = set(times(s))
    operator fun divAssign(s: Complex) = set(div(s))

    override fun equals(other: Any?): Boolean {
        return other != null && other is Matrix && dim[0] == other.dim[0] && dim[1] == other.dim[1]
                && indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] == other.m[row][col] } }.all { it.all { it } }
    }

    //TODO: transpose, transpose conjugate this
    fun transpose(): Matrix = Matrix(indiciesTransposed().mapIndexed { col, colInd -> colInd.map { row -> m[row][col] }.toTypedArray() }.toTypedArray())
    val T get() = transpose()

    fun conjugateTranspose() = Matrix(indiciesTransposed().mapIndexed { col, colInd -> colInd.map { row -> m[row][col].conj() }.toTypedArray() }.toTypedArray())
    val H get() = conjugateTranspose()

    fun kronecker(B: Matrix): Matrix {
        val A = this
        val mat = Matrix(A.rows * B.rows, A.columns * B.columns)

        for(a_i in 0 until A.rows) {
            for(a_j in 0 until A.columns) {
                for(b_i in 0 until B.rows) {
                    for(b_j in 0 until B.columns) {
                        mat.m[a_i * B.rows + b_i][a_j * B.columns + b_j] = A.m[a_i][a_j] * B.m[b_i][b_j]
                    }
                }
            }
        }

        return mat
    }

    fun isColumnVector() = this.columns == 1
    fun asColumnVector(): ColumnVector {
        check(isColumnVector()) {"Cannot convert ${this.shapeString()} to column vector,"}
        return ColumnVector(m.map { it[0] }.toTypedArray())
    }

    override fun toString() = indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col].toString() }.joinToString("\t") }.joinToString("\n")
    fun shapeString() = "${this.rows}x${this.columns}"

    private fun indicies() = (0 until rows).map { (0 until columns) }
    private fun indiciesTransposed() = (0 until columns).map { (0 until rows) }
}

fun columnVectorOf(vararg v: Any) = ColumnVector(complexArrayOf(*v))
@Serializable(with = MatrixSerializer::class)
class ColumnVector(v: Array<Complex>) : Matrix(v.map { arrayOf(it) }.toTypedArray()) {
    val qubits get() = log2(this.rows.toDouble()).roundToInt()
    val abs: Double get() {
        val scalarProd = (this.H * this).asScalar()
        check(scalarProd.imag == 0.0)
        return sqrt(scalarProd.real)
    }
}


object MatrixSerializer : KSerializer<Matrix> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Matrix") {
        element<Boolean>("columnVector")
        element<Array<Array<Complex>>>("m")
    }

    val matrixSerializer: KSerializer<Array<Array<Complex>>> = serializer()

    override fun serialize(encoder: Encoder, value: Matrix) {
        encoder.encodeStructure(descriptor) {
            encodeBooleanElement(descriptor, 0, value is ColumnVector)
//            encodeSerializableElement(descriptor, 1, Matrix.serializer())
            encodeSerializableElement(descriptor, 1, matrixSerializer, value.m)
        }
    }

    override fun deserialize(decoder: Decoder): Matrix =
        decoder.decodeStructure(descriptor) {
            val isColumnVector = decodeBooleanElement(descriptor, 0)
            val m: Array<Array<Complex>> = decodeSerializableElement(descriptor, 1, matrixSerializer)

            when(isColumnVector) {
                true -> ColumnVector(m.map { it[0] }.toTypedArray())  // this might discard information
                false -> Matrix(m)
            }
        }

}