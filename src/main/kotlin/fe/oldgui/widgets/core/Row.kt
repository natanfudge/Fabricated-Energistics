package spatialcrafting.client.gui.widgets.core

import fe.oldgui.Constraints
import fe.oldgui.DevWidget
import fe.oldgui.RuntimeWidget
import fe.oldgui.widgets.core.Overlay
import spatialcrafting.client.gui.widgets.core.Direction.LeftToRight
import spatialcrafting.client.gui.widgets.core.MainAxisAlignment.Start
import spatialcrafting.client.gui.widgets.runtimeWidget
import spatialcrafting.util.maxValueBy


enum class Direction {
    LeftToRight, TopToBottom
}

class RowClass(mainAxisAlignment: MainAxisAlignment,
               mainAxisSize : FlexSize = FlexSize.Expand,
               crossAxisAlignment: CrossAxisAlignment,
               override val composeDirectChildren: DevWidget.() -> Unit,
               overlay: Overlay?) : Flex(mainAxisAlignment,crossAxisAlignment, mainAxisSize, overlay) {
    override val minimumHeight get() = devChildren.maxValueBy { it.minimumHeight } ?: 0
    override val minimumWidth get() = devChildren.sumBy { it.minimumWidth }
    override val expandHeight get() = devChildren.any { it.expandHeight }
    override val expandWidth = mainAxisSize == FlexSize.Expand


    override fun getLayout(constraints: Constraints): RuntimeWidget = runtimeWidget(
            constraints = constraints, children = positionFlexLayout(constraints, direction = LeftToRight), debugIdentifier = "Row"
    ) {
        for (child in it.runtimeChildren) child.draw()
    }
}

fun DevWidget.Row(mainAxisAlignment: MainAxisAlignment = Start,
                                               mainAxisSize: FlexSize = FlexSize.Expand,
                                               crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
                                               children: DevWidget.() -> Unit): DevWidget =
        add(RowClass(mainAxisAlignment,mainAxisSize, crossAxisAlignment, children,overlay))

