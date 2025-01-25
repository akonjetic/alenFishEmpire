package com.example.alenfishempire.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.activity.fragment.EditFishFragment
import com.example.alenfishempire.activity.viewmodel.AdministrationViewModel
import com.example.alenfishempire.adapter.FishListAdapter
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.databinding.ActivityAdministrationBinding

class AdministrationActivity : AppCompatActivity(){

    private lateinit var binding : ActivityAdministrationBinding
    private val viewModel: AdministrationViewModel by viewModels()
    private val fishListAdapter by lazy { FishListAdapter(this, arrayListOf()) { fish -> openEditFishFragment(fish) }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdministrationBinding.inflate(layoutInflater)
        binding.recyclerViewFish.adapter = fishListAdapter
        binding.recyclerViewFish.layoutManager = LinearLayoutManager(this)
        val view = binding.root

        viewModel.fetchAllFish(this)
        viewModel.listOfAllFish.observe(this){
            fishListAdapter.updateData(it)
        }

        setContentView(view)

        binding.fabAddFish.setOnClickListener {
            val fragment = EditFishFragment.newInstance(null)
            fragment.show(this.supportFragmentManager, "EditFishFragment")
        }

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun openEditFishFragment(fish: Fish?) {
        val fragment = EditFishFragment.newInstance(fish)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }
}
