package rs.emulate.util.compression

import com.google.common.io.ByteStreams
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.DataFormatException
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

object Gzip {
    const val HEADER = 0x1F8B
}

fun ByteBuf.gunzip(): ByteBuf {
    val data = ByteArray(readableBytes())
    readBytes(data)

    GZIPInputStream(ByteArrayInputStream(data)).use { decompressor ->
        ByteArrayOutputStream().use { out ->
            while (true) {
                val buf = ByteArray(1024)
                val read = decompressor.read(buf, 0, buf.size)
                if (read == -1) {
                    break
                }

                out.write(buf, 0, read)
            }

            return Unpooled.wrappedBuffer(out.toByteArray())
        }
    }
}

fun ByteBuf.gunzip(uncompressedLength: Int): ByteBuf {
    val inflater = Inflater(true)

    if (getUnsignedShort(readerIndex()) != Gzip.HEADER) {
        throw IOException("Invalid GZIP header")
    }

    val off: Int
    val len: Int
    var bytes: ByteArray

    if (hasArray()) {
        len = readableBytes()
        off = arrayOffset() + readerIndex()
        bytes = array()
    } else {
        len = readableBytes()
        off = 0
        bytes = ByteArray(len)
        readBytes(bytes)
    }

    inflater.setInput(bytes, off + 10, len - 18)
    bytes = ByteArray(uncompressedLength)

    try {
        inflater.inflate(bytes)
    } catch (ex: DataFormatException) {
        throw IOException(ex)
    }

    return Unpooled.wrappedBuffer(bytes).asReadOnly()
}

fun ByteBuf.gzip(): ByteBuf {
    val output = Unpooled.buffer()

    ByteBufInputStream(this).use { inputStream ->
        GzipCompressorOutputStream(ByteBufOutputStream(output)).use { outputStream ->
            ByteStreams.copy(inputStream, outputStream)
        }
    }

    return output.asReadOnly()
}
