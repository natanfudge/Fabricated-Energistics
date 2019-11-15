package fe.block

import fe.network.NetworkNode
import fe.util.BlockStateUpdate
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderLayer
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.StateFactory
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class CableBlock(val color: Color) : Block(Settings.of(Material.GLASS)), NetworkNode {
    companion object {
        object Connection {
            val Down: BooleanProperty = BooleanProperty.of("down")
            val Up: BooleanProperty = BooleanProperty.of("up")
            val North: BooleanProperty = BooleanProperty.of("north")
            val South: BooleanProperty = BooleanProperty.of("south")
            val East: BooleanProperty = BooleanProperty.of("east")
            val West: BooleanProperty = BooleanProperty.of("west")

            val All = listOf(Down, Up, North, South, East, West)
        }
    }

    enum class Color(val lowercase: String) {
        Black("black"),
        Blue("blue"),
        Brown("brown"),
        Cyan("cyan"),
        Gray("gray"),
        Green("green"),
        LightBlue("light_blue"),
        LightGray("light_gray"),
        Lime("lime"),
        Magenta("magenta"),
        Orange("orange"),
        Pink("pink"),
        Purple("purple"),
        Red("red"),
        Transparent("transparent"),
        White("white"),
        Yellow("yellow")
    }

    override fun getRenderLayer() = BlockRenderLayer.CUTOUT_MIPPED

    init {
        var default = stateFactory.defaultState
        for (connection in Connection.All) {
            default = default.with(connection, false)
        }
        defaultState = default
    }

    override fun appendProperties(builder: StateFactory.Builder<Block, BlockState>) {
        builder.add(*Connection.All.toTypedArray())
    }

    private fun World.updateSides(pos: BlockPos) {
        val sides = mapOf(
            pos.up() to Connection.Up,
            pos.down() to Connection.Down,
            pos.north() to Connection.North,
            pos.south() to Connection.South,
            pos.east() to Connection.East,
            pos.west() to Connection.West
        )
        var state = defaultState
        for ((sidePos, sideConnection) in sides) {
            val connected = !world.getBlockState(sidePos).isAir
            state = state.with(sideConnection, connected)
        }
        world.setBlockState(pos, state, BlockStateUpdate.UpdateListeners)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        world.updateSides(pos)
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        world.updateSides(pos)
    }
}

class CoveredCableBlock(color: Color) : CableBlock(color) {
    companion object {
        val All = Color.values().map { CoveredCableBlock(it) }
    }
}

class GlassCableBlock(color: Color) : CableBlock(color) {
    companion object {
        val All = Color.values().map { GlassCableBlock(it) }
    }
}