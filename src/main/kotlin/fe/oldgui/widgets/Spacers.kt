package spatialcrafting.client.gui.widgets

import fe.oldgui.Constraints
import fe.oldgui.DevWidget
import fe.oldgui.RuntimeWidget
import fe.oldgui.widgets.core.Overlay

class VerticalSpacerClass(height: Int, overlay: Overlay?) : NoChildDevWidget(overlay) {
    override val minimumHeight = height
    override val minimumWidth = 0

    override fun getLayout(constraints: Constraints): RuntimeWidget = runtimeWidget(constraints) {}
}

fun DevWidget.VerticalSpace(height: Int) : DevWidget = add(VerticalSpacerClass(height,overlay))

class HorizontalSpacerClass(width: Int, overlay: Overlay?) : NoChildDevWidget(overlay) {
    override val minimumHeight = 0
    override val minimumWidth = width

    override fun getLayout(constraints: Constraints): RuntimeWidget = runtimeWidget(constraints) {}
}

fun DevWidget.HorizontalSpace(width: Int) : DevWidget = add(HorizontalSpacerClass(width,overlay))