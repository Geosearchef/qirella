package zxn.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import util.downloadFile
import util.loadFile
import zxn.network.ZXNetwork
import kotlin.js.Promise

object ZXStorage {

    val module = SerializersModule {  }

    val format = Json { serializersModule = module }

    fun load(): Promise<ZXNetwork> {
        return Promise { resolve, reject ->
            loadFile(listOf(".zxq"), callback = {
                println("Deserializing diagram")

//                val diagram = format.decodeFromString<ZXNetwork>(it)
//                resolve(diagram)

            })
        }
    }

    fun store(diagram: ZXNetwork) {
        println("Serializing and downloading diagram...")
        downloadFile(serializeDiagram(diagram).encodeToByteArray(), "diagram.zxq")
    }

    fun serializeDiagram(diagram: ZXNetwork): String {
        return ""
    }

}