package ui

import Composer
import circuit.CircuitComponent

enum class Action(val representation: String) {

    // selection actions
    DELETE("❌") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            selectedComponents.forEach { Composer.circuit.removeComponent(it) }
        }
    },


    LOAD("Load") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            throw UnsupportedOperationException()
        }
    },
    SAVE("Save") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            throw UnsupportedOperationException()
        }
    };

    abstract fun onAction(selectedComponents: List<CircuitComponent>)

}