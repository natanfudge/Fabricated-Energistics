package fe.kartifice.builder

import com.google.gson.JsonArray
import com.google.gson.JsonElement

class JsonArrayBuilder @JvmOverloads constructor(private val root: JsonArray = JsonArray()) {
    fun build(): JsonArray {
        return buildTo(JsonArray())
    }

    fun buildTo(target: JsonArray): JsonArray {
        target.addAll(root)
        return target
    }

    fun add(value: JsonElement?): JsonArrayBuilder {
        root.add(value)
        return this
    }

    fun add(value: String?): JsonArrayBuilder {
        root.add(value)
        return this
    }

    fun add(value: Boolean): JsonArrayBuilder {
        root.add(value)
        return this
    }

    fun add(value: Number?): JsonArrayBuilder {
        root.add(value)
        return this
    }

    fun add(value: Char?): JsonArrayBuilder {
        root.add(value)
        return this
    }

    fun addObject(settings: JsonObjectBuilder.() -> Unit): JsonArrayBuilder {
        root.add(JsonObjectBuilder().apply(settings).build())
        return this
    }

    fun addArray(settings: JsonArrayBuilder.() -> Unit): JsonArrayBuilder {
        root.add(JsonArrayBuilder().apply(settings).build())
        return this
    }

}