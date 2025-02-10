package com.example.alenfishempire.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.activity.viewmodel.StatsViewModel
import com.example.alenfishempire.adapter.FishListAdapter
import com.example.alenfishempire.adapter.FishStatsAdapter
import com.example.alenfishempire.database.entities.FishSalesStats
import com.example.alenfishempire.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val viewModel: StatsViewModel by viewModels()
    private val fishStatListAdapter by lazy { FishStatsAdapter(this, arrayListOf()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatsBinding.inflate(layoutInflater)
        binding.rvFishSalesStats.adapter = fishStatListAdapter
        binding.rvFishSalesStats.layoutManager = LinearLayoutManager(this)

        val view = binding.root

        viewModel.fetchAllFishSalesStats(this)
        viewModel.fetchTotalFreeFish(this)
        viewModel.fetchTotalSales(this)

        viewModel.totalSales.observe(this) {
            binding.tvTotalSales.text = it.toString()
        }

        viewModel.totalFreeFish.observe(this) {
            binding.tvTotalFreeFish.text = it.toString()
        }

        viewModel.listOfAllFishSalesStats.observe(this) {
                fishStatListAdapter.updateData(it as ArrayList<FishSalesStats>)
        }

        setContentView(view)
    }
}