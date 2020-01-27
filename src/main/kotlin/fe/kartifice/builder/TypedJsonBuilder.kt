package fe.kartifice.builder

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.function.Function
import java.util.function.Supplier

abstract class TypedJsonBuilder<out T> protected constructor(
    internal val root: JsonObject,
    private val ctor: (JsonObject) -> T
) {
    fun build(): T {
        return buildTo(JsonObject())
    }

    fun buildTo(target: JsonObject): T {
        root.entrySet().forEach { e ->
            target.add(e.key, e.value)
        }
        return ctor(target)
    }

    protected fun <J : JsonElement> JsonObject.with(key: String, ctor: () -> J, run: J.() -> Unit) {
        add(key, (if (has(key)) get(key) as J else ctor()).apply(run))
    }

    protected fun <J : JsonElement> with(key: String, ctor: () -> J, run: J.() -> Unit) {
        root.with(key, ctor, run)
    }

    protected fun arrayOf(vararg values: Boolean): JsonArray {
        val array = JsonArray()
        for (i in values) array.add(i)
        return array
    }

    protected fun arrayOf(vararg values: Char?): JsonArray {
        val array = JsonArray()
        for (i in values) array.add(i)
        return array
    }

    protected fun arrayOf(vararg values: Number?): JsonArray {
        val array = JsonArray()
        for (i in values) array.add(i)
        return array
    }

    protected fun arrayOf(vararg values: String?): JsonArray {
        val array = JsonArray()
        for (i in values) array.add(i)
        return array
    }

}

abstract class Builder<IN, OUT> protected constructor(
    internal val root: IN,
    private val ctor: (IN) -> OUT
) {
    abstract fun build(): OUT

//    fun buildTo(target: JsonObject): T {
//        root.entrySet().forEach { e ->
////            target.add(e.key, e.value)
////        }
////        return ctor(target)
////    }
//
//    protected fun <J : JsonElement> JsonObject.with(key: String, ctor: () -> J, run: J.() -> Unit) {
//        add(key, (if (has(key)) get(key) as J else ctor()).apply(run))
//    }
//
////    protected fun <J : JsonElement> with(key: String, ctor: () -> J, run: J.() -> Unit) {
////        root.with(key, ctor, run)
////    }
//
//    protected fun arrayOf(vararg values: Boolean): JsonArray {
//        val array = JsonArray()
//        for (i in values) array.add(i)
//        return array
//    }
//
//    protected fun arrayOf(vararg values: Char?): JsonArray {
//        val array = JsonArray()
//        for (i in values) array.add(i)
//        return array
//    }
//
//    protected fun arrayOf(vararg values: Number?): JsonArray {
//        val array = JsonArray()
//        for (i in values) array.add(i)
//        return array
//    }

    protected fun arrayOf(vararg values: String?): JsonArray {
        val array = JsonArray()
        for (i in values) array.add(i)
        return array
    }

}