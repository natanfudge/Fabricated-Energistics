@file:Suppress("unused")

package fe

import fe.chest.MeChestBlock
import fe.chest.MeChestScreen
import fe.chest.MeChestScreenController
import fe.drive.*
import fe.util.initClientOnly
import fe.util.initCommon
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


const val ModId = "fabricated-energistics"

object FabricatedEnergistics {
    val Group = FabricItemGroupBuilder.build(modId("item_group")) { ItemStack(DriveBayBlock) }
}


fun modId(path: String) = Identifier(ModId, path)
fun init() = initCommon(ModId, FabricatedEnergistics.Group) {
    registerBlocksWithItemBlocks {
        DriveBayBlock withId DriveBayBlock.Id
        MeChestBlock withId MeChestBlock.Id
    }

    registerTo(Registry.BLOCK_ENTITY_TYPE) {
        DriveBayBlockEntity.Type withId DriveBayBlock.Id
    }

    registerTo(Registry.ITEM) {
        for ((cell, id) in StorageDisk.cells) {
            cell withId id
        }
    }


    registerContainer(DriveBayBlock.Id, ::DriveBayScreenController)
    registerContainer(MeChestBlock.Id, ::MeChestScreenController)


}


fun initClient() = initClientOnly(ModId) {
    registerScreen(DriveBayBlock.Id, ::DriveBayScreenController, ::DriveBayScreen)
    registerScreen(MeChestBlock.Id, ::MeChestScreenController, ::MeChestScreen)

}