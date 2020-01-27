package fe.kartifice.builder.assets

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.kartifice.builder.JsonObjectBuilder
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.builder.addBuilder
import fe.kartifice.resource.JsonResource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier

/**
 * Builder for a model file (`namespace:models/block|item/modelid.json`).
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Model)
 */
@Environment(EnvType.CLIENT)
class ModelBuilder : TypedJsonBuilder<JsonResource<JsonObject>>(JsonObject(), { JsonResource(it) }) {
    /**
     * Set the parent model for this model to inherit from.
     * @param id The parent model ID (`namespace:block|item/modelid`
     * @return this
     */
    internal fun parent(id: Identifier): ModelBuilder {
        root.addProperty("parent", id.toString())
        return this
    }

    /**
     * Associate a texture with the given variable name.
     * @param name The texture variable name.
     * @param path The texture ID (`namespace:type/textureid`).
     * @return this
     */
    fun textures(builder: JsonObjectBuilder.() -> Unit) = addBuilder("textures", JsonObjectBuilder().apply(builder))


    /**
     * Associate a texture with the given variable name.
     * @param name The texture variable name.
     * @param path The texture ID (`namespace:type/textureid`).
     * @return this
     */
    fun texture(name: String, path: Identifier): ModelBuilder {
        with("textures", ::JsonObject) {
            addProperty(name, path.toString())
        }
        return this
    }

    /**
     * Modify the display transformation properties of this model for the given display position.
     * @param name The position name (e.g. `thirdperson_righthand`).
     * @param settings A callback which will be passed a [Display].
     * @return this
     */
    fun display(
        name: String?,
        settings: Display.() -> Unit
    ): ModelBuilder {
        with(
            "display",
            { JsonObject() },
            {
                add(name, Display().apply(settings).build())
            }
        )
        return this
    }

    /**
     * Add an element to this model.
     * @param settings A callback which will be passed a [ModelElementBuilder].
     * @return this
     */
    fun element(settings: ModelElementBuilder.() -> Unit): ModelBuilder {
        with(
            "elements",
            { JsonArray() },
            {
                add(ModelElementBuilder().apply(settings).build())
            }
        )
        return this
    }

    /**
     * Set whether this model should use ambient occlusion for lighting. Only applicable for block models.
     * @param ambientocclusion Whether to use ambient occlusion.
     * @return this
     */
    fun ambientocclusion(ambientocclusion: Boolean): ModelBuilder {
        this.root.addProperty("ambientocclusion", ambientocclusion)
        return this
    }

    /**
     * Add a property override to this model. Only applicable for item models.
     * @param settings A callback which will be passed a [Override].
     * @return this
     */
    fun override(settings: Override.() -> Unit): ModelBuilder {
        with(
            "overrides",
            { JsonArray() },
            {
                Override().apply(settings).build()
            }
        )
        return this
    }

    /**
     * Builder for model display settings.
     * @see ModelBuilder
     */
    @Environment(EnvType.CLIENT)
    class Display : TypedJsonBuilder<JsonObject>(JsonObject(), { it }) {
        /**
         * Set the rotation of this model around each axis.
         * @param x The rotation around the X axis.
         * @param y The rotation around the Y axis.
         * @param z The rotation around the Z axis.
         * @return this
         */
        fun rotation(x: Float, y: Float, z: Float): Display {
            root.add("rotation", arrayOf(x, y, z))
            return this
        }

        /**
         * Set the translation of this model along each axis.
         * @param x The translation along the X axis. Clamped to between -80 and 80.
         * @param y The translation along the Y axis. Clamped to between -80 and 80.
         * @param z The translation along the Z axis. Clamped to between -80 and 80.
         * @return this
         */
        fun translation(x: Float, y: Float, z: Float): Display {
            root.add("translation", arrayOf(x, y, z))
            return this
        }

        /**
         * Set the scale of this model on each axis.
         * @param x The scale on the X axis. Clamped to &lt;= 4.
         * @param y The scale on the Y axis. Clamped to &lt;= 4.
         * @param z The scale on the Z axis. Clamped to &lt;= 4.
         * @return this
         */
        fun scale(x: Float, y: Float, z: Float): Display {
            root.add("scale", arrayOf(x, y, z))
            return this
        }
    }

    /**
     * Builder for an item model property override.
     * @see ModelBuilder
     */
    @Environment(EnvType.CLIENT)
    class Override : TypedJsonBuilder<JsonObject>(JsonObject(), { it }) {
        /**
         * Set the required value of the given property.
         * Calling this multiple times will require all properties to match.
         * @param name The item property tag.
         * @param value The required integer value.
         * @return this
         * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Model.Item_tags)
         */
        fun predicate(name: String?, value: Int): Override {
            with(
                "predicate",
                { JsonObject() },
                {
                    addProperty(name, value)
                }
            )
            return this
        }

        /**
         * Set the model to be used instead of this one if the predicate matches.
         * @param id The model id (`namespace:block|item/modelid`).
         * @return this
         */
        fun model(id: Identifier): Override {
            root.addProperty("model", id.toString())
            return this
        }
    }
}