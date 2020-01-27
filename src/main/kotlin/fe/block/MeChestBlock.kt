package fe.block

import fabricktx.api.name
import fe.blockentity.MeChestBlockEntity
import fe.network.NetworkBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


object MeChestBlock : NetworkBlock<MeChestBlockEntity>(Block.Settings.of(Material.METAL), ::MeChestBlockEntity) {
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        println("Called on the " + world.name)
//        if (world.isServer) {
//            if (hit.side != Direction.UP) {
//                player.openGui(MeChestScreenController.Id, pos)
//            } else {
//                player.openGui(NetworkInventoryScreenController.Id, pos)
//            }
//        }

//        return if(world.isServer) ActionResult.FAIL
        return ActionResult.CONSUME
//        if(world.isServer) return ActionResult.PASS
//        else return ActionResult.FAIL
    }


}