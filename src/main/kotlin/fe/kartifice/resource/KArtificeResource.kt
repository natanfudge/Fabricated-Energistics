package fe.kartifice.resource

import java.io.InputStream

/** A virtual resource file.  */
interface KArtificeResource<T> {
    /** @return The raw data contained by this resource file.
     */
    val data: T

    /** @return The output-formatted string representation of this resource's data.
     */
    fun toOutputString(): String?

    /** @return This resource converted to an [InputStream].
     */
    fun toInputStream(): InputStream
}