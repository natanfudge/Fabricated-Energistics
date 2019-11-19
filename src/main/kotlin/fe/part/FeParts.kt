package fe.part

import alexiil.mc.lib.multipart.api.MultipartHolder
import alexiil.mc.lib.multipart.api.PartDefinition
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNbtReader
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNetLoader
import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import fe.modId
import net.minecraft.nbt.CompoundTag

object FeParts {
    val TANK = def(
        "tank",
        IPartNbtReader { definition, holder, tag ->
            PartTank(definition, holder, tag)
        },
        IPartNetLoader { definition, holder, buf, ctx ->
            PartTank(definition, holder, buf, ctx)
        }
    )

    private fun def(post: String, reader: IPartNbtReader, loader: IPartNetLoader): PartDefinition {
        return PartDefinition(modId(post), reader, loader)
    }

    fun load() {
        PartDefinition.PARTS[TANK.identifier] = TANK
    }
}