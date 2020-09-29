package de.rustyhamsterr.windydemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.thepeaklab.module.windykotlin.sample.R
import de.rustyhamsterr.windydemo.ui.main.MainFragment
import de.rustyhamsterr.windydemo.ui.other.OtherFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }


        findViewById<Button>(R.id.nav_btn_map).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        findViewById<Button>(R.id.nav_btn_no_map).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, OtherFragment.newInstance())
                .commitNow()
        }
    }
}