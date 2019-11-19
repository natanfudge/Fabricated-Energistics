package fe.client.model.part

import alexiil.mc.lib.multipart.api.render.PartModelKey

object TankPartModelKey : PartModelKey() {
    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)
}