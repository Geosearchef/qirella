package rendering

import Composer
import org.w3c.dom.CanvasRenderingContext2D
import util.toDecimals

object Rendering {

    var averageFrameTime = -1.0;

    fun render(delta: Double) {
        val canvas = Composer.canvas
        canvas.resizeCanvas()

        if(!Composer.renderRequested && !Composer.continousRendering) {
            return
        }

        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        val width = canvas.width.toDouble()
        val height = canvas.height.toDouble()
        ctx.clearRect(0.0, 0.0, width, height)

        ctx.fillStyle = "#278f42"
        ctx.beginPath()
        ctx.rect(100.0, 100.0, 50.0, 50.0)
        ctx.fill()

        ctx.fillStyle = "#000000"
        averageFrameTime = if(averageFrameTime == -1.0) delta else averageFrameTime * 0.95 + delta * 0.05;
        ctx.fillText("Frame Time: ${averageFrameTime.toDecimals(3)} s  (${ (1.0 / averageFrameTime).toDecimals(1) } fps)", 0.0, 20.0);
    }
}