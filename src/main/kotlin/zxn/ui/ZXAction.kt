package zxn.ui

import ui.UIAction
import zxn.ZXComposer
import zxn.network.ZXNode

enum class ZXAction(override val representation: String) : UIAction {

    REALIGN_ENTIRE_NETWORK("Align (A)") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            ZXComposer.realignNetwork()
        }
    },

    // selection actions
    CREATE_WIRES("Create Wires (C)") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            ZXComposer.createSelectedWires()
        }
    },
    DELETE_WIRES("Delete Wires (D)") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            ZXComposer.deleteSelectedWires()
        }
    },

    DELETE_NODES("Del. Nodes (RMB)") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            ZXComposer.deleteSelectedNodes()
        }
    },


    LOAD("\uD83D\uDCC2") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
//            val circuitPromise = Storage.load()
//
//            circuitPromise.then { QCComposer.circuit = it }
//            circuitPromise.catch { throw it }
            TODO("not yet implemented")
        }
    },
    SAVE("\uD83D\uDCBE") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
//            Storage.store(QCComposer.circuit)
            TODO("not yet implemented")
        }
    };

    abstract fun onZXAction(selectedNodes: List<ZXNode>)
    override fun onAction() {
        onZXAction(ZXComposer.selectedNodes)
    }
}