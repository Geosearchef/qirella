package zxn

import util.math.Vector
import zxn.network.QubitNode
import zxn.network.QubitNode.QubitNodeMode.INPUT
import zxn.network.QubitNode.QubitNodeMode.OUTPUT
import zxn.network.Spider
import zxn.network.Spider.SpiderColor.GREEN
import zxn.network.Spider.SpiderColor.RED
import zxn.network.Wire
import zxn.network.ZXNetwork

object ZXComposer {

    const val NODE_SIZE = 25

    var offset = Vector(200.0, 200.0) // worldSpace = renderSpace
    var scale = 1.0

    var network = ZXNetwork()

    fun init() {
        // swap gate network using 3 CNOTs
        with(network) {
            addNode(QubitNode(Vector(-30.0, 0.0), INPUT))
            addNode(Spider(Vector(0.0, 0.0), GREEN))
            addNode(Spider(Vector(30.0, 0.0), RED))
            addNode(Spider(Vector(60.0, 0.0), GREEN))
            addNode(QubitNode(Vector(90.0, 0.0), OUTPUT))

            addNode(QubitNode(Vector(-30.0, 60.0), INPUT))
            addNode(Spider(Vector(0.0, 60.0), RED))
            addNode(Spider(Vector(30.0, 60.0), GREEN))
            addNode(Spider(Vector(60.0, 60.0), RED))
            addNode(QubitNode(Vector(90.0, 60.0), OUTPUT))

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
        }
    }
}