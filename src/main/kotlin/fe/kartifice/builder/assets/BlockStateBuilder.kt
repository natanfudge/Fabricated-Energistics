package fe.kartifice.builder.assets

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.kartifice.builder.JsonObjectBuilder
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.builder.assets.BlockStateBuilder.Case
import fe.kartifice.resource.JsonResource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier

/**
 * Builder for a blockstate definition file (`namespace:blockstates/blockid.json`).
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Model.Block_states)
 */
@Environment(EnvType.CLIENT)
class BlockStateBuilder : TypedJsonBuilder<JsonResource<JsonObject>>(JsonObject(), { JsonResource(it) }) {
    /**
     * Add a variant for the given state key.
     * Calling this multiple times for the same key will modify the existing value.
     * `variant` and `multipart` are incompatible; calling this will remove any existing `multipart` definitions.
     *
     * @param name The state key (`""` for default or format: `"prop1=value,prop2=value"`).
     * @param settings A callback which will be passed a [BlockStateVariantBuilder].
     * @return this
     */
    fun variant(
        name: String,
        model: Identifier,
        settings: BlockStateVariantBuilder.() -> Unit = {}
    ): BlockStateBuilder {
        root.remove("multipart")
        with("variants", ::JsonObject) {
            with(name, ::JsonObject) {
                BlockStateVariantBuilder(this).apply {
                    model(model)
                    settings()
                }.buildTo(this)
            }
        }
        return this
    }

    /**
     * Add a variant for the given state key, with multiple weighted random options.
     * Calling this multiple times for the same key will add to the list instead of overwriting.
     * `variant` and `multipart` are incompatible; calling this will remove any existing `multipart` definitions.
     *
     * @param name The state key (`""` for default or format: `"prop1=value,prop2=value"`).
     * @param settings A callback which will be passed a [BlockStateVariantBuilder].
     * @return this
     */
    fun weightedVariant(
        name: String,
        settings: BlockStateVariantBuilder.() -> Unit
    ): BlockStateBuilder {
        root.remove("multipart")
        with("variants", ::JsonObject) {
            with(name, ::JsonArray) {
                add(BlockStateVariantBuilder().apply(settings).build())
            }
        }
        return this
    }

    fun multipart(init: MultipartBlockStateBuilder.() -> Unit)
            = root.add("multipart", MultipartBlockStateBuilder().apply(init).build())

    /**
     * Add a multipart case.
     * Calling this multiple times will add to the list instead of overwriting.
     * `variant` and `multipart` are incompatible; calling this will remove any existing `variant` definitions.
     *
     * @param settings A callback which will be passed a [Case].
     * @return this
     */
    @Deprecated("Use multipart{}")
    fun multipartCase(settings: Case.() -> Unit): BlockStateBuilder {
        root.remove("variants")
        with("multipart", ::JsonArray) {
            add(Case().apply(settings).build())
        }
        return this
    }


    /**
     * Builder for a blockstate multipart case.
     * @see BlockStateBuilder
     */
    @Environment(EnvType.CLIENT)
    class Case : TypedJsonBuilder<JsonObject?>(JsonObject(), { it }) {
        /**
         * Set the condition for this case to be applied.
         * Calling this multiple times with different keys will require all of the specified properties to match.
         * @param name The state name (e.g. `facing`).
         * @param state The state value (e.g. `north`).
         * @return this
         */
        fun whenState(name: String, state: String): Case {
            with("when", ::JsonObject) {
                remove("OR")
                addProperty(name, state)
            }
            return this
        }

        /**
         * Set the condition for this case to be applied.
         * Calling this multiple times with different keys will require at least one of the specified properties to match.
         * @param name The state name (e.g. `facing`).
         * @param state The state value (e.g. `north`).
         * @return this
         */
        fun whenAny(name: String, state: String): Case {
            with("when", ::JsonObject) {
                with("OR", ::JsonArray) {
                    entrySet().forEach { e ->
                        if (e.key != "OR") remove(e.key)
                    }
                    add(JsonObjectBuilder().add(name, state).build())
                }
            }
            return this
        }

        /**
         * Set the variant to be applied if the condition matches.
         * Calling this multiple times for the same key will overwrite the existing value.
         * @param settings A callback which will be passed a [BlockStateVariantBuilder].
         * @return this
         */
        fun apply(settings: BlockStateVariantBuilder.() -> Unit): Case {
            root.add("apply", BlockStateVariantBuilder().apply(settings).build())
            return this
        }

        /**
         * Set the variant to be applied if the condition matches, with multiple weighted random options.
         * Calling this multiple times will add to the list instead of overwriting.
         * @param settings A callback which will be passed a [BlockStateVariantBuilder].
         * @return this
         */
        fun weightedApply(settings: BlockStateVariantBuilder.() -> Unit): Case {
            with("apply", ::JsonArray) {
                add(BlockStateVariantBuilder().apply(settings).build())
            }
            return this
        }
    }
}

/**
 * Builder for a blockstate variant definition.
 * @see BlockStateBuilder
 */
@Environment(EnvType.CLIENT)
class BlockStateVariantBuilder : TypedJsonBuilder<JsonObject> {
    constructor() : super(JsonObject(), { it })

    constructor(root: JsonObject) : super(root, { it })

    /**
     * Set the model this variant should use.
     * @param id The model ID (`namespace:block|item/modelid`).
     * @return this
     */
    internal fun model(id: Identifier): BlockStateVariantBuilder {
        root.addProperty("model", id.namespace + ":block/" + id.path)
        return this
    }

    /**
     * Set the rotation of this variant around the X axis in increments of 90deg.
     * @param x The X axis rotation.
     * @return this
     * @throws IllegalArgumentException if `x` is not divisible by 90.
     */
    fun rotationX(x: Int): BlockStateVariantBuilder {
        require(x % 90 == 0) { "X rotation must be in increments of 90" }
        root.addProperty("x", x)
        return this
    }

    /**
     * Set the rotation of this variant around the Y axis in increments of 90deg.
     * @param y The Y axis rotation.
     * @return this
     * @throws IllegalArgumentException if `y` is not divisible by 90.
     */
    fun rotationY(y: Int): BlockStateVariantBuilder {
        require(y % 90 == 0) { "Y rotation must be in increments of 90" }
        root.addProperty("y", y)
        return this
    }

    /**
     * Set whether the textures of this model should not rotate with it.
     * @param uvlock Whether to lock texture rotation or not.
     * @return this
     */
    fun uvlock(uvlock: Boolean): BlockStateVariantBuilder {
        root.addProperty("uvlock", uvlock)
        return this
    }

    /**
     * Set the relative weight of this variant.
     * This property will be ignored if the variant is not added with [BlockStateBuilder.weightedVariant]
     * or [Case.weightedApply].
     * @param weight The weight.
     * @return this
     */
    fun weight(weight: Int): BlockStateVariantBuilder {
        root.addProperty("weight", weight)
        return this
    }
}