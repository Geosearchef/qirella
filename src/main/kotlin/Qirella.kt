import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import rendering.Rendering
import util.Util

object Qirella {
    val canvas = document.getElementById("composer-canvas") as? HTMLCanvasElement ?: throw RuntimeException("Composer canvas not found")

    var renderRequested = true
    var continousRendering = false

    fun requestRender() {
        renderRequested = true
    }

    private var lastFrame = Util.currentTimeMillis()
    fun animationFrame(t: Double) {
        val deltaMillis = Util.currentTimeMillis() - lastFrame;
        lastFrame += deltaMillis;
        val delta = deltaMillis / 1000.0;

        Rendering.render(delta, canvas)

        window.requestAnimationFrame(Qirella::animationFrame)
    }
}