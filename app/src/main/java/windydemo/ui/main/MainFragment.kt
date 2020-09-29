package de.rustyhamsterr.windydemo.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.sample.R
import com.thepeaklab.module.windykotlin.sample.databinding.MainFragmentBinding
import com.thepeaklab.module.windykotlin.view.WindyMapView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var windyMapView: WindyMapView? = null

    @SuppressLint("JavascriptInterface")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("MainFragment", "onCreateView")

        val binding: MainFragmentBinding =
                DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        Log.d("MainFragment", "onCreateView ${binding.windyMapView}")

        windyMapView = binding.windyMapView

        // uncomment the next line for debugging
        WebView.setWebContentsDebuggingEnabled(true)


        // set event Handler for events thrown by windy
        binding.windyMapView.setEventHandler(viewModel)
        WebView(context?.applicationContext).clearCache(true)

        binding.windyMapView.setOptions(
                WindyInitOptions(
                        "lJFzPgMWxQgcs9P9PgRd99xsX8pdbWCO",
                        true,
                        53.528740,
                        8.452565,
                        5
                )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d("MainFragment", "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onResume() {
        Log.d("MainFragment", "onResume")
        super.onResume()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        Log.d("MainFragment", "onAttachFragment")
        super.onAttachFragment(childFragment)
    }

    override fun onDestroy() {
        Log.d("MainFragment", "onDestroy")
        WebView(context).clearCache(true)
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("MainFragment", "onDetach")
        windyMapView = null
        super.onDetach()
    }
}