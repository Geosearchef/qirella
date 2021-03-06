package rendering

import Qirella
import org.w3c.dom.*
import scene.Scene
import util.toDecimals

object Rendering {

    private var averageFrameTime = -1.0;

    lateinit var ctx: CanvasRenderingContext2D
    var width = 0.0
    var height = 0.0


    fun render(delta: Double, canvas: HTMLCanvasElement) {
        canvas.resizeCanvas()

        if(!Qirella.renderRequested && !Qirella.continousRendering) {
            return
        }
        Qirella.renderRequested = false

        ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        width = canvas.width.toDouble()
        height = canvas.height.toDouble()
        ctx.clearRect(0.0, 0.0, width, height)

        Scene.currentScene.renderer.render()
        Scene.currentScene.uiManager.getUI().render()

        // render frametime
        ctx.fillStyle = "#000000"
        ctx.font = "8px sans-serif"
        ctx.textAlign = CanvasTextAlign.RIGHT
        averageFrameTime = if(averageFrameTime == -1.0) delta else averageFrameTime * 0.95 + delta * 0.05;
        ctx.fillText("Frame Time: ${averageFrameTime.toDecimals(3)} s  (${ (1.0 / averageFrameTime).toDecimals(1) } fps)", width - 3.0, height - 3.0);
        ctx.textAlign = CanvasTextAlign.LEFT
        ctx.font = "10px sans-serif"
    }
}