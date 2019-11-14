@file:Suppress("unused")

package fe

import fe.block.*
import fe.blockentity.DriveBayBlockEntity
import fe.blockentity.MeChestBlockEntity
import fe.client.gui.*
import fe.container.NetworkInventoryScreenController
import fe.item.StorageDisk
import fe.util.initClientOnly
import fe.util.initCommon
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

//TODO: write a custom builtin model for drives, reuse the same principles for chest

const val ModId = "fabricated-energistics"

object FabricatedEnergistics {
    val Group = FabricItemGroupBuilder.build(modId("item_group")) { ItemStack(DriveBayBlock) }
}

const val LogId = "FE"

fun modId(path: String) = Identifier(ModId, path)
fun init() = initCommon(ModId, FabricatedEnergistics.Group) {
    registerBlocksWithItemBlocks {
//        DriveBayBlock withId DriveBayBlock.Id
        MeChestBlock withId MeChestBlock.Id

//        for (cable in GlassCableBlock.All.take(1)) {
//            cable withId "cable_glass_" + cable.color.lowercase
//        }

        for (cable in CoveredCableBlock.All.take(1)) {
            cable withId "cable_covered_" + cable.color.lowercase
        }
    }

    registerTo(Registry.BLOCK_ENTITY_TYPE) {
        DriveBayBlockEntity.Type withId DriveBayBlock.Id
        MeChestBlockEntity.Type withId MeChestBlock.Id
    }

    registerTo(Registry.ITEM) {
        for ((cell, id) in StorageDisk.cells) {
            cell withId id
        }
    }


    registerContainer(DriveBayBlock.Id, ::DriveBayScreenController)
    registerContainer(MeChestScreenController.Id, ::MeChestScreenController)
    registerContainer(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController)


}


fun initClient() = initClientOnly(ModId) {
    registerScreen(DriveBayBlock.Id, ::DriveBayScreenController, ::DriveBayScreen)
    registerScreen(MeChestScreenController.Id, ::MeChestScreenController, ::MeChestScreen)
    registerScreen(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController, ::NetworkInventoryScreen)
}