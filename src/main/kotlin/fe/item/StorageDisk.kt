package fe.item

import fe.FabricatedEnergistics
import fe.network.DiskStack
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World

class StorageDisk(val differentItemsCapacity: Int, val perItemCapacity: Int) :
    Item(Settings().group(FabricatedEnergistics.Group).maxCount(1)) {
    companion object {
        private const val PerItemCapacity = 63
        val All = mapOf(
            StorageDisk(
                PerItemCapacity,
                1000
            ) to "storage_cell_1k",
            StorageDisk(
                PerItemCapacity,
                4000
            ) to "storage_cell_4k",
            StorageDisk(
                PerItemCapacity,
                16000
            ) to "storage_cell_16k",
            StorageDisk(
                PerItemCapacity,
                64000
            ) to "storage_cell_64k"
        )
    }

    private val totalMaxStackAmount : Int = differentItemsCapacity * perItemCapacity

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val inventory = DiskStack(stack).listContents()
        tooltip.add(
            LiteralText("${inventory.sumBy { it.count }} ")
                .append(TranslatableText("gui.fabricated-energistics.Of"))
                .append(LiteralText(" $totalMaxStackAmount "))
                .append(TranslatableText("gui.fabricated-energistics.BytesUsed"))
        )

        tooltip.add(
            LiteralText("${inventory.count { !it.isEmpty }} ")
                .append(TranslatableText("gui.fabricated-energistics.Of"))
                .append(LiteralText(" $differentItemsCapacity "))
                .append(TranslatableText("gui.fabricated-energistics.Types"))
        )

    }
}

