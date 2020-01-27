//package fe.block
//
//import fabricktx.api.Builders
//import fabricktx.api.SingularStateBlock
//import fabricktx.api.isServer
//import fe.blockentity.DriveBayBlockEntity
//import fe.modId
//import fe.network.NetworkBlock
//import fabricktx.api.openGui
//import fe.client.gui.DriveBayScreenController
//import net.minecraft.block.BlockState
//import net.minecraft.block.Material
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.util.ActionResult
//import net.minecraft.util.Hand
//import net.minecraft.util.hit.BlockHitResult
//import net.minecraft.util.math.BlockPos
//import net.minecraft.world.World
//
//object DriveBayBlock : SingularStateBlock<DriveBayBlockEntity>(Builders.blockSettings(Material.METAL), ::DriveBayBlockEntity),
//    NetworkBlock {
//    override fun onUse(
//        state: BlockState,
//        world: World,
//        pos: BlockPos,
//        player: PlayerEntity,
//        hand: Hand,
//        hit: BlockHitResult
//    ): ActionResult {
//        if (world.isServer) player.openGui(DriveBayScreenController.Id, pos)
//
//        return ActionResult.SUCCESS
//    }
//
//}
//
