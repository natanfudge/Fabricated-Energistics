@file:Suppress("unused")

package fe

//import fe.client.model.BlockStates.init
import fabricktx.api.initClientOnly
import fabricktx.api.initCommon
import fabricktx.api.itemStackList
import fabricktx.api.mcId
import fe.block.CoveredCableBlocks
import fe.block.MeChestBlock
import fe.block.TerminalBlock
import fe.client.gui.MeChestScreen
import fe.client.gui.MeChestScreenController
import fe.client.gui.NetworkInventoryScreen
import fe.client.model.initCableModels
import fe.container.NetworkInventoryScreenController
import fe.item.StorageDisk
import fe.kartifice.ClientResourcePackBuilder
import fe.kartifice.KArtifice
import fe.kartifice.builder.assets.ModelBuilder
import fe.network.DiskStack
import fe.network.InventoryComponentImpl
import fe.part.CablePart
import nerdhub.cardinal.components.api.event.ItemComponentCallback
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

//TODO: write a custom builtin model for drives, reuse the same principles for chest


const val ModId = "fabricated-energistics"

val String.id
    get() = Identifier(ModId, this)

object FabricatedEnergistics {
    val Group = FabricItemGroupBuilder.build(modId("item_group")) { ItemStack(MeChestBlock) }
}

val Logger = fabricktx.api.Logger(
    name = "Fabricated Energistics"
)

inline fun logDebug(log: () -> String) = Logger.debug(log)
inline fun logInfo(log: () -> String) = Logger.info(log)
inline fun logWarning(log: () -> String) = Logger.warning(log)

inline fun assert(message: String? = null, function: () -> Boolean) {
    if (!function()) throw AssertionError(message)
}

//TODO: API for automatically having item models for trivial models
fun modId(path: String) = Identifier(ModId, path)

class FabricatedEnergisticsInit : ModInitializer {
    override fun onInitialize() = initCommon(ModId, FabricatedEnergistics.Group) {

        registerBlocks {
            //        DriveBayBlock withId "drive"
            MeChestBlock withId "chest"
//        EnergyAcceptorBlock withId "energy_acceptor"
            TerminalBlock withId "terminal"

            //TODO: register all instead of just one
            CoveredCableBlocks.withId { "cable_covered_" + it.color.lowercase }
        }

        for ((disk, _) in StorageDisk.All) {
            ItemComponentCallback.event(disk).register(ItemComponentCallback { _, components ->
                components[DiskStack.DiskInventory] =
                    InventoryComponentImpl(disk, itemStackList(disk.differentItemsCapacity))
            })
        }


        registerTo(Registry.ITEM) {
            for ((cell, id) in StorageDisk.All) {
                cell withId id
            }
        }

        CablePart.Definition.register()

//    registerContainer(DriveBayScreenController.Id, ::DriveBayScreenController)
        registerContainer(MeChestScreenController.Id, ::MeChestScreenController)
        registerContainer(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController)
        
    }
}

class FabricatedEnergisticsClientInit : ClientModInitializer {
    override fun onInitializeClient() = initClientOnly(ModId) {
        KArtifice.registerAssets("default_assets".id) {
            initCableModels()

            StorageDisk.All.forEach { defaultItemModel(it.value.id) }

            simpleBlockModel("chest".id, parent = "block/cube".mcId) {
                textures {
                    val path = "block/chest"
                    "up" valued "$path/top_item".id
                    "down" valued "$path/bottom".id
                    "north" valued "$path/front".id
                    listOf("east", "south", "west").forEach { it valued "$path/side".id }
                }
            }

            val terminal = addBlockState("terminal".id) {
                variant("on=true", model = "display/terminal_on".id)
                variant("on=false", model = "display/terminal_off".id)
            }


        }

        for (cable in CoveredCableBlocks) cable.setRenderLayer(RenderLayer.getCutoutMipped())
        TerminalBlock.setRenderLayer(RenderLayer.getCutoutMipped())

//    registerScreen(DriveBayScreenController.Id, ::DriveBayScreenController, ::DriveBayScreen)
        registerScreen(MeChestScreenController.Id, ::MeChestScreenController, ::MeChestScreen)
        registerScreen(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController, ::NetworkInventoryScreen)
    }
}

