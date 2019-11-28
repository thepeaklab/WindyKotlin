package com.thepeaklab.module.windykotlin.core.models

import java.util.UUID

/**
 * WindyEventContent
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-25.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */
data class WindyEventContent(val name: EventName? = null, val options: Options? = null) {

    // MARK: EventName
    enum class EventName {

        initialize,

        zoomstart,
        zoomend,
        movestart,
        moveend,

        zoom,
        move,

        markerclick
    }

    // MARK: Options
    class Options {

        var bounds: Bounds? = null
        var uuid: UUID? = null

        data class Bounds(
            val _northEast: Coordinates,
            val _southWest: Coordinates
        ) {

            data class Coordinates(val lat: Double, val lng: Double)

            enum class CodingKeys(val value: String) {
                NORTHEAST("_northEast"),
                SOUTHWEST("_southWest");
            }
        }
    }
}