package storage

import Composer
import circuit.Circuit
import util.downloadFile
import util.loadFile

object Storage {

    @OptIn(ExperimentalStdlibApi::class)
    fun store() {
        // TODO: cyclic, contains references between components, need to manually serialize
        downloadFile(JSON.stringify(Composer.circuit).encodeToByteArray(), "test.qir")
    }

    fun load() {
        // TODO: cyclic, contains references between components, need to manually serialize
        loadFile(listOf(".qir"), callback = {
            Composer.circuit = JSON.parse<Circuit>(it)
        })
    }
}