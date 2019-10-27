package fe

import fe.util.InternalC2SPacket
import fe.util.InternalS2CPacket
import fe.util.InternalTwoSidedPacket
import fe.util.Packet
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry

interface C2SPacket<T : Packet<T>> : InternalC2SPacket<T> {
    override val modId get() = ModId
}

interface S2CPacket<T : Packet<T>> : InternalS2CPacket<T> {
    override val modId get() = ModId
}

interface TwoSidedPacket<T : Packet<T>> : InternalTwoSidedPacket<T> {
    override val modId get() = ModId
}

object Packets {
}


