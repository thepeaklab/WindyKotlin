package com.thepeaklab.module.windykotlin.viewmodel

import android.content.Context
import android.content.res.TypedArray
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.databinding.Bindable
import com.thepeaklab.module.windykotlin.BR
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
    val viewContext: WindyMapViewContext,
    val htmlResources: WindyHTMLResources,
    private var options: WindyInitOptions? = null
) : ObservableViewModel() {

    var eventHandler: WindyEventHandler? = null
    private var zoomLevel: Int? = null

    /**
     * init map
     *
     */
    fun initializeMap() {

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

        val N = attrs.indexCount
        for (i in 0 until N) {
            val attr = attrs.getIndex(i)
            when (attr) {
                R.styleable.WindyMapView_apiKey -> apiKey = attrs.getString(attr)
                R.styleable.WindyMapView_lat -> lat = attrs.getFloat(attr, -1f).toDouble()
                R.styleable.WindyMapView_lng -> lng = attrs.getFloat(attr, -1f).toDouble()
                R.styleable.WindyMapView_zoom -> zoom = attrs.getInteger(attr, -1)
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
     * dd marker to map
     *
     */
    fun addMarker(context: Context, marker: Marker) {
        viewContext.evaluateScript(htmlResources.addMarkerJSSnippet(context, marker))
    }

    /**
     * remove marker from map
     *
     */
    fun removeMarker(uuid: UUID) {
        viewContext.evaluateScript(htmlResources.removeMarkerJSSnippet(uuid))
    }

    /**
     * handle windy events
     *
     */
    fun handleEvent(event: String) {

        val content = htmlResources.decodeJavaScriptObject(event, WindyEventContent::class.java)
        content?.let { it ->
            if (it.name == WindyEventContent.EventName.zoomend) {
                zoomLevel = getZoom()
                notifyPropertyChanged(BR.osmVisible)
            }
            eventHandler?.onEvent(it)
        }
    }

    @UiThread
    private fun getZoom(): Int? {

        val javascript = """
        globalMap.getZoom();
        """
        var zoom: Int? = null
        viewContext.evaluateScript(javascript) {
            zoom = htmlResources.decodeJavaScriptObject(it, Int::class.java)
        }
        return zoom
    }

    /**
     * get visibility of osm copyright note
     */
    @Bindable
    fun getOsmVisible(): Int {
        zoomLevel?.let {
            return if (it <= 11) {
                View.GONE
            } else View.VISIBLE
        }
        return View.GONE
    }
}