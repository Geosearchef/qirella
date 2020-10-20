import circuit.Circuit
import circuit.CircuitComponent
import circuit.GateComponent
import circuit.GateType
import input.Input
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import rendering.Rendering
import util.Util.currentTimeMillis
import util.math.Vector

object Composer {

    const val GATE_SIZE = 50.0
    const val GATE_SPACING = 50.0
    const val GRID_SIZE = GATE_SIZE + GATE_SPACING

    const val GATE_SIZE_WORLD_SPACE = GATE_SIZE / GRID_SIZE


    val canvas = document.getElementById("composer-canvas") as? HTMLCanvasElement ?: throw RuntimeException("Composer canvas not found")

    var renderRequested = true
    var continousRendering = false

    var offset = Vector(3.0, 3.0) // in world space
    var scale = 1.0

    var circuit: Circuit = Circuit()
        set(value) {
            field = value
            requestRender()
        }

    var grabbedComponent: CircuitComponent? = null
    var grabbedOrigin: Vector? = null

    var selectedComponents: MutableList<CircuitComponent> = ArrayList<CircuitComponent>()


    fun init() {
        Input.init(canvas)

        window.requestAnimationFrame(::animationFrame)

        circuit.components.add(GateComponent(Vector(0.0, 0.0), GateType.HADAMARD))
        circuit.components.add(GateComponent(Vector(1.0, 1.0), GateType.X))
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

    fun deselectAllComponents() {
        selectedComponents.clear()
    }

    fun selectComponent(component: CircuitComponent) {
        if(component.selectable) {
            selectedComponents.add(component)
        }
    }
}