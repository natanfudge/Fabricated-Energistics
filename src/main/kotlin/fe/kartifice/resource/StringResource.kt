package fe.kartifice.resource

import java.io.ByteArrayInputStream
import java.io.InputStream

/** A virtual resource representing an arbitrary string.  */
class StringResource(vararg lines: String?) : KArtificeResource<String> {
    override val data: String

    override fun toOutputString(): String? {
        return data
    }

    override fun toInputStream(): InputStream {
        return ByteArrayInputStream(data.toByteArray())
    }

    /** @param lines Individual lines of the string this resource file contains.
     */
    init {
        data = java.lang.String.join("\n", *lines)
    }
}