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
import zxn.network.QubitNode
import zxn.network.Spider
import zxn.network.ZXHadamardNode

object ZXRenderer : Scene.SceneRenderer {


    override fun render() {
        // TODO: render UI
        renderNetwork()
    }

    private fun renderNetwork() {
        ctx.scale(ZXComposer.scale, ZXComposer.scale)
        ctx.translate(ZXComposer.offset.x, ZXComposer.offset.y)

        renderWires()
        renderNodes()

        ctx.setIdentityMatrix()
    }

    fun renderWires() {
        // TODO: how to render / represent multiple wires
        ctx.color("black")
        ctx.lineWidth = 2.0
        network.wires.forEach {
            ctx.drawLine(it.nodes.first.pos, it.nodes.second.pos)
        }
    }

    fun renderNodes() {
        network.nodes.forEach {
            when(it) {
                is Spider -> renderSpider(it.pos, it.color, it.phase)
                is ZXHadamardNode -> renderHadamardNode(it.pos)
                is QubitNode -> renderQubitNode(it.pos, it.mode)
            }
        }
    }

    fun renderSpider(pos: Vector, color: Spider.SpiderColor, phase: Double) {
        ctx.color(color.colorRepresentation)
        ctx.fillCircle(pos, NODE_RADIUS)
        ctx.color("black")
        if(!Spider.isDefaultPhase(phase)) {
            ctx.fillTextCentered(phase.toDecimals(2), pos)
        }
        ctx.lineWidth = 2.0
        ctx.strokeCircle(pos, NODE_RADIUS)
    }

    fun renderQubitNode(pos: Vector, mode: QubitNode.QubitNodeMode) {
        ctx.color("black")
        ctx.fillCircle(pos, QUBIT_RADIUS)
        ctx.fillTextCentered(mode.representation, pos + Vector(y = QUBIT_RADIUS * 1.8))
    }

    fun renderHadamardNode(pos: Vector) {
        ctx.color(ZXHadamardNode.COLOR_REPRESENTATION)
        ctx.fillRect(pos - Vector(NODE_SIZE, NODE_SIZE) / 2.0, NODE_SIZE, NODE_SIZE)
        ctx.color("black")
        ctx.lineWidth = 2.0
        ctx.strokeRect(pos - Vector(NODE_SIZE, NODE_SIZE) / 2.0, NODE_SIZE, NODE_SIZE)
    }
}