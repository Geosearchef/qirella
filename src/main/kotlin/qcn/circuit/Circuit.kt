package qcn.circuit

import kotlinx.serialization.Serializable
import qcn.QCComposer
import util.math.Vector

@Serializable
class Circuit {

    var components: MutableList<CircuitComponent> = ArrayList()

    fun addComponent(component: CircuitComponent): CircuitComponent {
        components.add(component)
        return component
    }

    fun removeComponent(component: CircuitComponent) {
        components.remove(component)

        (component as? ControlComponent)?.removeFromParent()
        (component as? GateComponent)?.controlComponents?.forEach { removeComponent(it) }

        QCComposer.selectedComponents.remove(component)
    }

    fun setComponentPosition(component: CircuitComponent, newPos: Vector) {
        if(! component.isValidPosition(newPos)) {
            return
        }

        component.pos = newPos
        if(component is GateComponent) {
            component.controlComponents.forEach { setComponentPosition(it, Vector(newPos.x ,it.pos.y)) }
            component.controlComponents.filter { isPositionBlocked(it.pos, it) }.forEach { removeComponent(it)}
        }
    }


    fun isPositionBlocked(pos: Vector, vararg exclusions: CircuitComponent): Boolean {
        return components.any { it !in exclusions && it.pos == pos}
    }

    fun getUsedRegisters(): List<Int> {
        return components.map { it.qubitIndex }.distinct().sorted()
    }

    fun getClassicalRegisters(): List<Int> {
        return components.filterIsInstance<MeasurementComponent>().map { it.classicalTarget }.distinct().sorted()
    }
}