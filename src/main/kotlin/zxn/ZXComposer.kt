package zxn

import util.math.Vector
import zxn.network.*
import zxn.network.QubitNode.QubitNodeMode.INPUT
import zxn.network.QubitNode.QubitNodeMode.OUTPUT
import zxn.network.Spider.SpiderColor.GREEN
import zxn.network.Spider.SpiderColor.RED

object ZXComposer {

    const val NODE_SIZE = 30.0
    const val NODE_RADIUS = NODE_SIZE / 1.5
    const val QUBIT_RADIUS = NODE_RADIUS / 1.5

    var offset = Vector(200.0, 200.0) // worldSpace = renderSpace
    var scale = 1.0

    var network = ZXNetwork()

    var grabbedNode: ZXNode? = null

    fun init() {
        // swap gate network using 3 CNOTs
        with(network) {
            val spacing = 80.0

            addNode(QubitNode(Vector(-1.0, 0.0) * spacing, INPUT))
            addNode(Spider(Vector(0.0, 0.0) * spacing, GREEN,/* _phase = 0.5*PI*/))
            addNode(Spider(Vector(1.0, 0.0) * spacing, RED))
            addNode(Spider(Vector(2.0, 0.0) * spacing, GREEN))
            addNode(QubitNode(Vector(3.0, 0.0) * spacing, OUTPUT))

            addNode(QubitNode(Vector(-1.0, 2.0) * spacing, INPUT))
            addNode(Spider(Vector(0.0, 2.0) * spacing, RED))
            addNode(Spider(Vector(1.0, 2.0) * spacing, GREEN))
            addNode(Spider(Vector(2.0, 2.0) * spacing, RED))
            addNode(QubitNode(Vector(3.0, 2.0) * spacing, OUTPUT))

            addWire(Wire(nodes[0], nodes[1]))
            addWire(Wire(nodes[1], nodes[2]))
            addWire(Wire(nodes[2], nodes[3]))
            addWire(Wire(nodes[3], nodes[4]))
            addWire(Wire(nodes[5], nodes[6]))
            addWire(Wire(nodes[6], nodes[7]))
            addWire(Wire(nodes[7], nodes[8]))
            addWire(Wire(nodes[8], nodes[9]))

            addWire(Wire(nodes[1], nodes[6]))
            addWire(Wire(nodes[2], nodes[7]))
            addWire(Wire(nodes[3], nodes[8]))

            addNode(ZXHadamardNode(Vector(5.0, 1.0) * spacing))
        }
    }
}