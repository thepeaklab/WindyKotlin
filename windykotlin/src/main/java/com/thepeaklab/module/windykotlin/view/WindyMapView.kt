package com.thepeaklab.module.windykotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.thepeaklab.module.windykotlin.R
import com.thepeaklab.module.windykotlin.core.WindyHTMLResources
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.WindyEventContent
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import com.thepeaklab.module.windykotlin.databinding.WindyMapViewBinding
import com.thepeaklab.module.windykotlin.viewmodel.WindyMapViewViewModel
import com.thepeaklab.module.windykotlin.viewmodel.WindyMapViewViewModelFactory
import java.util.UUID

/**
 * WindyMapView
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-19.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

class WindyMapView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs),
    WindyMapViewContext, WebAppInterface {

    constructor(
        context: Context,
        options: WindyInitOptions,
        eventHandler: WindyEventHandler? = null
    ) : this(context) {
        this.options = options
        viewModel?.setOptions(options)
        eventHandler?.let { viewModel?.eventHandler = it }
    }

    var isWindyLogoVisible: Boolean = true
        private set(value) {
            viewModel?.updateWindyLogoVisibility(value)
            field = value
        }

    internal var viewModel: WindyMapViewViewModel? = null
    private var binding: WindyMapViewBinding? = null
    private var options: WindyInitOptions? = null
    private var eventHandler: WindyEventHandler? = null

    init {

        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.windy_map_view, this, true)
        } else {

            viewModel = ViewModelProviders.of(
                context as AppCompatActivity, WindyMapViewViewModelFactory(
                    this,
                    WindyHTMLResources,
                    options
                )
            ).get(WindyMapViewViewModel::class.java)

            // get option from attribute values
            if (options == null) {
                val a = context.obtainStyledAttributes(attrs, R.styleable.WindyMapView)
                viewModel?.setOptions(a)
            }

            // register eventhandler
            eventHandler?.let {
                viewModel?.eventHandler = it
            }

            // inflate view
            binding = DataBindingUtil.inflate<WindyMapViewBinding>(
                    LayoutInflater.from(context),
                    R.layout.windy_map_view,
                    this,
                    false
            )

            // uncomment the next line for debugging
            WebView.setWebContentsDebuggingEnabled(true)

            // enable javascript and add bridge interface
            binding?.windyMap?.let {
                it.settings.apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    this.javaScriptEnabled = true
                }
                it.addJavascriptInterface(
                        this, "JSBridge"
                )
                it.settings.domStorageEnabled = true

                // set webview client
                it.webViewClient = object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        viewModel?.updateWindyLogoVisibility(isWindyLogoVisible)
                        super.onPageFinished(view, url)
                    }
                }
            }

            // add inflated view and set license notes
            binding?.let {
                it.viewModel = viewModel

                addLicenseNotes()
                addView(it.root)
            }

            // wait until rendering is finished
            this.setOnRenderingCompleteListener {

                // init map
                viewModel?.initializeMap()

            }
        }
    }

    /**
     * init map and load content
     *
     */
    override fun initMap(content: String) {
        binding?.windyMap?.loadDataWithBaseURL("", content, "text/html", "UTF-8", null)
    }

    /**
     * set options for Windy map
     *
     */
    fun setOptions(apiKey: String, lat: Double?, lng: Double?, zoom: Int?) {

        val options = WindyInitOptions(
            key = apiKey,
            lat = lat,
            lon = lng,
            zoom = zoom
        )

        setOptions(options)
    }

    /**
     * set options for Windy map
     *
     */
    fun setOptions(options: WindyInitOptions) {

        this.options = options
        viewModel?.setOptions(options)
    }

    /**
     * set eventhandler for events in windy map
     *
     */
    fun setEventHandler(eventHandler: WindyEventHandler) {
        this.eventHandler = eventHandler
        viewModel?.eventHandler = eventHandler
    }

    /**
     * pan to position
     *
     */
    fun panTo(coordinate: Coordinate, options: WindyZoomPanOptions? = null) =
        viewModel?.panTo(coordinate, options)

    /**
     * set zoom level
     *
     */
    fun setZoom(zoomLevel: Int, options: WindyZoomPanOptions? = null) =
        viewModel?.setZoom(zoomLevel, options)

    /**
     * configure map to show all given coordinates
     *
     */
    fun fitBounds(coordinates: List<Coordinate>) = viewModel?.fitBounds(coordinates)

    /**
     * get center coordinates of the map
     *
     */
    fun getMapCenter(closure: (Coordinate?) -> Unit) = viewModel?.getMapCenter(closure)

    /**
     * add marker to map
     *
     */
    fun addMarker(marker: Marker) = viewModel?.addMarker(context, marker)

    /**
     * remove all marker from map
     *
     */
    fun removeMarker(uuid: UUID) = viewModel?.removeMarker(uuid)

    /**
     * evaluate javascript
     *
     */
    override fun evaluateScript(script: String, closure: ((String) -> Unit)?) {
        binding?.windyMap?.evaluateJavascript(script) {
            closure?.invoke(it)
        }
    }

    /**
     * sets visibility of windy icon
     *
     */
    override fun setLogoVisibility(isVisible: Boolean) {
        isWindyLogoVisible = isVisible
    }

    @JavascriptInterface
    override fun postMessage(obj: String) {
        viewModel?.handleEvent(obj) // TODO created 15.09.2020: Falscher thread
    }

    private fun addLicenseNotes() {
        binding?.let {

            it.licenseRight.movementMethod = LinkMovementMethod.getInstance()
            it.licenseRight.autoLinkMask = 0
            it.licenseLeft.autoLinkMask = 0
            it.licenseLeft.movementMethod = LinkMovementMethod.getInstance()

            it.licenseLeft.text = fromHtml(context.getString(R.string.license_html_windy))
            it.licenseRight.text = fromHtml(context.getString(R.string.license_html_osm))
        }
    }

    private fun fromHtml(html: String?): Spanned {
        return when {
            html == null -> {
                // return an empty spannable if the html is null
                SpannableString("")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                // we are using this flag to give a consistent behaviour
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            }
            else -> {
                Html.fromHtml(html)
            }
        }
    }
}

interface WebAppInterface {

    fun postMessage(obj: String)
}

interface WindyEventHandler {

    fun onEvent(event: WindyEventContent)
}

/**
 * add viewtree observer to view to get notified if rendering is finished
 *
 */
fun View.setOnRenderingCompleteListener(onRenderingFinished: () -> Unit) {

    val vto = this.viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

        override fun onGlobalLayout() {

            this@setOnRenderingCompleteListener.viewTreeObserver.removeOnGlobalLayoutListener(this)
            onRenderingFinished()
        }
    })
}