package com.thepeaklab.module.windykotlin.core

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Icon
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.Point
import com.thepeaklab.module.windykotlin.core.models.WindyIcon
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import java.io.File
import java.util.UUID
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`

/**
 * WindyMapViewViewModelTest
 * Windykotlin
 *
 * Created by Thomas Cirksena on 2019-11-28.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

@Suppress("UNCHECKED_CAST")
class WindyHTMLResourcesTest {

    @JvmField
    @Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var context: Context

    @Before
    fun setUp() {
        context = mock()
    }

    @Test
    fun `indexHTML | with options | options are inserted correctly`() {

        // define test data
        val options = WindyInitOptions(
            key = "abc",
            verbose = true,
            lat = 58.1,
            lon = 8.9,
            zoom = 5
        )
        @Language("JSON")
        val optionsJSON = "{\"key\":\"abc\",\"verbose\":true,\"lat\":58.1,\"lon\":8.9,\"zoom\":5}"
        val expectedSnippet = "<html>\n" +
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
                "                    const options = JSON.parse('$optionsJSON');\n" +
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

        // trigger action
        val snippet = WindyHTMLResources.indexHTML(options)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `indexHTML | with api key | options are inserted correctly`() {

        // define test data
        @Language("JSON")
        val optionsJSON = "{\"key\":\"abc\"}"
        val expectedSnippet = "<html>\n" +
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
                "                    const options = JSON.parse('$optionsJSON');\n" +
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

        // trigger action
        val snippet = WindyHTMLResources.indexHTML("abc")

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `panToJSSnippet | without options | options are inserted correctly`() {

        // define test data
        val coordinate = Coordinate(58.1, 8.9)
        val expectedSnippet = "globalMap.panTo(new L.LatLng(58.1, 8.9));0;"

        // trigger action
        val snippet = WindyHTMLResources.panToJSSnippet(coordinate, null)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `panToJSSnippet | with options | options are inserted correctly`() {

        // define test data
        val coordinate = Coordinate(58.1, 8.9)
        val options = WindyZoomPanOptions(
            animate = true,
            duration = 2.0,
            easeLinearity = 3.0,
            noMoveStart = false
        )
        @Language("JSON")
        val optionsJSON = "{\"animate\":true,\"duration\":2.0,\"easeLinearity\":3.0,\"noMoveStart\":false}"
        val expectedSnippet = "globalMap.panTo(new L.LatLng(58.1, 8.9),$optionsJSON);0;"

        // trigger action
        val snippet = WindyHTMLResources.panToJSSnippet(coordinate, options)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `setZoomJSSnippet | without options | options are inserted correctly`() {

        // define test data
        val zoomlevel = 7
        val expectedSnippet = "globalMap.setZoom(7, );0;"

        // trigger action
        val snippet = WindyHTMLResources.setZoomJSSnippet(zoomlevel, null)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `setZoomJSSnippet | with options | options are inserted correctly`() {

        // define test data
        val zoomlevel = 7
        val options = WindyZoomPanOptions(
            animate = true,
            duration = 2.0,
            easeLinearity = 3.0,
            noMoveStart = false
        )
        @Language("JSON")
        val optionsJSON = "{\"animate\":true,\"duration\":2.0,\"easeLinearity\":3.0,\"noMoveStart\":false}"
        val expectedSnippet = "globalMap.setZoom(7, $optionsJSON);0;"

        // trigger action
        val snippet = WindyHTMLResources.setZoomJSSnippet(zoomlevel, options)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `fitBoundsJSSnippet | with coordinates | options are inserted correctly`() {

        // define test data
        val coordinates = listOf(
            Coordinate(58.1, 8.1),
            Coordinate(58.2, 8.2)
        )
        val expectedSnippet = "globalMap.fitBounds([[58.1,8.1],[58.2,8.2]]);0;"

        // trigger action
        val snippet = WindyHTMLResources.fitBoundsJSSnippet(coordinates)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `getCenterJSSnippet | return correct snippet code`() {

        // define test data
        val expectedSnippet = "globalMap.getCenter();"

        // trigger action
        val snippet = WindyHTMLResources.getCenterJSSnippet()

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    @Test
    fun `addMarkerJSSnippet | icon from url | attributes are converted correctly`() {

        // define test data
        val marker = Marker(
            uuid = UUID.fromString("bdfb7095-5e98-4db8-adc4-3b5953e1eabd"),
            coordinate = Coordinate(58.1, 8.2),
            icon = WindyIcon(
                icon = Icon(url = "http://www.mein.de/test/bild.jpg"),
                iconSize = Point(20, 20),
                iconAnchor = Point(2, 5),
                popupAnchor = Point(20, 0),
                shadowAnchor = Point(0, 0),
                shadowUrl = "http://www.mein.de/test/bild_schatten.jpg",
                shadowSize = Point(20, 20)
            )
        )
        @Language("JSON")
        val windyIconJSON = "{\"iconUrl\":\"http://www.mein.de/test/bild.jpg\",\"iconSize\":[20,20],\"iconAnchor\":[2,5],\"popupAnchor\":[20,0],\"shadowUrl\":\"http://www.mein.de/test/bild_schatten.jpg\",\"shadowSize\":[20,20],\"shadowAnchor\":[0,0]}"
        val expectedSnippet = "var icon = L.icon($windyIconJSON);\n" +
                "var marker = L.marker([58.1, 8.2], {icon: icon});\n" +
                "marker.uuid = \"bdfb7095-5e98-4db8-adc4-3b5953e1eabd\";\n" +
                "marker.addTo(globalMap);\n" +
                "marker.on('click', function(e) {\n" +
                "   sendNativeMessage('markerclick', {\n" +
                "       bounds: globalMap.getBounds(),\n" +
                "       uuid: this.uuid\n" +
                "   });\n" +
                "});\n" +
                "markers[marker.uuid] = marker;\n" +
                "0;"

        // trigger action
        val snippet = WindyHTMLResources.addMarkerJSSnippet(context, marker)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    /**
     * It's only possible to check if the return value for the 'iconUrl' is empty("") because the BitmapFactory and
     * the file creation cannot be mocked correctly
     *
     */
    @Test
    fun `addMarkerJSSnippet | icon from drawable | attributes are converted correctly`() {

        @DrawableRes val drawableRes: Int = android.R.drawable.ic_delete

        // define test data
        val marker = Marker(
            uuid = UUID.fromString("bdfb7095-5e98-4db8-adc4-3b5953e1eabd"),
            coordinate = Coordinate(58.1, 8.2),
            icon = WindyIcon(
                icon = Icon(drawable = drawableRes),
                iconSize = Point(20, 20),
                iconAnchor = Point(2, 5),
                popupAnchor = Point(20, 0),
                shadowAnchor = Point(0, 0),
                shadowUrl = "http://www.mein.de/test/bild_schatten.jpg",
                shadowSize = Point(20, 20)
            )
        )
        @Language("JSON")
        val windyIconJSON = "{\"iconUrl\":\"\",\"iconSize\":[20,20],\"iconAnchor\":[2,5],\"popupAnchor\":[20,0],\"shadowUrl\":\"http://www.mein.de/test/bild_schatten.jpg\",\"shadowSize\":[20,20],\"shadowAnchor\":[0,0]}"
        val expectedSnippet = "var icon = L.icon($windyIconJSON);\n" +
                "var marker = L.marker([58.1, 8.2], {icon: icon});\n" +
                "marker.uuid = \"bdfb7095-5e98-4db8-adc4-3b5953e1eabd\";\n" +
                "marker.addTo(globalMap);\n" +
                "marker.on('click', function(e) {\n" +
                "   sendNativeMessage('markerclick', {\n" +
                "       bounds: globalMap.getBounds(),\n" +
                "       uuid: this.uuid\n" +
                "   });\n" +
                "});\n" +
                "markers[marker.uuid] = marker;\n" +
                "0;"
        val cacheDir: File = mock()
        whenever(context.cacheDir).thenReturn(cacheDir)
        whenever(cacheDir.absolutePath).thenReturn("test")

        // trigger action
        val snippet = WindyHTMLResources.addMarkerJSSnippet(context, marker)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }

    /**
     * It's only possible to check if the return value for the 'iconUrl' is empty("") because the BitmapFactory and
     * the file creation cannot be mocked correctly
     *
     */
    @Test
    fun `addMarkerJSSnippet | icon from asset | attributes are converted correctly`() {

        // register stubbing
        `when`<Any>(null).thenReturn("hey")

        // define test data
        val marker = Marker(
            uuid = UUID.fromString("bdfb7095-5e98-4db8-adc4-3b5953e1eabd"),
            coordinate = Coordinate(58.1, 8.2),
            icon = WindyIcon(
                icon = Icon(asset = "testimage.gif"),
                iconSize = Point(20, 20),
                iconAnchor = Point(2, 5),
                popupAnchor = Point(20, 0),
                shadowAnchor = Point(0, 0),
                shadowUrl = "http://www.mein.de/test/bild_schatten.jpg",
                shadowSize = Point(20, 20)
            )
        )
        @Language("JSON")
        val windyIconJSON = "{\"iconUrl\":\"\",\"iconSize\":[20,20],\"iconAnchor\":[2,5],\"popupAnchor\":[20,0],\"shadowUrl\":\"http://www.mein.de/test/bild_schatten.jpg\",\"shadowSize\":[20,20],\"shadowAnchor\":[0,0]}"
        val expectedSnippet = "var icon = L.icon($windyIconJSON);\n" +
                "var marker = L.marker([58.1, 8.2], {icon: icon});\n" +
                "marker.uuid = \"bdfb7095-5e98-4db8-adc4-3b5953e1eabd\";\n" +
                "marker.addTo(globalMap);\n" +
                "marker.on('click', function(e) {\n" +
                "   sendNativeMessage('markerclick', {\n" +
                "       bounds: globalMap.getBounds(),\n" +
                "       uuid: this.uuid\n" +
                "   });\n" +
                "});\n" +
                "markers[marker.uuid] = marker;\n" +
                "0;"

        // trigger action
        val snippet = WindyHTMLResources.addMarkerJSSnippet(context, marker)

        // check assertions
        assertEquals(expectedSnippet, snippet)
    }
}