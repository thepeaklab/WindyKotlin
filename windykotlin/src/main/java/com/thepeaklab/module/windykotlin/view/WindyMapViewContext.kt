package com.thepeaklab.module.windykotlin.view

/**
 * WindyMapViewContext
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

interface WindyMapViewContext {

    fun initMap(content: String)
    fun evaluateScript(script: String, closure: ((String) -> Unit)? = null)
}