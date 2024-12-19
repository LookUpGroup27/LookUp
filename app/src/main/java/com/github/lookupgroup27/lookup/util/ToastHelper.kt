package com.github.lookupgroup27.lookup.util

import android.content.Context
import android.widget.Toast

open class ToastHelper(private val context: Context) {
    open fun showNoInternetToast() {
        Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show()
    }
}