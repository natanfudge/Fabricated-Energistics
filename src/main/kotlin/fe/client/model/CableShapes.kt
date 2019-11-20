package fe.client.model

import fe.block.CableBlock
import fe.block.CoveredCableBlock
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

fun cube(xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int): VoxelShape {
    return VoxelShapes.cuboid(xMin / 16.0, yMin / 16.0, zMin / 16.0, xMax / 16.0, yMax / 16.0, zMax / 16.0)
}

val CableBlock.coreShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.Core
    }
val CableBlock.upShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.Up
    }
val CableBlock.downShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.Down
    }
val CableBlock.northShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.North
    }
val CableBlock.southShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.South
    }
val CableBlock.eastShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.East
    }
val CableBlock.westShape
    get() = when (this) {
        is CoveredCableBlock -> CableShapes.Covered.West
    }

object CableShapes {
    object Glass {
        val Core = cube(6, 6, 6, 10, 10, 10)
    }

    object Covered {
        val Core = cube(5, 5, 5, 11, 11, 11)
        val Down = cube(6, 0, 6, 10, 5, 10)
        val East = cube(11, 6, 6, 16, 10, 10)
        val North = cube(6, 6, 0, 10, 10, 5)
        val South = cube(6, 6, 11, 10, 10, 16)
        val Up = cube(6, 11, 6, 10, 16, 10)
        val West = cube(0, 6, 6, 5, 10, 10)
    }
}

/*


fun coveredCable(facing: EnumFacing) {
    when (facing) {
        DOWN -> cube(6, 0, 6, 10, 5, 10)
        EAST -> cube(11, 6, 6, 16, 10, 10)
        NORTH -> cube(6, 6, 0, 10, 10, 5)
        SOUTH -> cube(6, 6, 11, 10, 10, 16)
        UP -> cube(6, 11, 6, 10, 16, 10)
        WEST -> cube(0, 6, 6, 5, 10, 10)
    }
}

fun cableCore(coreType) {
    when (coreType) {
                   // x1 y1 z1 x2  y2  z2
        GLASS -> cube(6, 6, 6, 10, 10, 10)
        COVERED -> cube(5, 5, 5, 11, 11, 11)
        DENSE -> cube(3, 3, 3, 13, 13, 13)
    }
}

fun glassConnection(facing: EnumFacing) {
    when (facing) {
        DOWN -> cube(6, 0, 6, 10, 6, 10)
        EAST -> cube(10, 6, 6, 16, 10, 10)
        NORTH -> cube(6, 6, 0, 10, 10, 6)
        SOUTH -> cube(6, 6, 10, 10, 10, 16)
        UP -> cube(6, 10, 6, 10, 16, 10)
        WEST -> cube(0, 6, 6, 6, 10, 10)
    }
}

fun straightGlassConnection(facing: EnumFacing) {
    when (facing) {
        DOWN, UP -> cube(6, 0, 6, 10, 16, 10)
        NORTH, SOUTH -> cube(6, 6, 0, 10, 10, 16)
        EAST, WEST -> cube(0, 6, 6, 16, 10, 10)
    }
}

fun constrainedGlassConnection(facing: EnumFacing) {
    when (facing) {
        DOWN -> cube(6, distanceFromEdge, 6, 10, 6, 10)
        EAST -> cube(10, 6, 6, 16 - distanceFromEdge, 10, 10)
        NORTH -> cube(6, 6, distanceFromEdge, 10, 10, 6)
        SOUTH -> cube(6, 6, 10, 10, 10, 16 - distanceFromEdge)
        UP -> cube(6, 10, 6, 10, 16 - distanceFromEdge, 10)
        WEST -> cube(distanceFromEdge, 6, 6, 6, 10, 10)
    }
}



*/

