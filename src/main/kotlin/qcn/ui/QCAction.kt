package qcn.ui

import qcn.QCComposer
import qcn.circuit.CircuitComponent
import qcn.storage.Storage

enum class QCAction(val representation: String) {

    // selection actions
    DELETE("❌") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            selectedComponents.forEach { QCComposer.circuit.removeComponent(it) }
        }
    },


    LOAD("\uD83D\uDCC2") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            val circuitPromise = Storage.load()

            circuitPromise.then { QCComposer.circuit = it }
            circuitPromise.catch { throw it }
        }
    },
    SAVE("\uD83D\uDCBE") {
        override fun onAction(selectedComponents: List<CircuitComponent>) {
            Storage.store(QCComposer.circuit)
        }
    };

    abstract fun onAction(selectedComponents: List<CircuitComponent>)

}