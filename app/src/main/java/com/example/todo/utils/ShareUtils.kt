package com.example.todo.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.ByteArrayOutputStream


object ShareUtils : BitmapUtils() {
    fun getTodoByteArray(view: View): ByteArray {
        val bitmap = viewToBitmap(view)
        return bitmapToByteArray(bitmap)
    }
}

open class BitmapUtils {
    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}