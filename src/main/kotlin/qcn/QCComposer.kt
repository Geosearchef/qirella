package qcn

import Qirella
import qcn.circuit.Circuit
import qcn.circuit.CircuitComponent
import qcn.circuit.GateComponent
import qcn.circuit.GateType
import qcn.ui.QCUI
import scene.UIManager
import ui.SceneUI
import util.math.Vector

object QCComposer : UIManager {

    const val GATE_SIZE = 50.0
    const val GATE_SPACING = 50.0
    const val GRID_SIZE = GATE_SIZE + GATE_SPACING

    const val GATE_SIZE_WORLD_SPACE = GATE_SIZE / GRID_SIZE

    var offset = Vector(3.0, 3.0) // in world space
    var scale = 1.0

    var circuit: Circuit = Circuit()
        set(value) {
            field = value
            Qirella.requestRender()
        }

    var grabbedComponent: CircuitComponent? = null
    var grabbedOrigin: Vector? = null

    var selectedComponents: MutableList<CircuitComponent> = ArrayList<CircuitComponent>()

    var uiInstance = QCUI(300, 200)


    fun init() {
        circuit.components.add(GateComponent(Vector(0.0, 0.0), GateType.HADAMARD))
        circuit.components.add(GateComponent(Vector(1.0, 1.0), GateType.X))
    }

    fun deselectAllComponents() {
        selectedComponents.clear()
    }

    fun selectComponent(component: CircuitComponent) {
        if(component.selectable) {
            selectedComponents.add(component)
        }
    }

    override fun regenerateUI(width: Int, height: Int) {
        uiInstance = QCUI(width, height)
    }

    override fun getUI(): SceneUI {
        return uiInstance
    }
}