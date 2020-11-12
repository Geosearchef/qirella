package zxn.ui

import org.w3c.dom.events.MouseEvent
import rendering.Rendering.ctx
import rendering.color
import rendering.fillTextLeft
import ui.SceneUI
import util.math.Rectangle
import util.math.Vector
import util.toDecimals
import zxn.ZXComposer
import zxn.ZXInput
import zxn.ZXRenderer
import zxn.network.QubitNode
import zxn.network.Spider
import zxn.network.ZXHadamardNode
import zxn.network.ZXNode
import kotlin.math.PI

class ZXUI(width: Int, height: Int) : SceneUI(width, height) {

    override val TOP_BAR_SIZE = 80.0

    companion object {
        const val ACTION_WIDTH = 100.0
        const val ACTION_HEIGHT = 25.0
        const val ACTION_SPACING = 10.0

        const val SELECTION_UI_SPACING = 15.0

        const val ADDABLE_NODE_SPACING = 25.0

        const val BOT_BAR_SIZE = 160.0
    }

    val nodeGenerators = HashMap<Rectangle, Pair<ZXNode, ()->ZXNode> >()
    var generatorsEndingX = 0.0

    val selectionActions = HashMap<ZXAction, Rectangle>()


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

        generatorsEndingX = rectangleSupplier.next().x
//        addTopBarSeparator(generatorsEndingX)
        generatorsEndingX += ADDABLE_NODE_SPACING * 2.0
    }

    // init actions
    init {
        val actionIntervalX = ACTION_WIDTH + ACTION_SPACING
        val actionIntervalY = ACTION_HEIGHT + ACTION_SPACING

        //TODO: could be refactored to auto grid
        addTopBarAction(ZXAction.REALIGN_ENTIRE_NETWORK, Rectangle(Vector(actionIntervalX * (-1.0), ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))

        addTopBarAction(ZXAction.DELETE_NODES, Rectangle(Vector(0.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(ZXAction.CREATE_WIRES, Rectangle(Vector(actionIntervalX * 1.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(ZXAction.DELETE_WIRES, Rectangle(Vector(actionIntervalX * 1.0, ACTION_SPACING + actionIntervalY), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(ZXAction.LOAD, Rectangle(Vector(actionIntervalX * 2.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(ZXAction.SAVE, Rectangle(Vector(actionIntervalX * 2.0, ACTION_SPACING + actionIntervalY), ACTION_WIDTH, ACTION_HEIGHT))


        // Selection actions
        val selectionActionsStart = generatorsEndingX + 105.0

        selectionActions[ZXAction.TOGGLE_INPUT_OUTPUT] = Rectangle(Vector(selectionActionsStart, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        selectionActions[ZXAction.TOGGLE_SPIDER_COLOR] = Rectangle(Vector(selectionActionsStart, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        selectionActions[ZXAction.SET_SPIDER_PHASE] = Rectangle(Vector(selectionActionsStart, ACTION_SPACING + actionIntervalY), ACTION_WIDTH, ACTION_HEIGHT)
    }

    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        super.onUIPressed(mousePosition, event)

        nodeGenerators.entries.find { mousePosition in it.key }?.value?.second?.let { nodeGenerator ->
            val newNode = nodeGenerator.invoke().apply { pos = ZXInput.toWorldSpace(mousePosition) }
            ZXComposer.network.addNode(newNode)
            ZXComposer.grabbedNode = newNode
        }

        selectionActions.filter { it.key.isEnabled(ZXComposer.selectedNodes) && mousePosition in it.value }.forEach {
            it.key.onZXAction(ZXComposer.selectedNodes)
        }
    }

    override fun render() {
        super.render()

        // Node generators
        nodeGenerators.forEach { entry ->
            ZXRenderer.renderNode(entry.key.center, entry.value.first)
        }

        //Selection properties
        ctx.font = "12px sans-serif"
        val selectedNode = ZXComposer.selectedNodes.firstOrNull()

//        if(ZXComposer.selectedNodes.isNotEmpty()) {
//            ctx.fillTextLeft("Selected: ${ZXComposer.selectedNodes.size}", Vector(generatorsEndingX, SELECTION_UI_SPACING))
//        }

        if(selectedNode != null) {
            ctx.fillTextLeft("Type: ${selectedNode::class.simpleName?.replace("ZX", "")?.replace("Node", "") ?: "Unknown"}",
                Vector(generatorsEndingX, SELECTION_UI_SPACING + 20.0 * 0.0))

            if(selectedNode is Spider) {
                ctx.fillTextLeft("Color: ${selectedNode.color.name}", Vector(generatorsEndingX, SELECTION_UI_SPACING + 20.0 * 1.0))
                ctx.fillTextLeft("Phase: ${(selectedNode.phase / PI).toDecimals(3)} π", Vector(generatorsEndingX, SELECTION_UI_SPACING + 20.0 * 2.0))
            }
        }

        selectionActions.filter { it.key.isEnabled(ZXComposer.selectedNodes) }.forEach { renderAction(it.value, it.key) }

        //Bottom bar
        val bottomBarY = height - BOT_BAR_SIZE

        ctx.color("#cccccc")
        ctx.fillRect(0.0, bottomBarY, width.toDouble(), BOT_BAR_SIZE)
    }

    override fun renderRule() {
        //TODO: implement rule display, test spider rule
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean = mousePosition.y < TOP_BAR_SIZE
}