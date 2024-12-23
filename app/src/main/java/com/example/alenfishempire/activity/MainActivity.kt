package com.example.alenfishempire.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alenfishempire.databinding.ActivityMainBinding
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding

class MainActivity :  AppCompatActivity() {

    private lateinit var binding: ActivityNewCalculationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCalculationBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)



    }
}