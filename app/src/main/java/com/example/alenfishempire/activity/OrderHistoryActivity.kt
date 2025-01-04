package com.example.alenfishempire.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.ui.theme.AlenFishEmpireTheme
import com.example.alenfishempire.activity.viewmodel.NewCalculationViewModel
import com.example.alenfishempire.activity.viewmodel.OrderHistoryViewModel
import com.example.alenfishempire.adapter.OrderHistoryAdapter
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding
import com.example.alenfishempire.databinding.ActivityOrderHistoryBinding

/*TODO da je defaultno postavljen trenutni mjesec i godina u filter*/

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

        applyFilters()
        setContentView(view)

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Filtriranje po mjesecu
        binding.monthFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = parent.getItemAtPosition(position).toString()
                viewModel.filterByMonth(getMonthNumber(selectedMonth)) // Convert to numeric month
                fetchFilteredAndSortedOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Filtriranje po godini
        binding.yearFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedYear = parent.getItemAtPosition(position).toString()
                viewModel.filterByYear(selectedYear)
                fetchFilteredAndSortedOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Sortiranje po opciji
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSortOption = parent.getItemAtPosition(position).toString()
                val sortQueryKey = getSortQueryKey(selectedSortOption) // Mapiranje na SQL ključeve
                viewModel.sortBy(sortQueryKey)
                fetchFilteredAndSortedOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getMonthNumber(monthName: String): String {
        // Mjeseci prema imenu, možeš dodati više ako treba
        val months = mapOf(
            "Siječanj" to "01",
            "Veljača" to "02",
            "Ožujak" to "03",
            "Travanj" to "04",
            "Svibanj" to "05",
            "Lipanj" to "06",
            "Srpanj" to "07",
            "Kolovoz" to "08",
            "Rujan" to "09",
            "Listopad" to "10",
            "Studeni" to "11",
            "Prosinac" to "12"
        )
        return months[monthName] ?: ""
    }

    private fun fetchFilteredAndSortedOrders() {
        viewModel.fetchFilteredAndSortedOrders(this) { data ->
            orderHistoryAdapter.updateData(data)
        }
    }

    private fun applyFilters() {
        fetchFilteredAndSortedOrders()
    }

    fun getSortQueryKey(selectedSortOption: String): String {
        return when (selectedSortOption) {
            "Datum (silazno)" -> "date_desc"
            "Datum (uzlazno)" -> "date_asc"
            "Količina ribe (silazno)" -> "quantity"
            "Cijena (silazno)" -> "price"
            else -> "date_desc"
        }
    }
}