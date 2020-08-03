import circuit.Circuit
import circuit.GateElement
import circuit.GateType
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

    var offset = Vector(-3.0, -3.0)
    var scale = 1.0

    var circuit = Circuit()

    fun init() {
        Input.init(canvas)

        window.requestAnimationFrame(::animationFrame)

        circuit.elements.add(GateElement(Vector(0.0, 0.0), GateType.HADAMARD))
        circuit.elements.add(GateElement(Vector(1.0, 1.0), GateType.PAULI_X))
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