package dev.jishin.android.weatherapp.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.jishin.android.weatherapp.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val mainVM: MainVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
