package com.example.alenfishempire.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.activity.viewmodel.StatsViewModel
import com.example.alenfishempire.adapter.FishStatsAdapter
import com.example.alenfishempire.database.entities.FishSalesStats
import com.example.alenfishempire.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val viewModel: StatsViewModel by viewModels()
    private val fishStatListAdapter by lazy { FishStatsAdapter(this, arrayListOf()) }


    @SuppressLint("SetTextI18n")
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
            binding.tvTotalSales.text = "Total Sales: €$it"
        }

        viewModel.totalFreeFish.observe(this) {
            binding.tvTotalFreeFish.text = "Total Free Fish: $it"
        }

        viewModel.listOfAllFishSalesStats.observe(this) {
            fishStatListAdapter.updateData(it as ArrayList<FishSalesStats>)
        }

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setContentView(view)
    }
}