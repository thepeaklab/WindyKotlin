package com.thepeaklab.module.windykotlin.core.models

/**
 * WindyDto
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-27.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved. *
 */

data class WindyIconDTO(
    val iconUrl: String,
    val iconSize: Array<Int>? = null,
    val iconAnchor: Array<Int>? = null,
    val popupAnchor: Array<Int>? = null,
    val shadowUrl: String? = null,
    val shadowSize: Array<Int>? = null,
    val shadowAnchor: Array<Int>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WindyIconDTO

        if (iconUrl != other.iconUrl) return false
        if (iconSize != null) {
            if (other.iconSize == null) return false
            if (!iconSize.contentEquals(other.iconSize)) return false
        } else if (other.iconSize != null) return false
        if (iconAnchor != null) {
            if (other.iconAnchor == null) return false
            if (!iconAnchor.contentEquals(other.iconAnchor)) return false
        } else if (other.iconAnchor != null) return false
        if (popupAnchor != null) {
            if (other.popupAnchor == null) return false
            if (!popupAnchor.contentEquals(other.popupAnchor)) return false
        } else if (other.popupAnchor != null) return false
        if (shadowUrl != other.shadowUrl) return false
        if (shadowSize != null) {
            if (other.shadowSize == null) return false
            if (!shadowSize.contentEquals(other.shadowSize)) return false
        } else if (other.shadowSize != null) return false
        if (shadowAnchor != null) {
            if (other.shadowAnchor == null) return false
            if (!shadowAnchor.contentEquals(other.shadowAnchor)) return false
        } else if (other.shadowAnchor != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconUrl.hashCode()
        result = 31 * result + (iconSize?.contentHashCode() ?: 0)
        result = 31 * result + (iconAnchor?.contentHashCode() ?: 0)
        result = 31 * result + (popupAnchor?.contentHashCode() ?: 0)
        result = 31 * result + (shadowUrl?.hashCode() ?: 0)
        result = 31 * result + (shadowSize?.contentHashCode() ?: 0)
        result = 31 * result + (shadowAnchor?.contentHashCode() ?: 0)
        return result
    }
}