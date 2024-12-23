package com.example.alenfishempire.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alenfishempire.databinding.ActivityMainBinding

class MainActivity :  AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        binding.newCalculationCard.setOnClickListener {
            val intent = Intent(this, NewCalculationActivity::class.java)
            startActivity(intent)
        }


    }
}