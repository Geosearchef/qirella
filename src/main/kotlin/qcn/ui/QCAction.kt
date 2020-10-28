package qcn.ui

import qcn.QCComposer
import qcn.circuit.CircuitComponent
import qcn.storage.Storage
import ui.UIAction

enum class QCAction(override val representation: String) : UIAction {

    // selection actions
    DELETE("❌") {
        override fun onQCAction(selectedComponents: List<CircuitComponent>) {
            selectedComponents.forEach { QCComposer.circuit.removeComponent(it) }
        }
    },


    LOAD("\uD83D\uDCC2") {
        override fun onQCAction(selectedComponents: List<CircuitComponent>) {
            val circuitPromise = Storage.load()

            circuitPromise.then { QCComposer.circuit = it }
            circuitPromise.catch { throw it }
        }
    },
    SAVE("\uD83D\uDCBE") {
        override fun onQCAction(selectedComponents: List<CircuitComponent>) {
            Storage.store(QCComposer.circuit)
        }
    };

    abstract fun onQCAction(selectedComponents: List<CircuitComponent>)
    override fun onAction() {
        onQCAction(QCComposer.selectedComponents)
    }
}