package zxn

import rendering.*
import rendering.Rendering.ctx
import scene.Scene
import util.math.Vector
import util.toDecimals
import zxn.ZXComposer.NODE_RADIUS
import zxn.ZXComposer.NODE_SIZE
import zxn.ZXComposer.QUBIT_RADIUS
import zxn.ZXComposer.network
import zxn.ZXComposer.selectedNodes
import zxn.network.QubitNode
import zxn.network.Spider
import zxn.network.ZXHadamardNode
import zxn.network.ZXNode

object ZXRenderer : Scene.SceneRenderer {

    const val SELECTION_COLOR = "#4fb3ff"

    override fun render() {
        renderNetwork()
    }

    private fun renderNetwork() {
        ctx.scale(ZXComposer.scale, ZXComposer.scale)
        ctx.translate(ZXComposer.offset.x, ZXComposer.offset.y)

        renderWires()
        renderNodes()
        renderAreaSelection()

        ctx.setIdentityMatrix()
    }

    private fun renderWires() {
        // TODO: how to render / represent multiple
        ctx.lineWidth = 2.0
        network.wires.forEach {
            if(selectedNodes.containsAll(it.nodeSet)) {
                ctx.color(SELECTION_COLOR)
            } else {
                ctx.color("black");
            }
            ctx.drawLine(it.nodes.first.pos, it.nodes.second.pos)
        }
    }

    private fun renderNodes() = network.nodes.forEach { renderNode(it.pos, it, selectedNodes.contains(it)) }

    fun renderNode(pos: Vector, node: ZXNode, selected: Boolean = false) {
        when (node) {
            is Spider -> renderSpider(pos, node.color, node.phase, selected)
            is ZXHadamardNode -> renderHadamardNode(pos, selected)
            is QubitNode -> renderQubitNode(pos, node.mode, selected)
        }
    }
    private fun renderSpider(pos: Vector, color: Spider.SpiderColor, phase: Double, selected: Boolean) {
        ctx.color(color.colorRepresentation)
        ctx.fillCircle(pos, NODE_RADIUS)
        ctx.color("black")
        if(!Spider.isDefaultPhase(phase)) {
            ctx.fillTextCentered(phase.toDecimals(2), pos)
        }

        ctx.color(if(selected) SELECTION_COLOR else "black")
        ctx.lineWidth = 2.0
        ctx.strokeCircle(pos, NODE_RADIUS)
    }

    private fun renderQubitNode(pos: Vector, mode: QubitNode.QubitNodeMode, selected: Boolean) {
        ctx.color("black")
        ctx.fillCircle(pos, QUBIT_RADIUS)
        ctx.fillTextCentered(mode.representation, pos + Vector(y = QUBIT_RADIUS * 1.8))

        if(selected) {
            ctx.color(SELECTION_COLOR)
            ctx.strokeCircle(pos, QUBIT_RADIUS)
        }
    }

    private fun renderHadamardNode(pos: Vector, selected: Boolean) {
        ctx.color(ZXHadamardNode.COLOR_REPRESENTATION)
        ctx.fillRect(pos - Vector(NODE_SIZE, NODE_SIZE) / 2.0, NODE_SIZE, NODE_SIZE)

        ctx.color(if(selected) SELECTION_COLOR else "black")
        ctx.lineWidth = 2.0
        ctx.strokeRect(pos - Vector(NODE_SIZE, NODE_SIZE) / 2.0, NODE_SIZE, NODE_SIZE)
    }

    private fun renderAreaSelection() {
        if(ZXInput.selectionAreaMode) {
            ctx.color(SELECTION_COLOR)
            ctx.globalAlpha = 0.4
            ctx.fillRect(ZXInput.selectionAreaStart, ZXInput.mousePositionWorld)
            ctx.globalAlpha = 1.0
        }
    }
}
