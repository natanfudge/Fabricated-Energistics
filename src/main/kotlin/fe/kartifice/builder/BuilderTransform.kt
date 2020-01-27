package fe.kartifice.builder

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.swordglowsblue.artifice.api.builder.assets.ModelBuilder
import fabricktx.api.splitOn

data class BuilderTransform<T>(val type: BuilderType<T>, val transform: TypedJsonBuilder<T>.() -> Unit) {
    companion object {
        private val AddBlockPrefixToBlockModelTextures = BuilderTransform(BuilderType.BlockModel) {
            (this.root.get("textures") as? JsonObject)?.let { textures ->
                textures.entrySet().forEach { (key, value) ->
                    assert(value is JsonPrimitive)
                    val (namespace, path) = value.asString.splitOn(':')
                    textures.addProperty(key, "$namespace:block/$path")
                }
            }
        }
        val DefaultTransforms : List<BuilderTransform<*>> = listOf(
//            AddBlockPrefixToBlockModelTextures
        )
    }
}

sealed class BuilderType<out T> {
    object BlockModel : BuilderType<ModelBuilder>()
}
