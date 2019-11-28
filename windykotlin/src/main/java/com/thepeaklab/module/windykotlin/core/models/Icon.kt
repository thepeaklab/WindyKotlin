package com.thepeaklab.module.windykotlin.core.models

import androidx.annotation.DrawableRes

/**
 * Icon
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-27.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved. *
 */

data class Icon(val url: String? = null, @DrawableRes val drawable: Int? = null, val asset: String? = null)