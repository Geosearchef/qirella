package circuit

import util.math.Vector

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
        return components.any { !exclusions.contains(it) && it.pos == pos}
    }

}