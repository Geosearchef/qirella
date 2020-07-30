import input.Input
import org.w3c.dom.HTMLCanvasElement
import rendering.Rendering
import util.Util.currentTimeMillis
import util.Vector
import kotlin.browser.document
import kotlin.browser.window

object Composer {
    val canvas = document.getElementById("composer-canvas") as? HTMLCanvasElement ?: throw RuntimeException("Composer canvas not found")

    var renderRequested = true
    var continousRendering = true

    var offset = Vector(0.0, 0.0)
    var scale = 1.0

    fun init() {
        Input.init(canvas)

        window.requestAnimationFrame(::animationFrame)
    }

    fun requestRender() {
        renderRequested = true
    }

    private var lastFrame = currentTimeMillis()
    private fun animationFrame(t: Double) {
        val deltaMillis = currentTimeMillis() - lastFrame;
        lastFrame += deltaMillis;
        val delta = deltaMillis / 1000.0;

        Rendering.render(delta, canvas)

        window.requestAnimationFrame(::animationFrame)
    }
}