@file:Suppress("unused")

package fe

import fe.drive.DriveBayScreenController
import fe.drive.DriveBayScreen
import fe.drive.DriveBayBlock
import fe.drive.DriveBayBlockEntity
import fe.util.initCommon
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.BlockContext
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


const val ModId = "fabricated-energistics"
object FabricatedEnergistics {
     val Group = FabricItemGroupBuilder.build(modId("item_group")) { ItemStack(DriveBayBlock) }
}


fun modId(path: String) = Identifier(ModId, path)
fun init() = initCommon(ModId,FabricatedEnergistics.Group) {
    registerBlocksWithItemBlocks {
        DriveBayBlock withId DriveBayBlock.Id
    }

    registerTo(Registry.BLOCK_ENTITY_TYPE){
        DriveBayBlockEntity.Type withId DriveBayBlock.Id
    }


    ContainerProviderRegistry.INSTANCE.registerFactory(DriveBayBlock.Id) { syncId, _, player, buf ->
        DriveBayScreenController(
            syncId,
            player.inventory,
            BlockContext.create(player.world, buf.readBlockPos())
        )
    }


}


fun initClient() {
    ScreenProviderRegistry.INSTANCE.registerFactory(
        DriveBayBlock.Id
    ) { syncId, _, player, buf ->
        DriveBayScreen(
            DriveBayScreenController(
                syncId,
                player.inventory,
                BlockContext.create(player.world, buf.readBlockPos())
            ),
            player
        )
    }
}