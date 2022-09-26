package com.example.todo.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.CalendarContract
import android.view.View
import com.google.android.gms.actions.NoteIntents
import java.io.ByteArrayOutputStream


class ShareUtils(
) : IntendGenerator() {

}

open class IntendGenerator() : SendTextIntentGenerator{
    override fun genNewSendTextIntent(title: String, note: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, note)
        }
    }
}

interface SendTextIntentGenerator {
    fun genNewSendTextIntent(title: String, note: String): Intent
}


