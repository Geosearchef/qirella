package zxn.calculus

import zxn.network.ZXNetwork
import zxn.network.ZXNode

object BialgebraLawB2 : ZXRule("BialgebraLaw", "B2", false) {

    override val inverse: ZXRule = BialgebraLawB2Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        return false
        TODO("Not yet implemented")
    }
}

object BialgebraLawB2Inverse : ZXRule("BialgebraLaw Inv", "B2_I", true) {

    override val inverse: ZXRule = BialgebraLawB2

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        return false
        TODO("Not yet implemented")
    }

}