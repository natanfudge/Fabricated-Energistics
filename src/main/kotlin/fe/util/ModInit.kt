package fe.util

import io.github.cottonmc.cotton.gui.CottonScreenController
import io.github.cottonmc.cotton.gui.client.CottonScreen
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelVariantProvider
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.container.BlockContext
import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Function

/**
 * Should be called at the init method of the mod. Do all of your registry here.
 */
inline fun initCommon(modId: String, group: ItemGroup? = null, init: CommonModInitializationContext.() -> Unit) {
    CommonModInitializationContext(modId, group).init()
}


/**
 * Should be called at the client init method
 */
inline fun initClientOnly(modId: String, init: ClientModInitializationContext.() -> Unit) {
    ClientModInitializationContext(modId).apply {
        init()
        registerS2C(Packet.InbuiltS2CPackets)
    }

}


class CommonModInitializationContext(
    @PublishedApi internal val modId: String,
    @PublishedApi internal val group: ItemGroup?
) {

    inline fun <T> registerTo(registry: Registry<T>, init: RegistryContext<T>.() -> Unit) {
        init(RegistryContext(modId, registry))
    }

    inline fun registerBlocksWithItemBlocks(init: BlockWithItemRegistryContext<Block>.() -> Unit) {
        init(BlockWithItemRegistryContext(modId, group))
    }

    inline fun registerBlocksBEsAndItems(init: BlockItemEntityRegistryContext.() -> Unit) {
        init(BlockItemEntityRegistryContext(modId, group))
    }

    fun registerContainer(containerId: Identifier, factory: (Int, PlayerInventory, BlockContext) -> Container) {
        ContainerProviderRegistry.INSTANCE.registerFactory(containerId) { syncId, _, player, buf ->
            factory(
                syncId,
                player.inventory,
                BlockContext.create(player.world, buf.readBlockPos())
            )
        }
    }

}

//typealias ControllerFactory =

class ClientModInitializationContext(@PublishedApi internal val modId: String) {
    inline fun <reified T : BlockEntity> registerBlockEntityRenderer(renderer: BlockEntityRenderer<T>) {
        BlockEntityRendererRegistry.INSTANCE.register(T::class.java, renderer)
    }

    fun registerKeyBinding(keyBinding: FabricKeyBinding) = KeyBindingRegistry.INSTANCE.register(keyBinding)
    fun registerKeyBindingCategory(name: String) = KeyBindingRegistry.INSTANCE.addCategory(name)

    fun registerBlockModel(blockPath: String, vararg textures: Identifier, bakery: () -> BakedModel) {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider {
            ModelVariantProvider { modelId, _ ->
                if (modelId.namespace == modId && modelId.path == blockPath) {
                    object : UnbakedModel {
                        override fun bake(
                            modelLoader: ModelLoader,
                            spriteFunction: Function<Identifier, Sprite>,
                            settings: ModelBakeSettings
                        ): BakedModel = bakery()

                        override fun getModelDependencies(): List<Identifier> = listOf()
                        override fun getTextureDependencies(
                            unbakedModelFunction: Function<Identifier, UnbakedModel>,
                            strings: MutableSet<String>
                        ): List<Identifier> = textures.toList()
                    }
                } else null
            }
        }
    }

    fun <T : CottonScreenController> registerScreen(
        screenId: Identifier,
        controllerFactory: (Int, PlayerInventory, BlockContext) -> T,
        screenFactory: (T, PlayerEntity) -> CottonScreen<T>
    ) {
        ScreenProviderRegistry.INSTANCE.registerFactory(screenId) { syncId, _, player, buf ->
            screenFactory(
                controllerFactory(
                    syncId,
                    player.inventory,
                    BlockContext.create(player.world, buf.readBlockPos())
                ),
                player
            )
        }
    }


//    fun <T : LettuceScreenController> registerLettuceScreen(
//        screenId: Identifier,
//        controllerFactory: (Int, PlayerInventory, BlockContext) -> T,
//        screenFactory: (T, PlayerEntity) -> LettuceScreen<T>
//    ) {
//        ScreenProviderRegistry.INSTANCE.registerFactory(screenId) { syncId, _, player, buf ->
//            screenFactory(
//                controllerFactory(
//                    syncId,
//                    player.inventory,
//                    BlockContext.create(player.world, buf.readBlockPos())
//                ),
//                player
//            )
//        }
//    }
}


open class RegistryContext<T>(private val namespace: String, private val registry: Registry<in T>) {
    // Kotlin won't let calling T.withId directly inside children
    protected open fun withIdWorkaround(toRegister: T, id: Identifier): T = Registry.register(registry, id, toRegister)

    infix fun T.withId(name: String): T = withId(Identifier(namespace, name))
    infix fun T.withId(id: Identifier): T = withIdWorkaround(this, id)
}

open class BlockWithItemRegistryContext<T : Block>(namespace: String, private val group: ItemGroup?) :
    RegistryContext<T>(namespace, Registry.BLOCK) {
//    infix fun Block.withId(name: String) = withId(Identifier(namespace, name))

    override fun withIdWorkaround(toRegister: T, id: Identifier): T {
        Registry.register(
            Registry.ITEM,
            id,
            BlockItem(toRegister, Item.Settings().group(group ?: ItemGroup.MISC))
        )
        return super.withIdWorkaround(toRegister, id)
    }
}

/**
 * Registry for Block, BlockEntity, and Item.
 */
class BlockItemEntityRegistryContext(namespace: String, group: ItemGroup?) :
    BlockWithItemRegistryContext<BlockWithBlockEntity>(namespace, group) {
//    override fun BlockWithBlockEntity.withId(id: Identifier): BlockWithBlockEntity {
//        super.
//    }

    //TODO: a way to register multiple blocks with one BE
    override fun withIdWorkaround(toRegister: BlockWithBlockEntity, id: Identifier): BlockWithBlockEntity {
        Registry.register(
            Registry.BLOCK_ENTITY,
            id,
            toRegister.entityType
        )
        return super.withIdWorkaround(toRegister, id)
    }
}
