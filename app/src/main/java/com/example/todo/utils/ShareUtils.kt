package com.example.todo.utils

import android.content.Intent

class ShareUtils(
) : SendTextIntentGenerator {
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


