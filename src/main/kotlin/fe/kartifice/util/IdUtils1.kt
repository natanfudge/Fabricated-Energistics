package fe.kartifice.util

import net.minecraft.util.Identifier

/** Utilities for modifying [Identifier]s.  */
object IdUtils {

    /**
     * Add a prefix and suffix to the path of the given [Identifier].
     * @param prefix The prefix to add.
     * @param id The base ID.
     * @param suffix The suffix to add.
     * @return A new [Identifier] with the wrapped path.
     */
    @JvmOverloads
    fun wrapPath(
        prefix: String = "",
        id: Identifier,
        suffix: String = ""
    ): Identifier {
        return if (prefix.isEmpty() && suffix.isEmpty()) id else Identifier(id.namespace, prefix + id.path + suffix)
    }

    /**
     * If the given [Identifier] has the namespace "minecraft" (the default namespace),
     * return a copy with the given `defaultNamespace`. Otherwise, return the ID unchanged.
     * @param id The base ID.
     * @param defaultNamespace The namespace to replace `minecraft` with if applicable.
     * @return The given ID with its namespace replaced if applicable.
     */
    fun withDefaultNamespace(id: Identifier, defaultNamespace: String?): Identifier {
        return if (id.namespace == "minecraft") Identifier(defaultNamespace, id.path) else id
    }
}