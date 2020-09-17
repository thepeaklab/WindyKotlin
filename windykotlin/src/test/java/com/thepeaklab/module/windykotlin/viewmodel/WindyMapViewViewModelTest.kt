package com.thepeaklab.module.windykotlin.viewmodel

import android.content.Context
import android.content.res.TypedArray
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.thepeaklab.module.windykotlin.R
import com.thepeaklab.module.windykotlin.core.WindyHTMLResources
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Icon
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.WindyEventContent
import com.thepeaklab.module.windykotlin.core.models.WindyIcon
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import com.thepeaklab.module.windykotlin.view.WindyMapViewContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

/**
 * WindyMapViewViewModelTest
 * WindyKotlin
 *
 * Created by Thomas Cirksena on 2019-11-28.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

@Suppress("UNCHECKED_CAST")
class WindyMapViewViewModelTest {

    @JvmField
    @Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewContext: WindyMapViewContext
    lateinit var context: Context
    lateinit var htmlResources: WindyHTMLResources

    @Before
    fun setUp() {
        viewContext = mock()
        context = mock()
        htmlResources = mock()
    }

    @Test
    fun `initializeMap | no options | don't run map init`() {

        // init ViewModel
        WindyMapViewViewModel(viewContext, htmlResources)

        // check assertions
        verify(viewContext, times(0)).initMap(any())
    }

    @Test
    fun `initializeMap | with options | run map init`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )

        // define mock behavior
        whenever(htmlResources.indexHTML(any<WindyInitOptions>())).thenReturn("html-content")

        // init ViewModel
        val viewModel = WindyMapViewViewModel(viewContext, htmlResources, options)

        // trigger action
        viewModel.initializeMap()

        // check assertions
        verify(viewContext, times(1)).initMap(any())
    }

    @Test
    fun `initializeMap | with options | html content is loaded`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )

        // define mock behavior
        whenever(htmlResources.indexHTML(any<WindyInitOptions>())).thenReturn("html-content")

        // init ViewModel
        val viewModel = WindyMapViewViewModel(viewContext, htmlResources, options)

        // trigger action
        viewModel.initializeMap()

        // check assertions
        verify(htmlResources, times(1)).indexHTML(eq(options))
    }

    @Test
    fun `options | set by WindyInitOptions | map init is requested`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )

        // define mock behavior
        whenever(htmlResources.indexHTML(any<WindyInitOptions>())).thenReturn("html-content")

        // init ViewModel
        val viewModel = spy(WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.setOptions(options)

        // check assertions
        verify(viewModel, times(1)).initializeMap()
    }

    @Test
    fun `options | set by TypedArray without 'apiKey' | map init is not requested`() {

        // define test data
        val typedArray: TypedArray = mock()
        val apiKey = "Ma-API-Key"
        val lat = 52.0f
        val lng = 8.0f
        val zoom = 5

        // define mock behavior
        whenever(typedArray.indexCount).thenReturn(3)
        whenever(typedArray.getIndex(eq(1))).thenReturn(R.styleable.WindyMapView_lat)
        whenever(typedArray.getIndex(eq(2))).thenReturn(R.styleable.WindyMapView_lng)
        whenever(typedArray.getIndex(eq(3))).thenReturn(R.styleable.WindyMapView_zoom)
        whenever(typedArray.getFloat(eq(R.styleable.WindyMapView_lat), eq(-1f))).thenReturn(lat)
        whenever(typedArray.getFloat(eq(R.styleable.WindyMapView_lng), eq(-1f))).thenReturn(lng)
        whenever(typedArray.getInteger(eq(R.styleable.WindyMapView_zoom), eq(-1))).thenReturn(zoom)

        // init ViewModel
        val viewModel = spy(WindyMapViewViewModel(viewContext, htmlResources))

        // trigger action
        viewModel.setOptions(typedArray)

        // check assertions
        verify(viewModel, times(0)).setOptions(any<WindyInitOptions>())
    }

    @Test
    fun `options | set by TypedArray with 'apiKey' | map init is requested`() {

        // define test data
        val apiKey = "Ma-API-Key"
        val lat = 52.0f
        val lng = 8.0f
        val zoom = 5
        val typedArray: TypedArray = mock()

        // define mock behavior
        whenever(typedArray.indexCount).thenReturn(4)
        whenever(typedArray.getIndex(eq(0))).thenReturn(R.styleable.WindyMapView_apiKey)
        whenever(typedArray.getIndex(eq(1))).thenReturn(R.styleable.WindyMapView_lat)
        whenever(typedArray.getIndex(eq(2))).thenReturn(R.styleable.WindyMapView_lng)
        whenever(typedArray.getIndex(eq(3))).thenReturn(R.styleable.WindyMapView_zoom)
        whenever(typedArray.getString(eq(R.styleable.WindyMapView_apiKey))).thenReturn(apiKey)
        whenever(typedArray.getFloat(eq(R.styleable.WindyMapView_lat), eq(-1f))).thenReturn(lat)
        whenever(typedArray.getFloat(eq(R.styleable.WindyMapView_lng), eq(-1f))).thenReturn(lng)
        whenever(typedArray.getInteger(eq(R.styleable.WindyMapView_zoom), eq(-1))).thenReturn(zoom)

        // init ViewModel
        val viewModel = spy(WindyMapViewViewModel(viewContext, htmlResources))

        // trigger action
        viewModel.setOptions(typedArray)

        // check assertions
        verify(viewModel, times(1)).initializeMap()
    }

    @Test
    fun `panTo | without options | request js-snippet without options`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val coordinate = Coordinate(51.0, 8.0)

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.panTo(coordinate)

        // check assertions
        verify(htmlResources, times(1)).panToJSSnippet(eq(coordinate), eq(null))
    }

    @Test
    fun `panTo | with options | request js-snippet with options`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val zoomPanOptions = WindyZoomPanOptions(animate = true)
        val coordinate = Coordinate(51.0, 8.0)

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.panTo(coordinate, zoomPanOptions)

        // check assertions
        verify(htmlResources, times(1)).panToJSSnippet(eq(coordinate), eq(zoomPanOptions))
    }

    @Test
    fun `panTo | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val zoomPanOptions = WindyZoomPanOptions(animate = true)
        val coordinate = Coordinate(51.0, 8.0)

        // define mock behavior
        whenever(htmlResources.panToJSSnippet(eq(coordinate), eq(zoomPanOptions))).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.panTo(coordinate, zoomPanOptions)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), eq(null))
    }

    @Test
    fun `setZoom | without options | request js-snippet without options`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val zoomLevel = 6

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.setZoom(zoomLevel)

        // check assertions
        verify(htmlResources, times(1)).setZoomJSSnippet(eq(zoomLevel), eq(null))
    }

    @Test
    fun `setZoom | with options | request js-snippet with options`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val zoomPanOptions = WindyZoomPanOptions(animate = true)
        val zoomLevel = 6

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.setZoom(zoomLevel, zoomPanOptions)

        // check assertions
        verify(htmlResources, times(1)).setZoomJSSnippet(eq(zoomLevel), eq(zoomPanOptions))
    }

    @Test
    fun `setZoom | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val zoomPanOptions = WindyZoomPanOptions(animate = true)
        val zoomLevel = 6

        // define mock behavior
        whenever(htmlResources.setZoomJSSnippet(eq(zoomLevel), eq(zoomPanOptions))).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.setZoom(6, zoomPanOptions)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), eq(null))
    }

    @Test
    fun `fitBounds | request js-snippet`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val coordinates = listOf(
            Coordinate(1.0, 2.0),
            Coordinate(3.0, 4.0),
            Coordinate(5.0, 6.0)
        )

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.fitBounds(coordinates)

        // check assertions
        verify(htmlResources, times(1)).fitBoundsJSSnippet(eq(coordinates))
    }

    @Test
    fun `fitBounds | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val coordinates = listOf(
            Coordinate(1.0, 2.0),
            Coordinate(3.0, 4.0),
            Coordinate(5.0, 6.0)
        )

        // define mock behavior
        whenever(htmlResources.fitBoundsJSSnippet(eq(coordinates))).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.fitBounds(coordinates)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), eq(null))
    }

    @Test
    fun `getMapCenter | request js-snippet`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val closure: (Coordinate?) -> Unit = mock()

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.getMapCenter(closure)

        // check assertions
        verify(htmlResources, times(1)).getCenterJSSnippet()
    }

    @Test
    fun `getMapCenter | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val closure: (Coordinate?) -> Unit = mock()

        // define mock behavior
        whenever(htmlResources.getCenterJSSnippet()).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.getMapCenter(closure)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), any())
    }

    @Test
    fun `getMapCenter | on js object return | object is decoded`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val closure: (Coordinate?) -> Unit = mock()

        // define mock behavior
        whenever(htmlResources.getCenterJSSnippet()).thenReturn("JS-Snippet")
        doAnswer { invocation ->
            val closureMock = invocation.arguments[1] as (String) -> Unit
            closureMock("")
        }.whenever(viewContext).evaluateScript(eq("JS-Snippet"), any())

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.getMapCenter(closure)

        // check assertions
        verify(htmlResources, times(1)).decodeJavaScriptObject(any(), eq(Coordinate::class.java))
    }

    @Test
    fun `addMarker | request js-snippet`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val marker = Marker(
            uuid = UUID.randomUUID(),
            coordinate = Coordinate(
                lat = 2.0,
                lng = 5.0
            ),
            icon = WindyIcon(
                icon = Icon(url = "http://www.my.test.image.png")
            )
        )

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.addMarker(context, marker)

        // check assertions
        verify(htmlResources, times(1)).addMarkerJSSnippet(eq(context), eq(marker))
    }

    @Test
    fun `addMarker | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val marker = Marker(
            uuid = UUID.randomUUID(),
            coordinate = Coordinate(
                lat = 2.0,
                lng = 5.0
            ),
            icon = WindyIcon(
                icon = Icon(url = "http://www.my.test.image.png")
            )
        )

        // define mock behavior
        whenever(htmlResources.addMarkerJSSnippet(eq(context), eq(marker))).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.addMarker(context, marker)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), eq(null))
    }

    @Test
    fun `removeMarker | request js-snippet`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val uuid = UUID.randomUUID()

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.removeMarker(uuid)

        // check assertions
        verify(htmlResources, times(1)).removeMarkerJSSnippet(eq(uuid))
    }

    @Test
    fun `removeMarker | evaluates script`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val uuid = UUID.randomUUID()

        // define mock behavior
        whenever(htmlResources.removeMarkerJSSnippet(eq(uuid))).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        viewModel.removeMarker(uuid)

        // check assertions
        verify(viewContext, times(1)).evaluateScript(eq("JS-Snippet"), eq(null))
    }

    @Test
    fun `handleEvent | on js object return | object is decoded`() {

        // define test data
        val options = WindyInitOptions(
            key = "My-API-Key",
            verbose = true,
            lat = 53.0,
            lon = 18.5,
            zoom = 10
        )
        val event = "{}"

        // define mock behavior
        whenever(htmlResources.getCenterJSSnippet()).thenReturn("JS-Snippet")

        // init ViewModel
        val viewModel = (WindyMapViewViewModel(viewContext, htmlResources, options))

        // trigger action
        //language=JSON
        viewModel.handleEvent("{\n  \"name\" : \"markerclick\",\n  \"options\" : {\n    \"bounds\": {\n      \"_northEast\": {\n        \"lat\": 52,\n        \"lng\": 8\n      },\n      \"_southWest\": {\n        \"lat\": 52,\n        \"lng\": 8\n      }\n    },\n    \"uuid\": \"3aa8df6e-3071-42d1-a1d3-3cf47c2dce30\"\n  }\n}")
        viewModel.handleEvent(event)

        // check assertions
        verify(htmlResources, times(1)).decodeJavaScriptObject(eq(event), eq(WindyEventContent::class.java))
    }
}