package fe.kartifice.builder.data.recipe

import com.google.gson.JsonObject
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.resource.JsonResource
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * Base builder for a recipe (`namespace:recipes/id.json`).
 * @param <T> this
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Recipe.JSON_format)
</T> */
abstract class RecipeBuilder<T : RecipeBuilder<T>> protected constructor() :
    TypedJsonBuilder<JsonResource<JsonObject>>(JsonObject(), { JsonResource(it) }) {
    /**
     * Set the type of this recipe.
     * @param id The type ID.
     * @return this
     */
    fun type(id: Identifier): T {
        root.addProperty("type", id.toString())
        return this as T
    }

    /**
     * Set the recipe book group of this recipe.
     * @param id The group ID.
     * @return this
     */
    fun group(id: Identifier): T {
        root.addProperty("group", id.toString())
        return this as T
    }
}