package fe.kartifice

import com.swordglowsblue.artifice.common.ArtificeRegistry
import fe.kartifice.impl.DynamicResourcePackFactory
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/** Registry methods for Artifice's virtual resource pack support.  */
object KArtifice {


    /**
     * Register a new client-side resource pack, creating resources with the given callback.
     * @param id The pack ID.
     * @param register A callback which will be passed a [ClientResourcePackBuilder].
     * @return The registered pack.
     */
    @Environment(EnvType.CLIENT)
    fun registerAssets(
        id: Identifier,
        register: ClientResourcePackBuilder.() -> Unit
    ) {
        Registry.register(
            ArtificeRegistry.ASSETS, id,
            DynamicResourcePackFactory(ResourceType.CLIENT_RESOURCES, id, register)
        )
    }

    /**
     * Register a new server-side resource pack, creating resources with the given callback.
     * @param id The pack ID.
     * @param register A callback which will be passed a [ServerResourcePackBuilder].
     * @return The registered pack.
     */
    fun registerData(id: Identifier, register: ServerResourcePackBuilder.() -> Unit) {
        Registry.register(
            ArtificeRegistry.DATA, id,
            DynamicResourcePackFactory(ResourceType.SERVER_DATA, id, register)
        )
    }
}