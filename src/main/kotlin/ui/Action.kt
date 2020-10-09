package ui

import Composer
import circuit.CircuitComponent
import storage.Storage

enum class Action(val representation: String) {

    // selection actions
    DELETE("❌") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            selectedComponents.forEach { Composer.circuit.removeComponent(it) }
        }
    },


    LOAD("\uD83D\uDCC2") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            Storage.load()//where
        }
    },
    SAVE("\uD83D\uDCBE") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            Storage.store()
        }
    };

    abstract fun onAction(selectedComponents: List<CircuitComponent>)

}