package zxn.calculus

import zxn.network.*

abstract class ZXRule(val longName: String, val shortName:String, val isInverse: Boolean) {

    companion object {
        val allRules = listOf(
            SpiderRule1,
            SpiderRule2,
            CopyingRuleB1
        )
    }

    val ruleNetwork = ZXNetwork(representsRule = true)
    abstract val inverse: ZXRule

    //TODO: presentation
    //TODO: specification
    //TODO: detection
    //TODO: application

    fun isApplicable(selectedNodes: List<ZXNode>, network: ZXNetwork) = apply(selectedNodes, network, dryRun = true)
    abstract fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean = false): Boolean


    fun List<ZXNode>.spiders() = this.filterIsInstance<Spider>()
    fun List<ZXNode>.spiders(color: Spider.SpiderColor) = spiders().filter { it.color == color }
    fun List<ZXNode>.hadamards() = this.filterIsInstance<ZXHadamardNode>()
    fun List<ZXNode>.qubits() = this.filterIsInstance<QubitNode>()
    fun List<ZXNode>.inputs() = qubits().filter { it.mode == QubitNode.QubitNodeMode.INPUT }
    fun List<ZXNode>.outputs() = qubits().filter { it.mode == QubitNode.QubitNodeMode.OUTPUT }
}
