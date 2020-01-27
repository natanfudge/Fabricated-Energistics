package fe.kartifice.builder.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.resource.JsonResource
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * Builder for tag files (`namespace:tags/type/tagid.json`).
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Tag)
 */
class TagBuilder : TypedJsonBuilder<JsonResource<JsonObject>>(JsonObject(), { JsonResource(it) }) {
    /**
     * Set whether this tag should override or append to versions of the same tag in lower priority data packs.
     * @param replace Whether to replace.
     * @return this
     */
    fun replace(replace: Boolean): TagBuilder {
        root.addProperty("replace", replace)
        return this
    }

    /**
     * Add a value to this tag.
     * @param id The value ID.
     * @return this
     */
    fun value(id: Identifier): TagBuilder {
        with("values", ::JsonArray) {  add(id.toString()) }
        return this
    }

    /**
     * Add multiple values to this tag.
     * @param ids The value IDs.
     * @return this
     */
    fun values(vararg ids: Identifier): TagBuilder {
        with("values", ::JsonArray) {
            for (id in ids) add(id.toString())
        }
        return this
    }

    /**
     * Include another tag into this tag's values.
     * @param tagId The tag ID.
     * @return this
     */
    fun include(tagId: Identifier): TagBuilder {
        with("values", ::JsonArray) { add("#$tagId") }
        return this
    }
}