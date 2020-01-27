package fe.kartifice.builder

import com.google.gson.JsonElement

internal fun <T, V : JsonElement> TypedJsonBuilder<T>.addBuilder(key: String, value: TypedJsonBuilder<V>) = root.add(key, value.build())