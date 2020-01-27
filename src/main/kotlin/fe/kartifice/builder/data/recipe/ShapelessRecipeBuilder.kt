package fe.kartifice.builder.data.recipe

import com.google.gson.JsonArray
import fe.kartifice.builder.JsonObjectBuilder
import net.minecraft.util.Identifier

/**
 * Builder for a shapeless crafting recipe (`namespace:recipes/id.json`).
 * @see [Minecraft Wiki](https://minecraft.gamepedia.com/Recipe.JSON_format)
 */
class ShapelessRecipeBuilder :
    RecipeBuilder<ShapelessRecipeBuilder>() {
    /**
     * Add an ingredient item.
     * @param id The item ID.
     * @return this
     */
    fun ingredientItem(id: Identifier): ShapelessRecipeBuilder {
        with("ingredients", ::JsonArray) {
            add(JsonObjectBuilder().add("item", id.toString()).build())
        }
        return this
    }

    /**
     * Add an ingredient item as any of the given tag.
     * @param id The tag ID.
     * @return this
     */
    fun ingredientTag(id: Identifier): ShapelessRecipeBuilder {
        with("ingredients", ::JsonArray) {
            add(
                JsonObjectBuilder().add("tag", id.toString()).build()
            )
        }
        return this
    }

    /**
     * Add an ingredient item as one of a list of options.
     * @param settings A callback which will be passed a [MultiIngredientBuilder].
     * @return this
     */
    fun multiIngredient(settings: MultiIngredientBuilder.() -> Unit): ShapelessRecipeBuilder {
        with(
            "ingredients",
            { JsonArray() },
             {
                add(MultiIngredientBuilder().apply(settings).build())
            }
        )
        return this
    }

    /**
     * Set the item produced by this recipe.
     * @param id The item ID.
     * @param count The number of result items.
     * @return this
     */
    fun result(id: Identifier, count: Int): ShapelessRecipeBuilder {
        root.add("result", JsonObjectBuilder().add("item", id.toString()).add("count", count).build())
        return this
    }

    init {
        type(Identifier("crafting_shapeless"))
    }
}