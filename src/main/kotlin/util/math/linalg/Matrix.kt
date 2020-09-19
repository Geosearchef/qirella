package util.math.linalg

import util.math.Complex

class Matrix(var m: Array<Array<Complex>> = arrayOf(
                arrayOf(Complex(1.0), Complex(0.0)),
                arrayOf(Complex(0.0), Complex(1.0)))) {

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
    operator fun minus(o: Matrix) = Matrix(indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] + o.m[row][col] }.toTypedArray() }.toTypedArray())
    operator fun times(s: Complex) = Matrix(indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] * s }.toTypedArray() }.toTypedArray())
    operator fun div(s: Complex) = times(Complex(1.0) / s)

    //TODO: MATMUL!!!!

    operator fun plusAssign(o: Matrix) = set(plus(o))
    operator fun minusAssign(o: Matrix) = set(minus(o))
    operator fun timesAssign(s: Complex) = set(times(s))
    operator fun divAssign(s: Complex) = set(div(s))

    override fun equals(other: Any?): Boolean {
        return other != null && other is Matrix && dim contentEquals other.dim
                && indicies().mapIndexed { row, rowInd -> rowInd.map { col -> m[row][col] == other.m[row][col] } }.all { it.all { it } }
    }

    //TODO: transpose, transpose conjugate this

    private fun indicies() = (0 until rows).map { (0 until columns) }
}