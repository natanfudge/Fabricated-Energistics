package fe.network

import net.minecraft.item.ItemStack

interface Network {
    val items : List<ItemStack>
}

interface NetworkNode