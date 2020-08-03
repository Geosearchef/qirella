package circuit

enum class GateType(val representation: String, val matrix : Any?) {
    PAULI_X("X", null),
    PAULI_Y("Y", null),
    PAULI_Z("Z", null),
    HADAMARD("H", null),
    CUSTOM("", null) {
        override fun apply(stateVector: Any?): Any? {
            return super.apply(stateVector)
        }
    };

    open fun apply(stateVector : Any?) : Any? {
        //TODO

        return null
    }
}