package fe.chest

import fe.modId
import fe.util.BlockWithBlockEntity
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object MeChestBlock : BlockWithBlockEntity(Settings.of(Material.METAL), ::MeChestBlockEntity) {
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
            ContainerProviderRegistry.INSTANCE.openContainer(Id, player) { it.writeBlockPos(pos) }
        }

        return true
    }
}