package rs.emulate.editor.workspace.resource

import rs.emulate.editor.workspace.resource.index.ResourceIndexBuilder

class ResourceCache(val bundles: List<ResourceBundle<*>> = emptyList()) {
    val indexTypeMap = bundles.associateBy { it.idType }

    fun index() = bundles.fold(ResourceIndexBuilder()) { builder, bundle ->
        bundle.index(builder)
        builder
    }.build()

    fun <T: ResourceId> load(resourceId: T): Resource? {
        val resourceBundle = indexTypeMap[resourceId::class] as ResourceBundle<T>?
        return resourceBundle?.load(resourceId)
    }
}