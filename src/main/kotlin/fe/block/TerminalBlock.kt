package fe.block

import fabricktx.api.Builders
import fabricktx.api.StateBlock
import fe.blockentity.TerminalBlockEntity
import fe.network.NetworkBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
private val On: BooleanProperty = BooleanProperty.of("on")
object TerminalBlock : NetworkBlock<TerminalBlockEntity>(Builders.blockSettings(Material.METAL),::TerminalBlockEntity) {


    init {
        defaultState = stateManager.defaultState.with(On,false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(On)
    }

}