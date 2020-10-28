package zxn.ui

import org.w3c.dom.events.MouseEvent
import ui.SceneUI
import util.math.Rectangle
import util.math.Vector
import zxn.ZXComposer
import zxn.ZXInput
import zxn.ZXRenderer
import zxn.network.QubitNode
import zxn.network.Spider
import zxn.network.ZXHadamardNode
import zxn.network.ZXNode

class ZXUI(width: Int, height: Int) : SceneUI(width, height) {

    override val TOP_BAR_SIZE = 80.0

    companion object {
        const val ACTION_WIDTH = 50.0
        const val ACTION_HEIGHT = 25.0
        const val ACTION_SPACING = 10.0

        const val ADDABLE_NODE_SPACING = 25.0
    }

    val nodeGenerators = HashMap<Rectangle, Pair<ZXNode, ()->ZXNode> >()



    // init generators
    init {
        val generatorSize = ZXComposer.NODE_RADIUS * 2.0
        val yPadding = (TOP_BAR_SIZE - generatorSize) / 2.0

        val rectangleSupplier = generateSequence(0, {it+1}).map { i ->
            Rectangle(
                Vector(
                    x = ADDABLE_NODE_SPACING + (ADDABLE_NODE_SPACING + generatorSize) * i,
                    y = yPadding
                ),
                width = generatorSize,
                height = generatorSize
            )
        }.iterator()

        // singleInstanceInvoker maps a function that generates a ZXNode to a Pair of said function and one instance of its invocation
        val singleInstanceInvoker: (()->ZXNode)->Pair<ZXNode, ()->ZXNode> = { Pair(it.invoke(), it) }

        nodeGenerators[rectangleSupplier.next()] = { Spider(Vector(), color = Spider.SpiderColor.GREEN)  }.let(singleInstanceInvoker)
        nodeGenerators[rectangleSupplier.next()] = { Spider(Vector(), color = Spider.SpiderColor.RED)    }.let(singleInstanceInvoker)
        nodeGenerators[rectangleSupplier.next()] = { ZXHadamardNode(Vector())                            }.let(singleInstanceInvoker)
        nodeGenerators[rectangleSupplier.next()] = { QubitNode(Vector(), QubitNode.QubitNodeMode.INPUT)  }.let(singleInstanceInvoker)
        nodeGenerators[rectangleSupplier.next()] = { QubitNode(Vector(), QubitNode.QubitNodeMode.OUTPUT) }.let(singleInstanceInvoker)
    }



    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        // TODO trigger action

        nodeGenerators.entries.find { mousePosition in it.key }?.value?.second?.let { nodeGenerator ->
            val newNode = nodeGenerator.invoke().apply { pos = ZXInput.toWorldSpace(mousePosition) }
            ZXComposer.network.addNode(newNode)
            ZXComposer.grabbedNode = newNode
        }
    }

    override fun render() {
        super.render()

        nodeGenerators.forEach { entry ->
            ZXRenderer.renderNode(entry.key.center, entry.value.first)
        }
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean = mousePosition.y < TOP_BAR_SIZE
}