package rs.emulate.common.config.kit

import io.netty.buffer.ByteBuf
import rs.emulate.common.config.Config
import rs.emulate.common.config.ConfigDecoder

object IdentikitDefinitionDecoder : ConfigDecoder<IdentikitDefinition> {

    override fun decode(id: Int, buffer: ByteBuf): IdentikitDefinition {
        val definition = IdentikitDefinition(id)
        var opcode = buffer.readUnsignedByte().toInt()

        while (opcode != Config.DEFINITION_TERMINATOR) {
            definition.decode(buffer, opcode)
            opcode = buffer.readUnsignedByte().toInt()
        }

        return definition
    }

    private fun IdentikitDefinition.decode(buffer: ByteBuf, opcode: Int) {
        when (opcode) {
            1 -> part = buffer.readUnsignedByte().toInt()
            2 -> {
                val count = buffer.readUnsignedByte().toInt()
                models = IntArray(count) { buffer.readUnsignedShort() }
            }
            3 -> playerDesignStyle = true
            in 40 until 50 -> originalColours[opcode - 40] = buffer.readUnsignedShort()
            in 50 until 60 -> replacementColours[opcode - 50] = buffer.readUnsignedShort()
            in 60 until 70 -> widgetModels[opcode - 60] = buffer.readUnsignedShort()
        }
    }

}
