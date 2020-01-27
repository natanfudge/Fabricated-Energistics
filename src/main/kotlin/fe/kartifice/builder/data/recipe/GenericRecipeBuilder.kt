package fe.kartifice.builder.data.recipe

import com.google.gson.JsonElement
import fe.kartifice.builder.JsonArrayBuilder
import fe.kartifice.builder.JsonObjectBuilder

/**
 * Builder for a recipe of an unknown type (`namespace:recipes/id.json`)
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Recipe.JSON_format)
 */
class GenericRecipeBuilder :
    RecipeBuilder<GenericRecipeBuilder>() {
    /**
     * Add a JSON element to this recipe.
     * @param name The key.
     * @param value The value.
     * @return this
     */
    fun add(name: String, value: JsonElement): RecipeBuilder<*> {
        root.add(name, value)
        return this
    }

    /**
     * Add a string to this recipe.
     * @param name The key.
     * @param value The value.
     * @return this
     */
    fun add(name: String, value: String): RecipeBuilder<*> {
        root.addProperty(name, value)
        return this
    }

    /**
     * Add a boolean to this recipe.
     * @param name The key.
     * @param value The value.
     * @return this
     */
    fun add(name: String, value: Boolean): RecipeBuilder<*> {
        root.addProperty(name, value)
        return this
    }

    /**
     * Add a number to this recipe.
     * @param name The key.
     * @param value The value.
     * @return this
     */
    fun add(name: String, value: Number): RecipeBuilder<*> {
        root.addProperty(name, value)
        return this
    }

    /**
     * Add a character to this recipe.
     * @param name The key.
     * @param value The value.
     * @return this
     */
    fun add(name: String, value: Char): RecipeBuilder<*> {
        root.addProperty(name, value)
        return this
    }

    /**
     * Add a JSON object to this recipe.
     * @param name The key.
     * @param settings A callback which will be passed a [JsonObjectBuilder].
     * @return this
     */
    fun addObject(name: String, settings: JsonObjectBuilder.() -> Unit): RecipeBuilder<*> {
        root.add(name,JsonObjectBuilder().apply(settings).build())
        return this
    }

    /**
     * Add a JSON array to this recipe.
     * @param name The key.
     * @param settings A callback which will be passed a [JsonArrayBuilder].
     * @return this
     */
    fun addArray(name: String, settings: JsonArrayBuilder.() -> Unit): RecipeBuilder<*> {
        root.add(name,JsonArrayBuilder().apply(settings).build() )
        return this
    }
}