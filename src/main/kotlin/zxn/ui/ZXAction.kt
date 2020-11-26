package zxn.ui

import kotlinx.browser.window
import ui.UIAction
import zxn.ZXComposer
import zxn.network.QubitNode
import zxn.network.Spider
import zxn.network.ZXNode
import kotlin.math.PI

enum class ZXAction(override val representation: String) : UIAction {

    TOGGLE_INPUT_OUTPUT("Toggle IN/OUT") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            selectedNodes.filterIsInstance<QubitNode>().forEach { it.toggleMode() }
        }

        override fun isEnabled(selectedNodes: List<ZXNode>): Boolean = selectedNodes.firstOrNull() is QubitNode
    },

    TOGGLE_SPIDER_COLOR("Toggle Color") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            selectedNodes.filterIsInstance<Spider>().forEach { it.toggleColor() }
        }

        override fun isEnabled(selectedNodes: List<ZXNode>): Boolean = selectedNodes.firstOrNull() is Spider
    },

    SET_SPIDER_PHASE("Set phase") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            val currentPhase = (selectedNodes.filterIsInstance<Spider>().firstOrNull()?.phase ?: PI) / PI
            window.prompt(message = "New phase? (as a multiple of π)", currentPhase.toString())?.let { newPhaseString ->
                try {
                    val newPhaseValue = newPhaseString.toDouble()
                    selectedNodes.filterIsInstance<Spider>().forEach { it.phase = newPhaseValue * PI }
                } catch (e: NumberFormatException) {
                    console.log("Could not parse new phase input: $newPhaseString")
                }
            }
        }

        override fun isEnabled(selectedNodes: List<ZXNode>): Boolean = selectedNodes.firstOrNull() is Spider
    },








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

    CLEAR_NETWORK("New Diagram (N)") {
        override fun onZXAction(selectedNodes: List<ZXNode>) {
            ZXComposer.network.clear()
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

    open fun isEnabled(selectedNodes: List<ZXNode>) = true
}