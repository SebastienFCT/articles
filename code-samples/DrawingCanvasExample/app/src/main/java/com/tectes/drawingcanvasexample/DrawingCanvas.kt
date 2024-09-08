package com.tectes.drawingcanvasexample

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    vm: DrawingCanvasViewModel
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { vm.onTapGesture(it) }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { vm.onDragStart(it) },
                    onDragEnd = { vm.onDragEnd() },
                    onDragCancel = { vm.onDragCancel() },
                    onDrag = { _, amount -> vm.onDrag(amount) }
                )
            }
    ) {
        vm.paths.forEach { drawPath(path = it, color = Color.Black, style = Stroke(10f)) }

        if (vm.currentPath.value != null && vm.currentPathRef.intValue > 0) {
            drawPath(path = vm.currentPath.value!!, color = Color.Black, style = Stroke(10f))
        }
    }
}