package com.debanshu777.painter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.debanshu777.painter.R

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelProviderFactory = MainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainActivityViewModel::class.java)

        setContentView(R.layout.activity_main)


    }
}