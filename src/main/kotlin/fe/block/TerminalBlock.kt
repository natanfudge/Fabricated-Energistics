package fe.block

import fe.network.NetworkNode
import fe.util.Builders
import net.minecraft.block.Block
import net.minecraft.block.Material

object TerminalBlock : Block(Builders.blockSettings(Material.METAL)), NetworkNode {

}