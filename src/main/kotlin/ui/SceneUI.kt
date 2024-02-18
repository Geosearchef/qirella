package ui

import org.w3c.dom.events.MouseEvent
import rendering.Rendering
import rendering.Rendering.ctx
import rendering.color
import rendering.fillRect
import rendering.fillTextCentered
import ui.UISeparator.Orientation.VERTICAL
import util.math.Rectangle
import util.math.Vector

abstract class SceneUI(val width: Int, val height: Int) {

    abstract val TOP_BAR_SIZE: Double
    open val ACTION_BUTTON_COLOR = "#dddddd"
    open val TOP_BAR_SEPARATOR_SIZE = 0.66

    private val topBarActions = HashMap<UIAction, Rectangle>()
    private var topBarActionsCompleted = false

    private val components = ArrayList<UIComponent>()

    open fun render() {
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, Rendering.width, TOP_BAR_SIZE)

        renderTopBarActions()
        components.forEach { it.render() }
    }

    fun renderAction(rect: Rectangle, action: UIAction) {
        ctx.color(ACTION_BUTTON_COLOR)
        ctx.fillRect(rect.pos, rect.width, rect.height)

        ctx.color("black")
        ctx.fillTextCentered(action.representation, rect.center + Vector(y=2.0))
    }


    fun addComponent(component: UIComponent) {
        components.add(component)
    }

    fun removeComponent(component: UIComponent) {
        components.remove(component)
    }

    fun addTopBarSeparator(x: Double) {
        components.add(UISeparator(Vector(x, TOP_BAR_SIZE / 2.0), TOP_BAR_SIZE * TOP_BAR_SEPARATOR_SIZE, VERTICAL))
    }

    fun renderTopBarActions() {
        if(!topBarActionsCompleted) {
            alignTopBarActions()
        }
        topBarActions.forEach { renderAction(it.value, it.key) }
    }

    fun addTopBarAction(action: UIAction, rect: Rectangle) {
        topBarActions[action] = rect
    }

    private fun alignTopBarActions() {
        if(topBarActionsCompleted) {
            throw IllegalStateException("Top bar actions were already completed")
        }

        // Shift all actions to to the right of the canvas
        val actionsWidth = (topBarActions.values.map { it.x + it.width }.maxOrNull() ?: 0.0) + 10.0
        val actionsStartX = this.width - actionsWidth

        topBarActions.values.forEach { it.x += actionsStartX }

        topBarActionsCompleted = true
    }


    open fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        topBarActions.entries.filter { mousePosition in it.value }.firstOrNull()?.let { it.key }?.let { action ->
            action.onAction()
        }
        components.filter { it.rectangle != null && mousePosition in it.rectangle }.forEach {
            it.onPressed(mousePosition, event)
        }
    }
    abstract fun isMouseEventOnUI(mousePosition: Vector): Boolean
}