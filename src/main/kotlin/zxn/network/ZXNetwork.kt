package zxn.network

class ZXNetwork {

    val nodes: MutableList<ZXNode> = ArrayList()
    val wires: MutableList<Wire> = ArrayList()


    fun addNode(node: ZXNode) {
        nodes.add(node)
    }

    fun removeNode(node: ZXNode) {
        nodes.remove(node)
    }

    fun addWire(wire: Wire) {
        wires.add(wire)
    }

    fun removeWire(wire: Wire) {
        wires.remove(wire)
    }
}