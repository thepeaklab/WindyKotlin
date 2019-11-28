package com.thepeaklab.module.windykotlin.core.models

/**
 * Point
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-27.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved. *
 */

data class Point(val x: Int, val y: Int) {
    fun arrayRepresentation(): Array<Int> {
        return arrayOf(x, y)
    }
}