fun ClientResourcePackBuilder.defaultItemModel(id: Identifier) = addItemModel(id) {
    parent(Identifier("minecraft", "item/generated"))
    texture("layer0", Identifier(id.namespace, "item/${id.path}"))
}

fun ClientResourcePackBuilder.simpleBlockModel(id: Identifier, parent: Identifier, init: ModelBuilder.() -> Unit) {
    addBlockState(id) {
        variant(name = "", model = id)
    }
    val model = addBlockModel(id, parent, init)
    addItemModel(Identifier(id.namespace,"block/" +id.path), parent = model)
}

//but
//annotation class KArtifice
//
//typealias ClientInit = ClientModInitializationContext
//
//
//@KArtifice
//class AssetInit(@PublishedApi internal val builder: ArtificeResourcePack.ClientResourcePackBuilder, private val autoModId: String) {
//    val String.id
//        get() = Identifier(this, autoModId)
//
//    val String.mcId get() = Identifier(this, "minecraft")
//
//    inline fun itemModel(id: Identifier, parent: Identifier, crossinline init: ModelBuilder.() -> Unit) =
//        builder.addItemModel(id) {
//            it.parent(parent)
//            it.init()
//        }
//
//    inline fun blockModel(id: Identifier, parent: Identifier, crossinline init: ModelBuilder.() -> Unit) =
//        builder.addBlockModel(id.copy(path = "block/" + id.path)) {
//            it.parent(parent)
//            it.init()
//        }
//
//    fun blockState(id: Identifier, init: BlockStateBuilderInit.() -> Unit) =
//        builder.addBlockState(id) { BlockStateBuilderInit(it,autoModId).init() }
//
//    fun defaultItemModel(id: Identifier) = itemModel(id, parent = Identifier("minecraft", "item/generated")) {
//        texture("layer0", Identifier(id.namespace, "item/${id.path}"))
//    }
//
////    inline fun modAssets(modId: String, init: ModAssetInit.() -> Unit) = ModAssetInit(this, modId).init()
//
//}
//
//@KArtifice
//class BlockStateBuilderInit(@PublishedApi internal val builder: BlockStateBuilder, private val autoModId: String) {
//    val String.id
//        get() = Identifier(this, autoModId)
//
//    fun variant(name: String, init: BlockStateBuilder.Variant.   () -> Unit) {
//        builder.variant(name, init)
//    }
//
////    inline fun variant(name: String, crossinline init: BlockStateBuilderVariantInit.() -> Unit) {
////        builder.variant(name) { BlockStateBuilderVariantInit(it).init() }
////    }
//}
//
//@KArtifice
//class ModelBuilderInit(@PublishedApi internal val builder: BlockStateBuilder,)
//
////class ModAssetInit(builder: ArtificeResourcePack.ClientResourcePackBuilder, private val modId: String) : AssetInit(builder) {
////
//////    @PublishedApi
//////    internal fun id(name: String) = Identifier(modId, name)
//////
//////    inline fun itemModel(name: String, parent: Identifier, crossinline init: ModelBuilder.() -> Unit) =
//////        builder.itemModel(id(name), parent, init)
//////
//////    fun blockState(name: String, init: BlockStateBuilder.() -> Unit) = builder.blockState(id(name), init)
//////
//////    fun defaultItemModel(name: String) = builder.defaultItemModel(id(name))
////}
//
//object KotlinArtifice {
//    inline fun modAssets(modId: String, packName: String, crossinline registrar: AssetInit.() -> Unit): ArtificeResourcePack =
//        Artifice.registerAssets(Identifier(modId, packName)) { AssetInit(it, modId).registrar() }
//
//
//}



