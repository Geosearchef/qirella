package zxn.calculus

import util.math.equalsNumerically
import zxn.network.*
import kotlin.math.PI

abstract class ZXRule(val longName: String, val shortName:String, val isInverse: Boolean) {

    companion object {
        val allRules = listOf(
                SpiderRule1,
                SpiderRule2,
                CopyingRuleB1,
                HopfLawH,
                BialgebraLawB2,
                PiCopyRule,
                PiCommutationRule
        )
    }

    val ruleNetwork = ZXNetwork(representsRule = true)
    abstract val inverse: ZXRule


    fun isApplicable(selectedNodes: List<ZXNode>, network: ZXNetwork) = apply(selectedNodes, network, dryRun = true)
    abstract fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean = false): Boolean


    protected fun verifyCount(selectedNodes: List<ZXNode>, count: Int) = selectedNodes.size == count
    protected fun verifySpiderCount(selectedNodes: List<ZXNode>, count: Int) = verifyCount(selectedNodes, count) && selectedNodes.all { it is Spider }
    protected fun verifySpiderZeroPhaseCount(selectedNodes: List<ZXNode>, count: Int) = verifyCount(selectedNodes, count) && selectedNodes.all { (it as? Spider)?.phase?.equalsNumerically(0.0, 0.001) ?: false }
    protected fun verifySpiderPiPhaseCount(selectedNodes: List<ZXNode>, count: Int) = verifyCount(selectedNodes, count) && selectedNodes.all { (it as? Spider)?.phase?.equalsNumerically(PI, 0.001) ?: false }

    fun List<ZXNode>.spiders() = this.filterIsInstance<Spider>()
    fun List<ZXNode>.spiders(color: Spider.SpiderColor) = spiders().filter { it.color == color }
    fun List<ZXNode>.hadamards() = this.filterIsInstance<ZXHadamardNode>()
    fun List<ZXNode>.qubits() = this.filterIsInstance<QubitNode>()
    fun List<ZXNode>.inputs() = qubits().filter { it.mode == QubitNode.QubitNodeMode.INPUT }
    fun List<ZXNode>.outputs() = qubits().filter { it.mode == QubitNode.QubitNodeMode.OUTPUT }
}
