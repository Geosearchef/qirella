package util.math.linalg

import util.math.Complex
import util.math.Complex.Companion.j
import util.math.minus
import util.math.plus
import util.math.times

fun matrixOf(vararg rows: Array<Complex>) = Matrix(arrayOf(*rows)) // spread operator as vararg is read-only (out)
fun kroneckerOf(vararg gates: Matrix) = gates.reduce { l, r -> l.kronecker(r) }
fun complexArrayOf(vararg array: Any): Array<Complex> {
    require(array.all { it is Double || it is Complex })
    return array.map { if(it is Double) Complex(it) else it as Complex }.toTypedArray()
}


class Matrix(var m: Array<Array<Complex>> = arrayOf(
                arrayOf(Complex(1.0), Complex(0.0)),
                arrayOf(Complex(0.0), Complex(1.0)))) {

    companion object {
        val I = Matrix()
        val Q0 = matrixOf(arrayOf(1.0+0.0*j), arrayOf(0.0+0.0*j))
        val Q1 = matrixOf(arrayOf(0.0+0.0*j), arrayOf(1.0+0.0*j))
        val PAULI_X = matrixOf(
            arrayOf(0.0+0.0*j, 1.0+0.0*j),
            arrayOf(1.0+0.0*j, 0.0+0.0*j)
        )
        val PAULI_Y = matrixOf(
            arrayOf(0.0+0.0*j, 0.0-1.0*j),
            arrayOf(0.0+1.0*j, 0.0+0.0*j)
        )
        val PAULI_Z = matrixOf(
            arrayOf(1.0+0.0*j, 0.0+0.0*j),
            arrayOf(0.0+0.0*j, -1.0+0.0*j)
        )
    }

    constructor (rows: Int, columns: Int) : this(Array(rows) { Array(columns) { Complex() } })

    val dim: IntArray get() = intArrayOf(m.size, if(m.isNotEmpty()) m[0].size else 0)
    val rows: Int get() = m.size
    val columns: Int get() = m[0].size

    fun isVector() = dim[0] == 1 || dim[1] == 1
    fun isScalar() = dim[0] == 1 && dim[1] == 1

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
        require(this.columns == o.rows) { "Incompatible matrix sizes: ${this.rows}x${this.columns} and ${o.rows}x${o.columns}" }
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

    override fun toString() = indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col].toString() }.joinToString("\t") }.joinToString("\n")

    private fun indicies() = (0 until rows).map { (0 until columns) }
    private fun indiciesTransposed() = (0 until columns).map { (0 until rows) }
}