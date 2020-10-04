package com.thepeaklab.module.windykotlin.viewmodel

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.ObservableBoolean
import com.thepeaklab.module.windykotlin.R
import com.thepeaklab.module.windykotlin.core.ObservableViewModel
import com.thepeaklab.module.windykotlin.core.WindyHTMLResources
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.WindyEventContent
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import com.thepeaklab.module.windykotlin.view.WindyEventHandler
import com.thepeaklab.module.windykotlin.view.WindyMapViewContext
import java.util.UUID

/**
 * WindyMapViewViewModel
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

class WindyMapViewViewModel(
    private val viewContext: WindyMapViewContext,
    private val htmlResources: WindyHTMLResources,
    private var options: WindyInitOptions? = null
) : ObservableViewModel() {

    var eventHandler: WindyEventHandler? = null
    val osmCopyrightVisible = ObservableBoolean(false)
    private var zoomLevel: Int? = null
    private val uuidOfMarkers= mutableListOf<UUID>()

    /**
     * init map
     *
     */
    fun initializeMap() {

        // clear marker list on init
        uuidOfMarkers.clear()

        // load data
        options?.let {
            viewContext.initMap(htmlResources.indexHTML(it))
        } ?: Log.d("WindyMapView", "'options' not set, so init is not possible")
    }

    /**
     * set options for map
     *
     */
    fun setOptions(options: WindyInitOptions) {
        this.options = options
        initializeMap()
    }

    /**
     * get windy options from Attributes
     *
     */
    fun setOptions(attrs: TypedArray) {

        var apiKey: String? = null
        var lat: Double? = null
        var lng: Double? = null
        var zoom: Int? = null
        var showWindyLogo: Boolean? = null

        val n = attrs.indexCount
        for (i in 0 until n) {
            when (val attr = attrs.getIndex(i)) {
                R.styleable.WindyMapView_apiKey -> apiKey = attrs.getString(attr)
                R.styleable.WindyMapView_lat -> lat = attrs.getFloat(attr, -1f).toDouble()
                R.styleable.WindyMapView_lng -> lng = attrs.getFloat(attr, -1f).toDouble()
                R.styleable.WindyMapView_zoom -> zoom = attrs.getInteger(attr, -1)
                R.styleable.WindyMapView_showWindyLogo -> showWindyLogo =
                    attrs.getBoolean(attr, true)
            }
        }
        attrs.recycle()

        apiKey?.let {
            setOptions(
                WindyInitOptions(
                    key = it,
                    lat = lat,
                    lon = lng,
                    zoom = zoom
                )
            )
        }

        showWindyLogo?.let {
            viewContext.setLogoVisibility(it)
        }
    }

    /**
     * move map center to the given cooordinates
     *
     */
    fun panTo(coordinate: Coordinate, options: WindyZoomPanOptions? = null) {
        viewContext.evaluateScript(htmlResources.panToJSSnippet(coordinate, options))
    }

    /**
     * change zoom level
     *
     */
    fun setZoom(zoomLevel: Int, options: WindyZoomPanOptions? = null) {
        this.zoomLevel = zoomLevel
        viewContext.evaluateScript(htmlResources.setZoomJSSnippet(zoomLevel, options))
    }

    /**
     * move and zoom map to fit all given coordinates in display area
     *
     */
    fun fitBounds(coordinates: List<Coordinate>) {
        viewContext.evaluateScript(htmlResources.fitBoundsJSSnippet(coordinates))
    }

    /**
     * get the current center position of the map
     *
     */
    fun getMapCenter(closure: (Coordinate?) -> Unit) {
        viewContext.evaluateScript(htmlResources.getCenterJSSnippet()) {
            val coordinate = htmlResources.decodeJavaScriptObject(it, Coordinate::class.java)
            closure(coordinate)
        }
    }

    /**
     * add marker to map
     *
     */
    fun addMarker(context: Context, marker: Marker) {
        uuidOfMarkers.add(marker.uuid)
        viewContext.evaluateScript(htmlResources.addMarkerJSSnippet(context, marker))
    }

    /**
     * remove marker from map
     *
     */
    fun removeMarker(uuid: UUID) {
        uuidOfMarkers.remove(uuid)
        viewContext.evaluateScript(htmlResources.removeMarkerJSSnippet(uuid))
    }

    /**
     * remove all marker from map
     *
     */
    fun removeAllMarkers() {
        uuidOfMarkers.forEach {
            removeMarker(it)
        }
    }

    /**
     * handle windy events
     *
     */
    fun handleEvent(event: String) {

        val content = htmlResources.decodeJavaScriptObject(event, WindyEventContent::class.java)
        content?.let { it ->
            if (it.name == WindyEventContent.EventName.zoomend) {
                Handler(Looper.getMainLooper()).post {
                    getZoom()
                }
            }
            eventHandler?.onEvent(it)
        }
    }

    private fun getZoom() {

        val javascript = """
        globalMap.getZoom();
        """
        viewContext.evaluateScript(javascript) {
            val result = htmlResources.decodeJavaScriptObject(it, Double::class.java)
            result?.toInt().let {
                zoomLevel = it
                osmCopyrightVisible.set(zoomLevel!! > 11)
            }
        }
    }

    /**
     * updates the visibility of Windy logo
     *
     */
    fun updateWindyLogoVisibility(value: Boolean) {
        val addOrRemove = if (value) "remove" else "add"
        val javascript =
            """
        var bodyElement = document.querySelector("body");
        bodyElement.classList.$addOrRemove("windy-logo-invisible");
        """
        viewContext.evaluateScript(javascript)
    }
}