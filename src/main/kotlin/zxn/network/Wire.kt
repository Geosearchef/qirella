package zxn.network

open class Wire(val nodes: Pair<ZXNode, ZXNode>) {

    constructor(first: ZXNode, second: ZXNode) : this(Pair(first, second))

    val nodeSet: Set<ZXNode> get() = setOf(nodes.first, nodes.second)

}