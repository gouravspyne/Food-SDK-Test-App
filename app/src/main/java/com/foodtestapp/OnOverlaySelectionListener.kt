package com.foodtestapp

import android.view.View

interface OnOverlaySelectionListener {
    fun onOverlaySelected(view: View, position: Int = -1, data: Any? = null)
}

