package fe.kartifice.builder.assets

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.Identifier

class MultipartBlockStateBuilder {
    private val cases: JsonArray = JsonArray()

    fun whenState(vararg condition: Pair<String, String>, apply: MultipartModelBuilder.() -> Unit) = cases.addObject {
        if (condition.isNotEmpty()) {
            putObject("when") {
                for ((k, v) in condition) put(k, v)
            }
        }
        add("apply", MultipartModelBuilder().apply(apply).build())
    }

    //    infix fun String.to(boolValue : Boolean) = this to boolValue.toString()
    infix fun BooleanProperty.to(boolValue: Boolean) = this.name to boolValue.toString()

    internal fun build() = cases
}

fun MultipartBlockStateBuilder.always(
    model: Identifier,
    x: Int? = null,
    y: Int? = null,
    uvlock: Boolean? = null,
    weight: Int? = null
) = whenState {
    model.applyModel(x, y, uvlock, weight)
}

internal inline fun gsonObject(init: JsonObject.() -> Unit) = JsonObject().apply(init)

internal inline fun JsonObject.put(key: String, value: String) = addProperty(key, value)
internal inline fun JsonObject.putObject(key: String, value: JsonObject.() -> Unit) = add(key, JsonObject().apply(value))
internal inline fun JsonObject.putIfSpecified(key: String, value: String?) = value?.let { addProperty(key, it) }
internal inline fun JsonObject.putIfSpecified(key: String, value: Int?) = value?.let { addProperty(key, value) }
internal inline fun JsonObject.putIfSpecified(key: String, value: Boolean?) = value?.let { addProperty(key, value) }
internal inline fun JsonArray.addObject(init: JsonObject.() -> Unit) = add(gsonObject(init))

class MultipartModelBuilder internal constructor() {
    private val models: JsonArray = JsonArray()
//    fun applyModel(model: Identifier, x: Int? = null, y: Int? = null, uvlock: Boolean? = null, weight: Int? = null) {
//        models.add(gsonObject {
//            put("model", model.toString())
//            putIfSpecified("x", x)
//            putIfSpecified("y", y)
//            putIfSpecified("uvlock", uvlock)
//            putIfSpecified("weight", weight)
//        })
//    }

    fun Identifier.applyModel(x: Int? = null, y: Int? = null, uvlock: Boolean? = null, weight: Int? = null) {
        models.add(gsonObject {
            put("model", "$namespace:block/$path")
            putIfSpecified("x", x)
            putIfSpecified("y", y)
            putIfSpecified("uvlock", uvlock)
            putIfSpecified("weight", weight)
        })
    }

    internal fun build(): JsonElement = if (models.count() == 1) {
        models.first()
            .also { require(it.asJsonObject.get("weight") == null) { "Weight cannot be specified when there is only one model" } }
    } else models
}

