package fe.kartifice

import com.swordglowsblue.artifice.common.ClientResourcePackProfileLike
import com.swordglowsblue.artifice.common.ServerResourcePackProfileLike
import fe.kartifice.builder.BuilderTransform
import fe.kartifice.builder.assets.*
import fe.kartifice.builder.data.AdvancementBuilder
import fe.kartifice.builder.data.LootTableBuilder
import fe.kartifice.builder.data.TagBuilder
import fe.kartifice.builder.data.recipe.*
import fe.kartifice.resource.KArtificeResource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import java.io.IOException

/**
 * A resource pack containing Artifice-based resources. May be used for resource generation with
 * [KArtificeResourcePack.dumpResources], or as a virtual resource pack with [KArtifice.registerAssets]
 * or [KArtifice.registerData].
 */
interface KArtificeResourcePack : ResourcePack, ServerResourcePackProfileLike, ClientResourcePackProfileLike {
    /**
     * @return The [ResourceType] this pack contains.
     */
    val type: ResourceType

    /**
     * @return Whether this pack is set as optional (only relevant for client-side packs)
     */
    val isOptional: Boolean

    /**
     * @return Whether this pack is set as visible in the resource packs menu (only relevant for client-side packs)
     */
    val isVisible: Boolean

    /**
     * Dump all resources from this pack to the given folder path.
     *
     * @param folderPath The path generated resources should go under (relative to Minecraft's installation folder)
     * @throws IOException              if there is an error creating the necessary directories.
     * @throws IllegalArgumentException if the given path points to a file that is not a directory.
     */
    @Throws(IOException::class)
    fun dumpResources(folderPath: String)

    fun setTransforms(transforms: List<BuilderTransform<*>>)

}

/**
 * Passed to resource construction callbacks to register resources.
 */
interface ResourcePackBuilder {
    /**
     * Add a resource at the given path.
     *
     * @param id       The resource path.
     * @param resource The resource to add.
     */
    fun add(id: Identifier, resource: KArtificeResource<*>)

    /**
     * Set this pack's display name. Defaults to the pack's ID if not set.
     *
     * @param name The desired name.
     */
    //TODO: convert to named parameter
    fun setDisplayName(name: String)

    /**
     * Set this pack's description.
     *
     * @param desc The desired description.
     */
    //TODO: convert to named parameter
    fun setDescription(desc: String)
}


/**
 * Passed to resource construction callbacks to register client-side resources.
 */
@Environment(EnvType.CLIENT)
interface ClientResourcePackBuilder : ResourcePackBuilder {
    /**
     * Add an item model for the given item ID.
     *
     * @param id An item ID, which will be converted into the correct path.
     * @param f  A callback which will be passed a [ModelBuilder] to create the item model.
     */
    fun addItemModel(id: Identifier, parent: Identifier? = null, f: ModelBuilder.() -> Unit = {})

    /**
     * Add a block model for the given block ID.
     *
     * @param id A block ID, which will be converted into the correct path.
     * @param f  A callback which will be passed a [ModelBuilder] to create the block model.
     * @return The id of the model to be used in blockstates
     */
    fun addBlockModel(id: Identifier, parent: Identifier? = null, f: ModelBuilder.() -> Unit) : Identifier

    /**
     * @return [id], to be referenced in blockstates
     */
    fun addModel(id: Identifier, parent: Identifier? = null, f: ModelBuilder.() -> Unit): Identifier

    /**
     * Add a blockstate definition for the given block ID.
     *
     * @param id A block ID, which will be converted into the correct path.
     * @param f  A callback which will be passed a [BlockStateBuilder] to create the blockstate definition.
     * @return [id], to be used in addItemModel
     */
    fun addBlockState(id: Identifier, f: BlockStateBuilder.() -> Unit) : Identifier

    /**
     * Add a translation file for the given language.
     *
     * @param id The namespace and language code of the desired language.
     * @param f  A callback which will be passed a [TranslationBuilder] to create the language file.
     */
    fun addTranslations(id: Identifier, f: TranslationBuilder.() -> Unit)

    /**
     * Add a particle definition for the given particle ID.
     *
     * @param id A particle ID, which will be converted into the correct path.
     * @param f  A callback which will be passed a [ParticleBuilder] to create the particle definition.
     */
    fun addParticle(id: Identifier, f: ParticleBuilder.() -> Unit)

    /**
     * Add a texture animation for the given item ID.
     *
     * @param id An item ID, which will be converted into the correct path.
     * @param f  A callback which will be passed an [AnimationBuilder] to create the texture animation.
     */
    fun addItemAnimation(id: Identifier, f: AnimationBuilder.() -> Unit)

    /**
     * Add a texture animation for the given block ID.
     *
     * @param id A block ID, which will be converted into the correct path.
     * @param f  A callback which will be passed an [AnimationBuilder] to create the texture animation.
     */
    fun addBlockAnimation(id: Identifier, f: AnimationBuilder.() -> Unit)

