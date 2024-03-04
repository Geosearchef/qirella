package qcn.storage

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import qcn.circuit.*
import util.downloadFile
import util.loadFile
import kotlin.js.Promise

object QCStorage {

    val module = SerializersModule {
        polymorphic(CircuitComponent::class) {
            subclass(GateComponent::class, GateComponent.serializer())
            subclass(MeasurementComponent::class, MeasurementComponent.serializer())
            subclass(ControlComponent::class, ControlComponent.serializer())
        }
    }

    val format = Json { serializersModule = module }

    @OptIn(ExperimentalStdlibApi::class)
    fun store(circuit: Circuit) {
        println("Serializing and downloading circuit...")
        downloadFile(serializeCircuit(circuit).encodeToByteArray(), "test.qir")
    }

    fun load() : Promise<Circuit> {
        return Promise { resolve, reject ->
            loadFile(listOf(".qir"), callback = {
                println("Deserializing circuit...")

                val circuit = format.decodeFromString<Circuit>(it)

                // set controlled gates
                circuit.components.filterIsInstance<ControlComponent>().forEach { control ->
                    control.controlledGate = circuit.components.filterIsInstance<GateComponent>().find { it.pos == control.controlledGate?.pos }
                    control.controlledGate?.controlComponents?.add(control)
                    if(control.controlledGate == null) {
                        reject(NoSuchElementException("No gate found for controlled gate"))
                    }
                }

                resolve(circuit)
            })
        }
    }

    fun serializeCircuit(circuit: Circuit) : String {
        return format.encodeToString(circuit)
    }

}