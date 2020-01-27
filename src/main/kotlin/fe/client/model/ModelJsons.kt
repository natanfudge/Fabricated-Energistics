package fe.client.model

import fe.block.CableBlock.Companion.Connection.Down
import fe.block.CableBlock.Companion.Connection.East
import fe.block.CableBlock.Companion.Connection.North
import fe.block.CableBlock.Companion.Connection.South
import fe.block.CableBlock.Companion.Connection.Up
import fe.block.CableBlock.Companion.Connection.West
import fe.block.CoveredCableBlocks
import fe.id
import fe.kartifice.ClientResourcePackBuilder
import fe.kartifice.builder.assets.always

fun ClientResourcePackBuilder.initCableModels() {
    for (coveredCable in CoveredCableBlocks) {
        val color = coveredCable.color.lowercase
        val path = "cable/covered"
        val texture = "parts/cable/covered/$color".id
        val centerModel = addBlockModel("$path/center_$color".id, parent = "block/$path/center".id) {
            textures {
                "base" valued texture
                "particle" valued texture
            }
        }
//TODO: figure out block prefix issues
        val connectionModel = addBlockModel("$path/connection_$color".id, parent = "block/$path/connection".id) {
            texture("base", texture)
        }

        val block = addBlockState("cable_covered_$color".id) {
            multipart {
                always(centerModel)

                with(connectionModel) {
                    whenState(Down to true) {
                        applyModel(x = 90)
                    }
                    whenState(Up to true) {
                        applyModel(x = 270)
                    }
                    whenState(North to true) {
                        applyModel()
                    }
                    whenState(South to true) {
                        applyModel(x = 180)
                    }
                    whenState(West to true) {
                        applyModel(x = 180, y = 90)
                    }
                    whenState(East to true) {
                        applyModel(x = 180, y = 270)
                    }
                }

            }
        }

        addItemModel(block, parent = "item/cable_covered".id) {
            texture("base", texture)
        }
    }
}
