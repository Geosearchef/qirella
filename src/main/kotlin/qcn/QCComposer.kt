package qcn

import Qirella
import qcn.circuit.Circuit
import qcn.circuit.CircuitComponent
import qcn.circuit.GateGenerator
import qcn.circuit.generatorStartupComplete
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

    var selectedComponents: MutableList<CircuitComponent> = ArrayList()

    var uiInstance = QCUI(300, 200)


    fun init() {
        circuit.components.add(GateGenerator.HADAMARD.generate(Vector(0.0, 0.0)))
        circuit.components.add(GateGenerator.X.generate(Vector(1.0, 1.0)))

        generatorStartupComplete = true // this causes the generators to be (have been) loaded which causes them to generate their dummies, for which we want to prevent user input
    }

    fun deselectAllComponents() {
        selectedComponents.clear()
    }

    fun selectComponent(component: CircuitComponent) {
        if(component.selectable && !selectedComponents.contains(component)) {
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