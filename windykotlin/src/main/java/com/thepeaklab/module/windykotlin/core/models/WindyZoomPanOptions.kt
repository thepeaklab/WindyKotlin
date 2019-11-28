package com.thepeaklab.module.windykotlin.core.models

/**
 * WindyZoomPanOptions
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-20.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

data class WindyZoomPanOptions(
    val animate: Boolean? = null,
    val duration: Double? = null,
    val easeLinearity: Double? = null,
    val noMoveStart: Boolean? = null
)