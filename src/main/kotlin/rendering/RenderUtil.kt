package rendering

import org.w3c.dom.*
import util.Vector

fun toWorldSpace() {
    throw UnsupportedOperationException() // TODO
}

fun toRenderSpace(pos: Vector) = pos * Rendering.GRID_SIZE

fun HTMLCanvasElement.resizeCanvas() {
    val displayWidth = this.offsetWidth
    val displayHeight = this.offsetHeight

    if(displayWidth != this.width || displayHeight != this.height) {
        this.style.margin = "0"
        this.style.border = "0"

        this.width = displayWidth
        this.height = displayHeight

        println("Resized canvas to $displayWidth x $displayHeight")
    }
}

fun CanvasRenderingContext2D.setIdentityMatrix() = this.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

fun CanvasRenderingContext2D.fillRect(v: Vector, w: Double, h: Double) = fillRect(v.x, v.y, w, h)
fun CanvasRenderingContext2D.fillSquare(v: Vector, s: Double) = fillRect(v, s, s)
fun CanvasRenderingContext2D.fillTextCentered(s: String, v: Vector) {
    val tB = textBaseline; val tA = textAlign

    textBaseline = CanvasTextBaseline.MIDDLE
    textAlign = CanvasTextAlign.CENTER
    fillText(s, v.x, v.y)

    textBaseline = tB; textAlign = tA
}

fun CanvasRenderingContext2D.color(c: String) {
    fillStyle = c
}