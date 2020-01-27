package fe.kartifice.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.swordglowsblue.artifice.common.ArtificeRegistry
import fe.kartifice.ClientResourcePackBuilder
import fe.kartifice.KArtificeResourcePack
import fe.kartifice.ResourcePackBuilder
import fe.kartifice.ServerResourcePackBuilder
import fe.kartifice.builder.BuilderTransform
import fe.kartifice.builder.JsonObjectBuilder
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.builder.assets.*
import fe.kartifice.builder.data.AdvancementBuilder
import fe.kartifice.builder.data.LootTableBuilder
import fe.kartifice.builder.data.TagBuilder
import fe.kartifice.builder.data.recipe.*
import fe.kartifice.resource.JsonResource
import fe.kartifice.resource.KArtificeResource
import fe.kartifice.util.IdUtils
import fe.kartifice.virtualpack.KArtificeResourcePackContainer
import net.fabricmc.api.EnvType
import net.fabricmc.api.EnvironmentInterface
import net.minecraft.SharedConstants
import net.minecraft.client.resource.ClientResourcePackProfile
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.util.Identifier
import org.apache.commons.io.input.NullInputStream
import org.apache.logging.log4j.LogManager
import java.io.*
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier

class KArtificeResourcePackImpl<T : ResourcePackBuilder>(
    override val type: ResourceType,
    private val id: Identifier,
    registerResources: T.() -> Unit
) : KArtificeResourcePack {
    private val namespaces: MutableSet<String> = mutableSetOf()
    private val resources: MutableMap<Identifier, KArtificeResource<*>> = mutableMapOf()
    private val languages: MutableSet<LanguageDefinition> = mutableSetOf()
    private val metadata: JsonResource<JsonObject>
//    private var transforms: List<BuilderTransform<*>> = BuilderTransform.DefaultTransforms

    //TODO: remove transforms entirely
    override fun setTransforms(transforms: List<BuilderTransform<*>>) {
//        this.transforms = transforms
    }

    private var description: String? = null
    private var displayName: String? = null
    override var isOptional: Boolean = false
        private set
    override var isVisible: Boolean = false
        private set

    @EnvironmentInterface(
        value = EnvType.CLIENT,
        itf = ClientResourcePackBuilder::class
    )
    private inner class ArtificeResourcePackBuilder : ClientResourcePackBuilder,
        ServerResourcePackBuilder {
        override fun setDisplayName(name: String) {
            displayName = name
        }

        override fun setDescription(desc: String) {
            description = desc
        }

        override fun setVisible() {
            isVisible = true
        }

        override fun setOptional() {
            isOptional = true
            isVisible = true
        }

        override fun add(id: Identifier, resource: KArtificeResource<*>) {
            resources[id] = resource
            namespaces.add(id.namespace)
        }

        override fun addItemModel(
            id: Identifier,
            parent: Identifier?,
            f: ModelBuilder.() -> Unit
        ) = addJson("models/item/", id, ModelBuilder(), f) {
            parent?.let { parent(it) }
        }


        override fun addBlockModel(
            id: Identifier,
            parent: Identifier?,
            f: ModelBuilder.() -> Unit
        ): Identifier {
            addJson("models/block/", id, ModelBuilder(), f) {
                parent?.let { parent(it) }
            }

            return id
        }

        override fun addModel(id: Identifier, parent: Identifier?, f: ModelBuilder.() -> Unit) =
            addJson("models/", id, ModelBuilder(), f) {
                parent?.let { parent(it) }
            }.run { id }


        override fun addBlockState(
            id: Identifier,
            f: BlockStateBuilder.() -> Unit
        ) = addJson("blockstates/", id, BlockStateBuilder(), f).run { id }


        override fun addTranslations(
            id: Identifier,
            f: TranslationBuilder.() -> Unit
        ) = addJson("lang/", id, TranslationBuilder(), f)


        override fun addParticle(
            id: Identifier,
            f: ParticleBuilder.() -> Unit
        ) = addJson("particles/", id, ParticleBuilder(), f)


        override fun addItemAnimation(
            id: Identifier,
            f: AnimationBuilder.() -> Unit
        ) = addMcmeta("textures/item/", id, AnimationBuilder(), f)


        override fun addBlockAnimation(
            id: Identifier,
            f: AnimationBuilder.() -> Unit
        ) = addMcmeta(
            "textures/block/",
            id, AnimationBuilder(),
            f
        )


        override fun addLanguage(def: LanguageDefinition) {
            languages.add(def)
        }

        override fun addLanguage(
            code: String,
            region: String,
            name: String,
            rtl: Boolean
        ) {
            this.addLanguage(LanguageDefinition(code, region, name, rtl))
        }

        override fun addAdvancement(
            id: Identifier,
            f: AdvancementBuilder.() -> Unit
        ) = addJson("advancements/", id, AdvancementBuilder(), f)


        override fun addLootTable(
            id: Identifier,
            f: LootTableBuilder.() -> Unit
        ) = addJson("loot_tables/", id, LootTableBuilder(), f)


        override fun addItemTag(
            id: Identifier,
            f: TagBuilder.() -> Unit
        ) = addJson("tags/items/", id, TagBuilder(), f)


        override fun addBlockTag(
            id: Identifier,
            f: TagBuilder.() -> Unit
        ) = addJson("tags/blocks/", id, TagBuilder(), f)


        override fun addEntityTypeTag(
            id: Identifier,
            f: TagBuilder.() -> Unit
        ) = addJson("tags/entity_types/", id, TagBuilder(), f)


        override fun addFluidTag(
            id: Identifier,
            f: TagBuilder.() -> Unit
        ) = addJson("tags/fluids/", id, TagBuilder(), f)


        override fun addFunctionTag(
            id: Identifier,
            f: TagBuilder.() -> Unit
        ) = addJson("tags/functions/", id, TagBuilder(), f)


        override fun addGenericRecipe(
            id: Identifier,
            f: GenericRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, GenericRecipeBuilder(), f)


        override fun addShapedRecipe(
            id: Identifier,
            f: ShapedRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, ShapedRecipeBuilder(), f)


        override fun addShapelessRecipe(
            id: Identifier,
            f: ShapelessRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, ShapelessRecipeBuilder(), f)


        override fun addStonecuttingRecipe(
            id: Identifier,
            f: StonecuttingRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, StonecuttingRecipeBuilder(), f)


        override fun addSmeltingRecipe(
            id: Identifier,
            f: CookingRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, CookingRecipeBuilder()) {
            type(Identifier("smelting"))
            f()
        }


        override fun addBlastingRecipe(
            id: Identifier,
            f: CookingRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, CookingRecipeBuilder()) {
            type(Identifier("blasting"))
            f()
        }


        override fun addSmokingRecipe(
            id: Identifier,
            f: CookingRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, CookingRecipeBuilder()) {
            type(Identifier("smoking"))
            f()
        }


        override fun addCampfireRecipe(
            id: Identifier,
            f: CookingRecipeBuilder.() -> Unit
        ) = addJson("recipes/", id, CookingRecipeBuilder()) {
            type(Identifier("campfire_cooking"))
            f()
        }

        private inline fun <T : TypedJsonBuilder<out JsonResource<*>>> addMcmeta(
            path: String,
            id: Identifier,
            obj: T,
            init: T.() -> Unit = {}
        ) = addAnyExt(".mcmeta", path, id, obj, init)


        private inline fun <T : TypedJsonBuilder<out JsonResource<*>>> addJson(
            path: String,
            id: Identifier,
            obj: T,
            init: T.() -> Unit = {},
            additionalInit: T.() -> Unit = {}
        ) = addAnyExt(".json", path, id, obj, init, additionalInit)

//        private inline fun <T : TypedJsonBuilder<out JsonResource<*>>> addJson(
//            path: String,
//            id: Identifier,
//            obj: T,
//            init: T.() -> Unit = {}
//        ) = addAnyExt(".json", path, id, obj, init)

        private inline fun <T : TypedJsonBuilder<out JsonResource<*>>> addAnyExt(
            ext: String,
            path: String,
            id: Identifier,
            obj: T,
            init: T.() -> Unit,
            additionalInit: T.() -> Unit = {}
        ) {
            this.add(IdUtils.wrapPath(path, id, ext), obj.apply { init();additionalInit() }.build())
        }
    }

    override fun openRoot(fname: String): InputStream {
        return if (fname == "pack.mcmeta") metadata.toInputStream() else NullInputStream(0)
    }

    @Throws(IOException::class)
    override fun open(type: ResourceType, id: Identifier): InputStream {
        if (!contains(type, id)) throw FileNotFoundException(id.path)
        return resources[id]!!.toInputStream()
    }

    override fun findResources(
        type: ResourceType,
        namespace: String,
        prefix: String,
        maxDepth: Int,
        pathFilter: Predicate<String>
    ): Collection<Identifier> {
        if (type != this.type) return HashSet()
        val keys: MutableSet<Identifier> =
            HashSet(resources.keys)
        keys.removeIf { id: Identifier ->
            !id.path.startsWith(
                prefix
            ) || !pathFilter.test(id.path)
        }
        return keys
    }

    override fun contains(type: ResourceType, id: Identifier): Boolean {
        return type == this.type && resources.containsKey(id)
    }

    override fun <T> parseMetadata(reader: ResourceMetadataReader<T>): T? {
        return if (metadata.data.has(reader.key)) reader.fromJson(metadata.data.getAsJsonObject(reader.key)) else null
    }

    override fun getNamespaces(type: ResourceType): Set<String> {
        return HashSet(namespaces)
    }

    override fun close() {}
    override fun getName(): String = displayName ?: run {
        when (type) {
            ResourceType.CLIENT_RESOURCES -> {
                val aid = ArtificeRegistry.ASSETS.getId(this)
                aid?.toString() ?: "Generated Resources".also { displayName = it }
            }
            ResourceType.SERVER_DATA -> {
                val did = ArtificeRegistry.DATA.getId(this)
                did?.toString() ?: "Generated Data".also { displayName = it }
            }
        }
    }

    override fun <T : ResourcePackProfile> toClientResourcePackProfile(factory: ResourcePackProfile.Factory<T>): ClientResourcePackProfile {
        return KArtificeResourcePackContainer(
            isOptional, isVisible, ResourcePackProfile.of<T>(
                id.toString(),
                false, Supplier { this }, factory,
                if (isOptional) ResourcePackProfile.InsertionPosition.TOP else ResourcePackProfile.InsertionPosition.BOTTOM
            ) ?: error("Could not create ")
        )
    }

    override fun <T : ResourcePackProfile> toServerResourcePackProfile(factory: ResourcePackProfile.Factory<T>): ResourcePackProfile {
        return ResourcePackProfile.of<T>(
            id.toString(),
            false, Supplier { this }, factory,
            ResourcePackProfile.InsertionPosition.BOTTOM
        ) ?: error("Could not create server resource pack profile")
    }

    @Throws(IOException::class, IllegalArgumentException::class)
    override fun dumpResources(folderPath: String) {
        LogManager.getLogger()
            .info("[Artifice] Dumping " + name + " " + type.directory + " to " + folderPath + ", this may take a while.")
        val dir = File(folderPath)
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("Can't dump resources to $folderPath; couldn't create parent directories")
        }
        require(dir.isDirectory) { "Can't dump resources to $folderPath as it's not a directory" }
        if (!dir.canWrite()) {
            throw IOException("Can't dump resources to $folderPath; permission denied")
        }
        writeResourceFile(File("$folderPath/pack.mcmeta"), metadata)
        resources.forEach { (id: Identifier, resource: KArtificeResource<*>) ->
            val path =
                String.format("./%s/%s/%s/%s", folderPath, type.directory, id.namespace, id.path)
            writeResourceFile(File(path), resource)
        }
        LogManager.getLogger()
            .info("[Artifice] Finished dumping " + name + " " + type.directory + ".")
    }

    private fun writeResourceFile(output: File, resource: KArtificeResource<*>) {
        try {
            if (output.parentFile.exists() || output.parentFile.mkdirs()) {
                val writer = BufferedWriter(FileWriter(output))
                val data = resource.data
                if (data is JsonElement) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    writer.write(gson.toJson(data))
                } else {
                    writer.write(resource.data.toString())
                }
                writer.close()
            } else {
                throw IOException("Failed to dump resource file " + output.path + "; couldn't create parent directories")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {
        registerResources(ArtificeResourcePackBuilder() as T)
        val packMeta = JsonObjectBuilder()
            .add("pack_format", SharedConstants.getGameVersion().packVersion)
            .add("description", if (description != null) description!! else "In-memory resource pack created with Artifice")
            .build()
        val languageMeta = JsonObject()
        for (def in languages) {
            languageMeta.add(
                def.code, JsonObjectBuilder()
                    .add("name", def.name)
                    .add("region", def.region)
                    .add("bidirectional", def.isRightToLeft)
                    .build()
            )
        }
        val builder =
            JsonObjectBuilder()
        builder.add("pack", packMeta)
        if (languages.size > 0) builder.add("language", languageMeta)
        metadata = JsonResource(builder.build())
    }
}