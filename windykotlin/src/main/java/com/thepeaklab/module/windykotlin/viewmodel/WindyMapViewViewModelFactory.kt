package com.thepeaklab.module.windykotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thepeaklab.module.windykotlin.core.WindyHTMLResources
import com.thepeaklab.module.windykotlin.view.WindyMapViewContext
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions

/**
 * WindyMapViewViewModelFactory
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 *
 * Factory class for creating ViewModel
 */

@Suppress("UNCHECKED_CAST")
class WindyMapViewViewModelFactory(val viewContext: WindyMapViewContext, val htmlResoutces:WindyHTMLResources, val options: WindyInitOptions?) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WindyMapViewViewModel(viewContext, htmlResoutces, options) as T
    }
}