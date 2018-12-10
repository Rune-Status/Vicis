package rs.emulate.legacy.model

import io.netty.buffer.ByteBuf

/**
 * Feature flags that control how [Model]s are rendered, and what extra attributes they may have.
 */
sealed class ModelFeature {

    /**
     * Indicates that a model uses the same render priority for all of its [Face]s.
     */
    data class GlobalFaceRenderPriority(val priority: Int) : ModelFeature()

    /**
     * Indicates that a model is textured.
     */
    object FaceTextures : ModelFeature()

    /**
     * Indicates that a model has transparent [Face]s.
     */
    object FaceTransparency : ModelFeature()

    /**
     * Indicates that a model has [Face]s that are mapped to bones in a skeleton.
     */
    object FaceSkinning : ModelFeature()

    /**
     * Indicates that a model has [Vertex]es that are mapped to bones in a skeleton.
     */
    object VertexSkinning : ModelFeature()

    companion object {
        fun decodeFrom(header: ByteBuf): Set<ModelFeature> {
            val features = mutableSetOf<ModelFeature>()

            if (header.readBoolean()) features += FaceTextures

            header.readUnsignedByte().toInt()
                .let { if (it != INDIVIDUAL_PRIORITIES) features += GlobalFaceRenderPriority(it) }

            if (header.readBoolean()) features += FaceTransparency
            if (header.readBoolean()) features += FaceSkinning
            if (header.readBoolean()) features += VertexSkinning

            return features

        }

        private const val INDIVIDUAL_PRIORITIES = 255
    }

}
