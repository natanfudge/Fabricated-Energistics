package fe.kartifice.resource

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

/** A virtual resource representing a string with template expansions.
 * Templates in the string take the form `$key`.
 * @see TemplateResource.expand
 */
class TemplateResource(vararg template: String?) : KArtificeResource<String> {
    private val template: String
    private val expansions: MutableMap<String, String> = HashMap()
    /**
     * Set the expansion string for a given key.
     * @param key The key to be expanded (ex. `"key"` expands `$key`).
     * @param expansion The expanded string.
     * @return this
     */
    fun expand(key: String, expansion: String): TemplateResource {
        expansions[key] = expansion
        return this
    }

    override fun toInputStream(): InputStream {
        return ByteArrayInputStream(data.toByteArray())
    }

    override fun toOutputString(): String? {
        return data
    }

    override val data: String
        get() {
            var expanded = template

            for ((key,value) in expansions) expanded = expanded.replace(("\\$$key").toRegex(), value)
            return expanded
        }

    /** @param template Individual lines of the template string.
     */
    init {
        this.template = java.lang.String.join("\n", *template)
    }
}