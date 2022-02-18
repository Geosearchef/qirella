package zxn

import Qirella
import scene.UIManager
import ui.SceneUI
import util.math.Vector
import zxn.calculus.ZXRule
import zxn.network.*
import zxn.network.QubitNode.QubitNodeMode.INPUT
import zxn.network.QubitNode.QubitNodeMode.OUTPUT
import zxn.network.Spider.SpiderColor.GREEN
import zxn.network.Spider.SpiderColor.RED
import zxn.ui.ZXUI

object ZXComposer : UIManager {

    const val NODE_SIZE = 30.0
    const val NODE_RADIUS = NODE_SIZE / 1.5
    const val QUBIT_RADIUS = NODE_RADIUS / 1.5

    var offset = Vector(200.0, 200.0) // worldSpace = renderSpace
    var scale = 1.0

    var network = ZXNetwork()

    
    var grabbedNode: ZXNode? = null
    var grabOffset = Vector()
    var selectedNodes: MutableList<ZXNode> = ArrayList()

    var uiInstance = ZXUI(300, 200)

    var greyscaleMode = false

    fun init() {
        // swap gate network using 3 CNOTs
        with(network) {
            val spacing = 80.0

            //CNOT
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


            // Hopf circuit
            for (y in listOf(0.0, 2.0)) {
                val hopf1Input = QubitNode(Vector(8.0, y) * spacing, INPUT).also(::addNode)
                val hopf1SpiderGreen = Spider(Vector(10.0, y) * spacing, GREEN).also(::addNode)
                val hopf1SpiderRed = Spider(Vector(12.0, y) * spacing, RED).also(::addNode)
                val hopf1Output = QubitNode(Vector(14.0, y) * spacing, OUTPUT).also(::addNode)
                addWire(hopf1Input, hopf1SpiderGreen)
                if(y == 0.0) {
                    repeat(2) { addWire(hopf1SpiderGreen, hopf1SpiderRed) }
                }
                addWire(hopf1SpiderRed, hopf1Output)
            }
        }
    }

    fun selectNode(node: ZXNode) {
        if(node.selectable && !selectedNodes.contains(node)) {
            selectedNodes.add(node)
        }
    }

    fun deselectAllNodes() {
        selectedNodes.clear()
    }

    fun createSelectedWires() {
        if(selectedNodes.size == 1) {
            // create self wire
            network.addWire(Wire(selectedNodes.first(), selectedNodes.first()))
        } else {
            for(i in 0 until selectedNodes.size) {
                for(j in i+1 until selectedNodes.size) {
                    network.addWire(Wire(selectedNodes[i], selectedNodes[j]))
                }
            }
        }
    }

    fun deleteSelectedWires() {
        network.wires.filter { selectedNodes.containsAll(it.nodeSet) }.forEach { network.removeWire(it) }
    }

    fun deleteSelectedNodes() {
        selectedNodes.forEach { network.removeNode(it) }
    }

    fun removeNode(node: ZXNode) {
        network.removeNode(node)
        selectedNodes.remove(node)
    }

    fun realignNetwork() {
        zxn.network.util.realignNetwork(network)
    }

    fun applyRule(rule: ZXRule) {
        val result = rule.apply(selectedNodes, network)
        if(!result) {
            console.log("Rule is not applicable in this context")
        }

        deselectAllNodes()
        Qirella.requestRender()
    }

    override fun regenerateUI(width: Int, height: Int) {
        uiInstance = ZXUI(width, height)
    }

    override fun getUI(): SceneUI {
        return uiInstance
    }
}