package de.rustyhamsterr.windydemo.ui.other

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thepeaklab.module.windykotlin.sample.R

class OtherFragment : Fragment() {

    companion object {
        fun newInstance() = OtherFragment()
    }

    private lateinit var viewModel: OtherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.other_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OtherViewModel::class.java)
        // TODO: Use the ViewModel
    }

}