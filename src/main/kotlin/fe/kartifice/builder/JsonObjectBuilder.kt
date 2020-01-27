package fe.kartifice.builder

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.util.Identifier

open class JsonObjectBuilder : TypedJsonBuilder<JsonObject> {
    constructor() : super(JsonObject(), { it })


    constructor(root: JsonObject) : super(root, { it })


    @Deprecated("Use valued")
    fun add(name: String, value: JsonElement): JsonObjectBuilder {
        root.add(name, value)
        return this
    }

    @Deprecated("Use valued")
    fun add(name: String, value: String): JsonObjectBuilder {
        root.addProperty(name, value)
        return this
    }

    @Deprecated("Use valued")
    fun add(name: String, value: Boolean): JsonObjectBuilder {
        root.addProperty(name, value)
        return this
    }

    @Deprecated("Use valued")
    fun add(name: String, value: Number): JsonObjectBuilder {
        root.addProperty(name, value)
        return this
    }

    @Deprecated("Use valued")
    fun add(name: String, value: Char): JsonObjectBuilder {
        root.addProperty(name, value)
        return this
    }

    private infix fun String.valued(value: String) {
        root.addProperty(this, value)
    }

    infix fun String.valued(id: Identifier) = valued(id.toString())

    fun addObject(
        name: String,
        settings: JsonObjectBuilder.() -> Unit
    ): JsonObjectBuilder {
        root.add(name, JsonObjectBuilder().apply(settings).build())
        return this
    }

    fun addArray(
        name: String,
        settings: JsonArrayBuilder.() -> Unit
    ): JsonObjectBuilder {
        root.add(name, JsonArrayBuilder().apply(settings).build())
        return this
    }
}