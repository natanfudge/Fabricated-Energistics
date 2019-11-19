@file:Suppress("unused")

package fe

import fe.block.*
//import fe.blockentity.DriveBayBlockEntity
//import fe.blockentity.EnergyAcceptorBlockEntity
import fe.client.gui.*
import fe.container.NetworkInventoryScreenController
import fe.item.StorageDisk
import fe.network.DiskStack
import fe.network.InventoryComponentImpl
import fe.part.FeParts
//import fe.part.FeParts
import fe.util.initClientOnly
import fe.util.initCommon
import fe.util.itemStackList
import nerdhub.cardinal.components.api.event.ItemComponentCallback
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

//TODO: write a custom builtin model for drives, reuse the same principles for chest


const val ModId = "fabricated-energistics"

object FabricatedEnergistics {
    val Group = FabricItemGroupBuilder.build(modId("item_group")) { ItemStack(MeChestBlock) }
}

const val LogId = "FE"

fun modId(path: String) = Identifier(ModId, path)
fun init() = initCommon(ModId, FabricatedEnergistics.Group) {

    registerBlocksBEsAndItems {
        DriveBayBlock withId DriveBayBlock.Id
        MeChestBlock withId MeChestBlock.Id
        EnergyAcceptorBlock withId EnergyAcceptorBlock.Id

        for (cable in CoveredCableBlock.All.take(1)) {
            cable withId "cable_covered_" + cable.color.lowercase
        }
    }

    for ((disk, _) in StorageDisk.All) {
        ItemComponentCallback.event(disk).register(ItemComponentCallback { _, components ->
            components[DiskStack.DiskInventory] = InventoryComponentImpl(disk, itemStackList(disk.differentItemsCapacity))
        })
    }


    registerTo(Registry.ITEM) {
        for ((cell, id) in StorageDisk.All) {
            cell withId id
        }
    }

    FeParts.load()


    registerContainer(DriveBayBlock.Id, ::DriveBayScreenController)
    registerContainer(MeChestScreenController.Id, ::MeChestScreenController)
    registerContainer(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController)


}


fun initClient() = initClientOnly(ModId) {
    registerScreen(DriveBayBlock.Id, ::DriveBayScreenController, ::DriveBayScreen)
    registerScreen(MeChestScreenController.Id, ::MeChestScreenController, ::MeChestScreen)
    registerScreen(NetworkInventoryScreenController.Id, ::NetworkInventoryScreenController, ::NetworkInventoryScreen)
}