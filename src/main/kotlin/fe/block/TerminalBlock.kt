package fe.block

import fe.blockentity.TerminalBlockEntity
import fe.network.NetworkBlock
import fe.util.BlockWithBlockEntity
import fe.util.Builders
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderLayer
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.state.StateFactory
import net.minecraft.state.property.BooleanProperty
private val On: BooleanProperty = BooleanProperty.of("on")
object TerminalBlock : BlockWithBlockEntity(Builders.blockSettings(Material.METAL),::TerminalBlockEntity), NetworkBlock {


    init {
        defaultState = stateFactory.defaultState.with(On,false)
    }

    override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
        builder.add(On)
    }

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

}