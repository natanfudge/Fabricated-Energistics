package fe.part

import alexiil.mc.lib.multipart.api.AbstractPart
import alexiil.mc.lib.multipart.api.MultipartHolder
import alexiil.mc.lib.multipart.api.PartDefinition
import alexiil.mc.lib.multipart.api.render.PartModelKey
import fe.modId
import net.minecraft.util.shape.VoxelShape

class CablePart(definition: PartDefinition, holder: MultipartHolder) : AbstractPart(definition, holder) {
    companion object {
//        val Id = modId("cable_part")
//
////        private val Definition = PartDefinition(Id, PartDefinition.IPartNbtReader { definition, holder, nbt ->
////            CablePart()
////        }, PartDefinition.IPartNetLoader { definition, holder, buffer, ctx ->
////            CablePart()
////        })
//
//        init {
//            PartDefinition.PARTS[Id] = Definition
//        }
    }

    override fun getModelKey(): PartModelKey? {
        TODO("not implemented")
    }

    override fun getShape(): VoxelShape {
        TODO("not implemented")
    }


}