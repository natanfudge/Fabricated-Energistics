package fe.block

//import fe.block.CableBlock.Companion.Connection.*
import alexiil.mc.lib.multipart.api.MultipartContainer
import alexiil.mc.lib.multipart.api.NativeMultipart
import fabricktx.api.*
import fe.blockentity.CableBlockEntity
import fe.client.model.*
import fe.network.NetworkBlock
import fe.part.CablePart
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.EntityContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.state.StateManager

//TODO: expand to other cable types

val CoveredCableBlocks = BlockList(CableBlock.Color.values().map { CoveredCableBlock(it) })

val AllCableBlocks = CoveredCableBlocks

sealed class CableBlock(val color: Color) : NetworkBlock<CableBlockEntity>(Settings.of(Material.GLASS), ::CableBlockEntity),
    NativeMultipart {


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

    init {
        var default = stateManager.defaultState
        for (connection in Connection.All) {
            default = default.with(connection, false)
        }
        defaultState = default
    }

    private fun allStates(options: List<BooleanProperty>): List<BlockState> {
        var currentLayer = listOf(defaultState)
        for (option in options) {
            currentLayer = buildList<BlockState> {
                for (state in currentLayer) {
                    add(state.with(option, false))
                    add(state.with(option, true))
                }
            }
        }

        return currentLayer
    }


    private var stateToShape: Map<BlockState, VoxelShape> = allStates(Connection.All).map { state ->
        var shape = coreShape
        if (state.get(Connection.Up)) shape += upShape
        if (state.get(Connection.Down)) shape += downShape
        if (state.get(Connection.North)) shape += northShape
        if (state.get(Connection.South)) shape += southShape
        if (state.get(Connection.East)) shape += eastShape
        if (state.get(Connection.West)) shape += westShape
        state to shape
    }.toMap()

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ePos: EntityContext): VoxelShape {
        return stateToShape[state] ?: error("Could not find blockstate = $state in map of all blockstates: $stateToShape")
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



    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
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
        var newState = defaultState
        for ((sidePos, sideConnection) in sides) {
            val blockState = world.getBlockState(sidePos)
            val connected = !blockState.isAir && blockState.block is NetworkBlock<*>
            newState = newState.with(sideConnection, connected)
        }

        if (newState != world.getBlockState(pos)) world.setBlockState(pos, newState, BlockStateUpdate.UpdateListeners)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
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

    override fun getMultipartConversion(
        world: World?,
        pos: BlockPos?,
        state: BlockState?
    ): List<MultipartContainer.MultipartCreator> =
        listOf(MultipartContainer.MultipartCreator { CablePart(CablePart.Definition, it) })

}
class CoveredCableBlock(color: Color) : CableBlock(color) {


//    override fun getRayTraceShape(state: BlockState?, view: BlockView?, pos: BlockPos?): VoxelShape {
//        return super.getRayTraceShape(state, view, pos)
//    }

//    override fun getMultipartConversion(
//        world: World?,
//        pos: BlockPos?,
//        state: BlockState?
//    ): List<MultipartCreator> = listOf(MultipartCreator { CablePart(CablePart.Definition, it) })
}

//class GlassCableBlock(color: Color) : CableBlock(color) {
//    companion object {
//        val All = Color.values().map { GlassCableBlock(it) }
//    }
//
//    override fun getMultipartConversion(
//        world: World?,
//        pos: BlockPos?,
//        state: BlockState?
//    ): MutableList<MultipartContainer.MultipartCreator> {
//        TODO("not implemented")
//    }
//}