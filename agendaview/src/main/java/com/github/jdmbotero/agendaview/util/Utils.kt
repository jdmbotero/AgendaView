package com.github.jdmbotero.agendaview.util

import android.content.Context
import android.content.res.Configuration

object Utils {

    fun getScreenSize(context: Context): IntArray {
        val displayMetrics = context.resources.displayMetrics

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && width > height) {
            val size = width
            width = height
            height = size
        }

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && height > width) {
            val size = height
            height = width
            width = size
        }

        return intArrayOf(width, height)
    }
}