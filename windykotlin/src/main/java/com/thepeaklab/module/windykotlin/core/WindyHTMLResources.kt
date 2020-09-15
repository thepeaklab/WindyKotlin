package com.thepeaklab.module.windykotlin.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import java.util.UUID

/**
 * WindyHTMLResources
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

object WindyHTMLResources {

    fun indexHTML(options: WindyInitOptions): String {

        val gson = Gson()
        val optionsJSONString = gson.toJson(options)

        return "<html>\n" +
                "            <head>\n" +
                "                <meta\n" +
                "                    name=\"viewport\"\n" +
                "                    content=\"width=device-width, initial-scale=1.0, shrink-to-fit=no\"\n" +
                "                />\n" +
                "                <script src=\"https://unpkg.com/leaflet@1.4.0/dist/leaflet.js\"></script>\n" +
                "                <script src=\"https://api4.windy.com/assets/libBoot.js\"></script>\n" +
                "                <style>\n" +
                "                    body {\n" +
                "                        padding: 0;\n" +
                "                        margin: 0;\n" +
                "                    }\n" +
                "                    html, body, #windy {\n" +
                "                        height: 100%;\n" +
                "                        width: 100%;\n" +
                "                    }\n" +
                "                    html body.windy-logo-invisible a#logo {\n" +
                "                        display: none !important;\n" +
                "                    }\n" +
                "                    div#mobile-ovr-select, div#embed-zoom, div#bottom, div#windy-app-promo {\n" +
                "                        display: none !important;\n" +
                "                    }\n" +
                "                </style>\n" +
                "            </head>\n" +
                "            <body>\n" +
                "                <div id=\"windy\"></div>\n" +
                "                <script type=\"text/javascript\">\n" +
                "                    const options = JSON.parse('$optionsJSONString');\n" +
                "                    var globalMap;\n" +
                "                    var markers = {};\n" +
                "\n" +
                "                    function sendNativeMessage(name, options = {}) {\n" +
                "                        var obj = {\n" +
                "                            name: name,\n" +
                "                            options: options\n" +
                "                        };\n" +
                "                        JSBridge.postMessage(JSON.stringify(obj));\n" +
                "                    }\n" +
                "\n" +
                "                    // Initialize Windy API\n" +
                "                    windyInit(options, windyAPI => {\n" +
                "                        const { map, broadcast } = windyAPI;\n" +
                "                        globalMap = map;\n" +
                "\n" +
                "                        var streetMapPane = globalMap.createPane('streetMap');" +
                "                        streetMapPane.style.zIndex = 'auto';\n" +
                "                        var topLayer = L.tileLayer('https://b.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "                            pane: 'streetMap',\n" +
                "                            minZoom: 11,\n" +
                "                            maxZoom: 20,\n" +
                "                        }).addTo(map);\n" +
                "                        topLayer.setOpacity('0');\n" +
                "                        map.options.minZoom = 4;\n" +
                "                        map.options.maxZoom = 18;\n" +
                "                        map.on('zoomend', function() {\n" +
                "                            if (map.getZoom() >= 11) {\n" +
                "                                topLayer.setOpacity('1');\n" +
                "                            } else {\n" +
                "                                topLayer.setOpacity('0');\n" +
                "                            }\n" +
                "                        });" +
                "\n" +
                "                        let events = [\n" +
                "                            'zoomstart',\n" +
                "                            'zoomend',\n" +
                "                            'movestart',\n" +
                "                            'moveend',\n" +
                "                            'zoom',\n" +
                "                            'move'\n" +
                "                        ];\n" +
                "                        events.forEach(function(event) {\n" +
                "                            map.on(event, params => {\n" +
                "                                sendNativeMessage(event, {\n" +
                "                                    bounds: map.getBounds()\n" +
                "                                });\n" +
                "                            });\n" +
                "                        });\n" +
                "                        sendNativeMessage('initialize', {\n" +
                "                            bounds: map.getBounds()\n" +
                "                        });\n" +
                "                    });\n" +
                "                </script>\n" +
                "            </body>\n" +
                "        </html>"
    }

    fun indexHTML(apiKey: String): String {
        return indexHTML(options = WindyInitOptions(apiKey))
    }

    fun panToJSSnippet(coordinate: Coordinate, options: WindyZoomPanOptions?): String {

        val optionsJSONString = options?.let {
            val gson = Gson()
            "," + gson.toJson(options)
        } ?: ""

        return "globalMap.panTo(new L.LatLng(${coordinate.lat}, ${coordinate.lng})$optionsJSONString);0;"
    }

    fun setZoomJSSnippet(zoom: Int, options: WindyZoomPanOptions?): String {

        val optionsJSONString = options?.let {
            val gson = Gson()
            gson.toJson(options)
        } ?: ""

        return "globalMap.setZoom($zoom, $optionsJSONString);0;"
    }

    fun fitBoundsJSSnippet(coordinates: List<Coordinate>): String {

        val coordinatesArray = coordinates.map { arrayOf(it.lat, it.lng) }

        val gson = Gson()
        val jsonStringArray = gson.toJson(coordinatesArray)

        return "globalMap.fitBounds($jsonStringArray);0;"
    }

    fun getCenterJSSnippet(): String {

        return "globalMap.getCenter();"
    }

    /**
     * decode javascript object to kotlin class
     *
     * @return casted object or null
     */
    fun <T> decodeJavaScriptObject(jsObject: String, c: Class<T>): T? {
        try {
            val gson = Gson()
            val decodesObject = gson.fromJson(jsObject, c)
            return decodesObject
        } catch (e: Exception) {
            Log.e("WindyMapView", e.message, e)
        }
        return null
    }

    fun addMarkerJSSnippet(context: Context, marker: Marker): String {

        val iconDTO = marker.icon.toWindyDto(context, marker.uuid)

        val gson = Gson()
        val windyIconJSON = gson.toJson(iconDTO)

        return "var icon = L.icon($windyIconJSON);\n" +
                "var marker = L.marker([${marker.coordinate.lat}, ${marker.coordinate.lng}], {icon: icon});\n" +
                "marker.uuid = \"${marker.uuid}\";\n" +
                "marker.addTo(globalMap);\n" +
                "marker.on('click', function(e) {\n" +
                "   sendNativeMessage('markerclick', {\n" +
                "       bounds: globalMap.getBounds(),\n" +
                "       uuid: this.uuid\n" +
                "   });\n" +
                "});\n" +
                "markers[marker.uuid] = marker;\n" +
                "0;"
    }

    fun removeMarkerJSSnippet(uuid: UUID): String {

        return "var marker = markers[\"$uuid\"];\n" +
                "if (marker) {\n" +
                "   globalMap.removeLayer(marker);\n" +
                "}\n" +
                "0;;"
    }
}