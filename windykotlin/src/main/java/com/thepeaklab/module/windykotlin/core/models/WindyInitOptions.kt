package com.thepeaklab.module.windykotlin.core.models

/**
 * WindyInitOptions
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

data class WindyInitOptions(
    val key: String,
    val verbose: Boolean? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val zoom: Int? = null
)