package com.thepeaklab.module.windykotlin.core.models

import java.util.UUID

/**
 * Marker
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-27.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

data class Marker(val uuid: UUID, val coordinate: Coordinate, val icon: WindyIcon)