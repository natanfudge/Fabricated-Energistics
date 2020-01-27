package fe.kartifice.virtualpack

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.resource.ClientResourcePackProfile
import net.minecraft.resource.ResourcePackProfile
import java.util.function.Supplier

/** A wrapper around [ClientResourcePackProfile] exposing optionality/visibility.
 * @see ClientResourcePackBuilder.setOptional
 *
 * @see ClientResourcePackBuilder.setVisible
 */
@Environment(EnvType.CLIENT)
class KArtificeResourcePackContainer(val isOptional: Boolean, val isVisible: Boolean, wrapping: ResourcePackProfile) :
    ClientResourcePackProfile(
        wrapping.name,
        !isOptional, Supplier { wrapping.createResourcePack() },
        wrapping.displayName,
        wrapping.description,
        wrapping.compatibility,
        wrapping.initialPosition,
        wrapping.isPinned,
        null
    ) {
    /** @return Whether this pack is optional.
     */
    /** @return Whether this pack is visible.
     */

}