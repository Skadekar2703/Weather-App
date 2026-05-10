package com.tommy.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.tommy.weatherapp.databinding.ActivityMainBinding
import com.tommy.weatherapp.ui.fragment.SearchFragment
import com.tommy.weatherapp.ui.fragment.WeatherFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val appContainer by lazy { WeatherAppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainer) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WeatherFragment())
            }
        }
    }

    fun openSearch() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, SearchFragment())
            addToBackStack(SearchFragment::class.java.simpleName)
        }
    }
}
