package rs.emulate.legacy.config.floor

import rs.emulate.legacy.config.Config
import rs.emulate.legacy.config.ConfigDecoder
import rs.emulate.util.getAsciiString
import rs.emulate.util.getUnsignedByte
import rs.emulate.util.getUnsignedTriByte
import java.nio.ByteBuffer

object FloorDefinitionDecoder : ConfigDecoder<FloorDefinition> {

    override val entryName: String = "flo"

    override fun decode(id: Int, buffer: ByteBuffer): FloorDefinition {
        val definition = FloorDefinition(id)
        var opcode = buffer.getUnsignedByte()

        while (opcode != Config.DEFINITION_TERMINATOR) {
            definition.decode(buffer, opcode)
            opcode = buffer.getUnsignedByte()
        }

        return definition
    }

    private fun FloorDefinition.decode(buffer: ByteBuffer, opcode: Int) {
        when (opcode) {
            1 -> rgb = buffer.getUnsignedTriByte()
            2 -> texture = buffer.getUnsignedByte()
            3 -> return /* unused */
            5 -> occludes = false
            6 -> buffer.getAsciiString()
            7 -> minimapColour = buffer.getUnsignedTriByte()
        }
    }

}
