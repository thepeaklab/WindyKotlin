package com.thepeaklab.module.windykotlin.core

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * WindyKotlin
 *
 * Created by Niels Hillemeyer on 2020-09-16.
 * Copyright © 2020 the peak lab. gmbh & co. kg. All rights reserved.
 */
object WindyViewBinding {

    /**
     * handle visibility according to boolean.
     *
     * Keep Measurement makes view INVISIBLE instead of GONE
     *
     */
    @JvmStatic
    @BindingAdapter(
        value = [
            "app:visibility",
            "app:visibility_keepMeasurement"],
        requireAll = false
    )
    fun setVisibility(view: View, visibility: Boolean, keepMeasurement: Boolean = false) {
        if (visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = if (keepMeasurement) {
                View.INVISIBLE
            } else {
                View.GONE
            }
        }
    }
}