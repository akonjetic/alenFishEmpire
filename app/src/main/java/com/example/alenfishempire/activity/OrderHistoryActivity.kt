package com.example.alenfishempire.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.activity.ui.theme.AlenFishEmpireTheme
import com.example.alenfishempire.activity.viewmodel.NewCalculationViewModel
import com.example.alenfishempire.activity.viewmodel.OrderHistoryViewModel
import com.example.alenfishempire.adapter.OrderHistoryAdapter
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding
import com.example.alenfishempire.databinding.ActivityOrderHistoryBinding

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private val viewModel: OrderHistoryViewModel by viewModels()
    private val orderHistoryAdapter by lazy { OrderHistoryAdapter(this, arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        binding.ordersRecyclerView.adapter = orderHistoryAdapter
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        val view = binding.root

        viewModel.fetchAllOrdersWithDetails(this){ details ->
            orderHistoryAdapter.updateData(details as ArrayList<Map<String, Any>>)
        }

        setContentView(view)


    }
}