package com.facilitation.view.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/** Class to handle the logic of coloring the Canvas of the Snake game.
 * @constructor
 * @param context the Context in which the SnakeView will be operating.
 * @property paint the Paint object to use for drawing.*/
class SnakeView(context: Context) : View(context) {
    private val paint = Paint()

    /** Calls super.onDraw(), sets the whole canvas to black and draws a green rectangle.
     * @param canvas the Canvas to be colored.*/
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        paint.color = Color.GREEN
        canvas.drawRect(100f, 100f, 200f, 200f, paint)
    }
}
