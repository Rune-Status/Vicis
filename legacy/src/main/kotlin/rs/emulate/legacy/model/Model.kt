package rs.emulate.legacy.model

/**
 * A representation of a Model stored in the cache.
 *
 * @param textureCoordinates A pool of texture coordinates, projected into viewspace.
 */
data class Model(
    val features: Set<ModelFeature>,
    val faces: Array<Face>,
    val vertices: Array<Vertex>,
    val textureCoordinates: Array<TexCoord>
) {

    /**
     * Check if this `Model` has the given feature enabled.
     */
    fun hasFeature(feature: ModelFeature): Boolean {
        return features.contains(feature)
    }

    fun recolour(map: Map<Int, Int>): Model {
        val faces = faces.map { face ->
            if (face.colour in map) {
                face.copy(colour = map[face.colour]!!)
            } else {
                face
            }
        }.toTypedArray()

        return copy(faces = faces)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Model) {
            if (features != other.features) return false
            if (!faces.contentEquals(other.faces)) return false
            if (!vertices.contentEquals(other.vertices)) return false

            return textureCoordinates.contentEquals(other.textureCoordinates)
        }

        return false
    }

    override fun hashCode(): Int {
        var result = features.hashCode()
        result = 31 * result + faces.contentHashCode()
        result = 31 * result + vertices.contentHashCode()
        return 31 * result + textureCoordinates.contentHashCode()
    }

}
