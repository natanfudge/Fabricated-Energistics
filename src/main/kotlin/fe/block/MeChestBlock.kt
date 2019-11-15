package fe.block

import fe.blockentity.MeChestBlockEntity
import fe.client.gui.MeChestScreenController
import fe.container.NetworkInventoryScreenController
import fe.modId
import fe.network.NetworkNode
import fe.util.BlockWithBlockEntity
import fe.util.openGui
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object MeChestBlock : BlockWithBlockEntity(Settings.of(Material.METAL), ::MeChestBlockEntity),NetworkNode {
    val Id = modId("chest")
    override fun activate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): Boolean {
        if (world.isClient) return true
        if (hit.side != Direction.UP) {
            player.openGui(MeChestScreenController.Id, pos)
        } else {
            player.openGui(NetworkInventoryScreenController.Id, pos)
        }

        return true
    }
}