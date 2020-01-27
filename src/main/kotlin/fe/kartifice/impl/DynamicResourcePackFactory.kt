package fe.kartifice.impl

import com.swordglowsblue.artifice.common.ClientResourcePackProfileLike
import com.swordglowsblue.artifice.common.ServerResourcePackProfileLike
import fe.kartifice.ResourcePackBuilder
import net.minecraft.client.resource.ClientResourcePackProfile
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

class DynamicResourcePackFactory<T : ResourcePackBuilder>(private val type : ResourceType, private val id : Identifier, private val init : T.() -> Unit)
    : ClientResourcePackProfileLike, ServerResourcePackProfileLike {
    override fun <T : ResourcePackProfile> toClientResourcePackProfile(factory: ResourcePackProfile.Factory<T>): ClientResourcePackProfile {
        return KArtificeResourcePackImpl(type,id, init).toClientResourcePackProfile(factory)
    }

    override fun <T : ResourcePackProfile> toServerResourcePackProfile(factory: ResourcePackProfile.Factory<T>): ResourcePackProfile {
        return KArtificeResourcePackImpl(type,id, init).toServerResourcePackProfile(factory)
    }
}