package zxn.network.util

import zxn.network.QubitNode
import zxn.network.QubitNode.QubitNodeMode.INPUT
import zxn.network.QubitNode.QubitNodeMode.OUTPUT
import zxn.network.ZXNetwork
import zxn.network.ZXNode
import kotlin.math.abs

const val NODE_SPACING = 80.0

fun realignNetwork(network: ZXNetwork) {
    val toBeProcessed = ArrayList(network.nodes)
        .filter { it.getNeighbors().isNotEmpty() }
        .sortedBy { it.pos.y }.toMutableList()

    val processed = ArrayList<ZXNode>()

    // assume grid, scale up and translate later
    val center = toBeProcessed.map { it.pos }.reduce { acc, r -> acc + r } / network.nodes.size.toDouble()

    // align inputs according to current order into column -1
    val inputs = toBeProcessed.filter { it is QubitNode && it.mode == INPUT }
    inputs.forEach { it.pos.x = 0.0 }
    inputs.forEachIndexed { index, node -> node.pos.y = index.toDouble() }
    processed.addAll(inputs)
    toBeProcessed.removeAll(inputs)

    var timeStep = 1
    while(toBeProcessed.isNotEmpty()) {
        // Find all nodes that can be placed so that none in the same timestep are connected
        val placedInCurrentTimeStep = ArrayList<ZXNode>()

        for(node in toBeProcessed) {
            val neighbors = node.getNeighbors()
            if(placedInCurrentTimeStep.contains(node)
                || neighbors.any { placedInCurrentTimeStep.contains(it) }
                || neighbors.none { processed.contains(it) }) {
                continue
            }

            // get height based on already placed nodes
            val targetY = neighbors.filter { processed.contains(it) }.map { it.pos.y }.average()

            // find best free spot
            val bestY = ((targetY.toInt() - 20) .. (targetY.toInt() + 20))
                    .filter { candidate -> placedInCurrentTimeStep.none { it.pos.y.toInt() == candidate } }
                    .minByOrNull { abs(targetY - it) }!!

            node.pos.x = timeStep.toDouble()
            node.pos.y = bestY.toDouble()
            placedInCurrentTimeStep.add(node)
        }

        processed.addAll(placedInCurrentTimeStep)
        toBeProcessed.removeAll(placedInCurrentTimeStep)
        timeStep++
    }

    val outputs = processed.filter { it is QubitNode && it.mode == OUTPUT }
    outputs.map { it.pos.x }.maxOrNull()?.let { maxOutputY ->
        outputs.forEach { it.pos.x = maxOutputY }
    }

    // scale back up
    val newCenter = processed.map { it.pos }.reduce { acc, r -> acc + r } / processed.size.toDouble()
    processed.forEach { it.pos *= NODE_SPACING }
    processed.forEach { it.pos += (center - newCenter) }
}