package rendering

import Composer
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import util.toDecimals

object Rendering {

    private var averageFrameTime = -1.0;

    private lateinit var ctx: CanvasRenderingContext2D
    private var width = 0.0
    private var height = 0.0


    fun render(delta: Double, canvas: HTMLCanvasElement) {
        canvas.resizeCanvas()

        if(!Composer.renderRequested && !Composer.continousRendering) {
            return
        }

        ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        width = canvas.width.toDouble()
        height = canvas.height.toDouble()
        ctx.clearRect(0.0, 0.0, width, height)

        renderCircuit()

        ctx.fillStyle = "#000000"
        averageFrameTime = if(averageFrameTime == -1.0) delta else averageFrameTime * 0.95 + delta * 0.05;
        ctx.fillText("Frame Time: ${averageFrameTime.toDecimals(3)} s  (${ (1.0 / averageFrameTime).toDecimals(1) } fps)", 0.0, 20.0);
    }

    private fun renderCircuit() {
        ctx.scale(Composer.scale, Composer.scale)
        ctx.translate(Composer.offset.x, Composer.offset.y)

        ctx.fillStyle = "#278f42"
        ctx.beginPath()
        ctx.rect(100.0, 100.0, 50.0, 50.0)
        ctx.fill()

        ctx.setIdentityMatrix()
    }
}