    /**
     * Add a custom language. Translations must be added separately with [ClientResourcePackBuilder.addTranslations].
     *
     * @param def A [LanguageDefinition] for the desired language.
     */
    fun addLanguage(def: LanguageDefinition)

    /**
     * Add a custom language. Translations must be added separately with [ClientResourcePackBuilder.addTranslations].
     *
     * @param code   The language code for the custom language (i.e. `en_us`). Must be all lowercase alphanum / underscores.
     * @param region The name of the language region (i.e. United States).
     * @param name   The name of the language (i.e. English).
     * @param rtl    Whether the language is written right-to-left instead of left-to-right.
     */
    fun addLanguage(code: String, region: String, name: String, rtl: Boolean)

    /**
     * Mark this pack as optional (can be disabled in the resource packs menu). Will automatically mark it as visible.
     */
    fun setOptional()

    /**
     * Mark this pack as visible (will be shown in the resource packs menu).
     */
    fun setVisible()
}

/**
 * Passed to resource construction callbacks to register server-side resources.
 */
interface ServerResourcePackBuilder : ResourcePackBuilder {
    /**
     * Add an advancement with the given ID.
     *
     * @param id The ID of the advancement, which will be converted into the correct path.
     * @param f  A callback which will be passed an [AdvancementBuilder] to create the advancement.
     */
    fun addAdvancement(id: Identifier, f: AdvancementBuilder.() -> Unit)

    /**
     * Add a loot table with the given ID.
     *
     * @param id The ID of the loot table, which will be converted into the correct path.
     * @param f  A callback which will be passed a [LootTableBuilder] to create the loot table.
     */
    fun addLootTable(id: Identifier, f: LootTableBuilder.() -> Unit)

    /**
     * Add an item tag with the given ID.
     *
     * @param id The ID of the tag, which will be converted into the correct path.
     * @param f  A callback which will be passed a [TagBuilder] to create the tag.
     */
    fun addItemTag(id: Identifier, f: TagBuilder.() -> Unit)

    /**
     * Add a block tag with the given ID.
     *
     * @param id The ID of the tag, which will be converted into the correct path.
     * @param f  A callback which will be passed a [TagBuilder] to create the tag.
     */
    fun addBlockTag(id: Identifier, f: TagBuilder.() -> Unit)

    /**
     * Add an entity type tag with the given ID.
     *
     * @param id The ID of the tag, which will be converted into the correct path.
     * @param f  A callback which will be passed a [TagBuilder] to create the tag.
     */
    fun addEntityTypeTag(id: Identifier, f: TagBuilder.() -> Unit)

    /**
     * Add a fluid tag with the given ID.
     *
     * @param id The ID of the tag, which will be converted into the correct path.
     * @param f  A callback which will be passed a [TagBuilder] to create the tag.
     */
    fun addFluidTag(id: Identifier, f: TagBuilder.() -> Unit)

    /**
     * Add a function tag with the given ID.
     *
     * @param id The ID of the tag, which will be converted into the correct path.
     * @param f  A callback which will be passed a [TagBuilder] to create the tag.
     */
    fun addFunctionTag(id: Identifier, f: TagBuilder.() -> Unit)

    /**
     * Add a recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [GenericRecipeBuilder] to create the recipe.
     */
    fun addGenericRecipe(id: Identifier, f: GenericRecipeBuilder.() -> Unit)

    /**
     * Add a shaped crafting recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [ShapedRecipeBuilder] to create the recipe.
     */
    fun addShapedRecipe(id: Identifier, f: ShapedRecipeBuilder.() -> Unit)

    /**
     * Add a shapeless crafting recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [ShapelessRecipeBuilder] to create the recipe.
     */
    fun addShapelessRecipe(id: Identifier, f: ShapelessRecipeBuilder.() -> Unit)

    /**
     * Add a stonecutter recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [StonecuttingRecipeBuilder] to create the recipe.
     */
    fun addStonecuttingRecipe(id: Identifier, f: StonecuttingRecipeBuilder.() -> Unit)

    /**
     * Add a smelting recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [CookingRecipeBuilder] to create the recipe.
     */
    fun addSmeltingRecipe(id: Identifier, f: CookingRecipeBuilder.() -> Unit)

    /**
     * Add a blast furnace recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [CookingRecipeBuilder] to create the recipe.
     */
    fun addBlastingRecipe(id: Identifier, f: CookingRecipeBuilder.() -> Unit)

    /**
     * Add a smoker recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [CookingRecipeBuilder] to create the recipe.
     */
    fun addSmokingRecipe(id: Identifier, f: CookingRecipeBuilder.() -> Unit)

    /**
     * Add a campfire recipe with the given ID.
     *
     * @param id The ID of the recipe, which will be converted into the correct path.
     * @param f  A callback which will be passed a [CookingRecipeBuilder] to create the recipe.
     */
    fun addCampfireRecipe(id: Identifier, f: CookingRecipeBuilder.() -> Unit)
}