package fe.kartifice.builder.assets

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.kartifice.builder.TypedJsonBuilder
import fe.kartifice.resource.JsonResource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * Builder for a particle definition (`namespace:particles/particleid.json`).
 */
@Environment(EnvType.CLIENT)
class ParticleBuilder : TypedJsonBuilder<JsonResource<JsonObject>>(JsonObject(), { JsonResource(it) }) {
    /**
     * Add a texture to this particle.
     * Calling this multiple times will add to the list instead of overwriting.
     * @param id The texure ID (`namespace:textureid`).
     * @return this
     */
    fun texture(id: Identifier): ParticleBuilder {
        with("textures", ::JsonArray) {  add(id.toString()) }
        return this
    }
}