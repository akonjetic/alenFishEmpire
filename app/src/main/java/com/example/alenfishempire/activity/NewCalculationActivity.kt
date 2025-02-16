package com.example.alenfishempire.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.viewmodel.NewCalculationViewModel
import com.example.alenfishempire.adapter.FishOrderAdapter
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.FishOrderItem
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

const val EXTRA_IS_READ_ONLY = "READ_ONLY"
const val EXTRA_ORDER = "ORDER"

class NewCalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCalculationBinding
    private val viewModel: NewCalculationViewModel by viewModels()
    private lateinit var adapter: FishOrderAdapter
    private val orderList = mutableListOf<FishOrderItem>()
    private var fishPriceMap = mutableMapOf<String, Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FishOrderAdapter(orderList, emptyList(), fishPriceMap, { position -> removeOrderRow(position) }, { calculateGrandTotal() })
        binding.rvFishOrders.layoutManager = LinearLayoutManager(this)
        binding.rvFishOrders.adapter = adapter

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        viewModel.fetchAllFish(this)
        viewModel.listOfAllFish.observe(this) { fishList ->
            val fishNames = fishList.map { it.name }
            fishPriceMap = fishList.associate { it.name to it.price }.toMutableMap()

            adapter = FishOrderAdapter(orderList, fishNames, fishPriceMap, { position -> removeOrderRow(position) }, { calculateGrandTotal() })
            binding.rvFishOrders.adapter = adapter
        }

        binding.addRowButton.setOnClickListener { addNewRow() }
        binding.sumUpOrder.setOnClickListener { saveOrder() }

        addNewRow() // Dodaj prvi red pri pokretanju
    }

    private fun addNewRow() {
        orderList.add(FishOrderItem(fishType = fishPriceMap.keys.firstOrNull() ?: "", quantity = 1, price = fishPriceMap.values.firstOrNull() ?: 0f))
        adapter.notifyItemInserted(orderList.size - 1)
    }

    private fun removeOrderRow(position: Int) {
        orderList.removeAt(position)
        adapter.notifyItemRemoved(position)
        calculateGrandTotal()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun calculateGrandTotal() {
        var grandTotalQuantity = 0
        var grandTotalPrice = 0.0

        for (item in orderList) {
            if (!item.isFree) {
                grandTotalQuantity += item.quantity
                grandTotalPrice += item.quantity * item.price
            }
        }

        // **Ažuriraj prikaz ukupne količine i cijene**
        binding.totalQuantity.text = grandTotalQuantity.toString()
        binding.totalAmount.text = "€${String.format("%.2f", grandTotalPrice)}"
    }


    private fun saveOrder() {
        val fishOrderList = mutableListOf<FishOrder>()
        val currentDate = Date()

        for (item in orderList) {
            val selectedFish = viewModel.listOfAllFish.value?.find { it.name == item.fishType }
            if (selectedFish != null && item.quantity > 0) {
                val fishOrder = FishOrder(
                    id = 0,
                    fishId = selectedFish.id,
                    quantity = item.quantity,
                    isFree = item.isFree
                )
                viewModel.saveNewFishOrder(this, fishOrder) { fishOrderId ->
                    fishOrderList.add(fishOrder.copy(id = fishOrderId))

                    if (fishOrderList.size == orderList.size) {
                        val fishOrderIds = fishOrderList.map { it.id }
                        val order = Order(id = 0, date = currentDate, fishOrderId = fishOrderIds, discount = null)
                        viewModel.saveNewOrder(this, order) { orderId -> order.id = orderId }
                        showExportDialog(order)
                    }
                }
            }
        }
    }

    private fun showExportDialog(order: Order) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_export, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnSavePdf = dialogView.findViewById<Button>(R.id.btnSavePdf)
        val btnNoThanks = dialogView.findViewById<Button>(R.id.btnNoThanks)

        btnSavePdf.setOnClickListener {
            exportToPDF(order)
            dialog.dismiss()
        }

        btnNoThanks.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    @SuppressLint("SimpleDateFormat", "DefaultLocale")
    private fun exportToPDF(order: Order) {
        val fileName = "Order_${order.id}.pdf"
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, fileName)

        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss") // Koristi "HH" za 24-satni format

        val formattedDate = dateFormat.format(order.date)

        document.add(Paragraph("ORDER DETAILS").setBold())
        document.add(Paragraph("Order ID: ${order.id}"))
        document.add(Paragraph("Date: $formattedDate"))

        document.add(Paragraph())
        document.add(Paragraph())

        val table = Table(floatArrayOf(100f, 100f, 100f, 100f, 100f))
        table.addHeaderCell("Fish Type").setBold()
        table.addHeaderCell("Quantity").setBold()
        table.addHeaderCell("Item Price (€)").setBold()
        table.addHeaderCell("Free").setBold()
        table.addHeaderCell("Total (€)").setBold()

        viewModel.fetchFishOrderDetails(this, order.fishOrderId) { detailsList ->
            detailsList.forEach { details ->
                val fishOrder = details["fishOrder"] as FishOrder
                val fish = details["fish"] as Fish

                table.addCell(fish.name).setTextAlignment(TextAlignment.CENTER)
                table.addCell(fishOrder.quantity.toString()).setTextAlignment(TextAlignment.CENTER)
                table.addCell(fish.price.toString()).setTextAlignment(TextAlignment.CENTER)
                table.addCell(if (fishOrder.isFree) "Yes" else "No")
                    .setTextAlignment(TextAlignment.CENTER)
                table.addCell(if (fishOrder.isFree) "/" else (fish.price * fishOrder.quantity.toFloat()).toString())
                    .setTextAlignment(TextAlignment.CENTER)
            }

            val totalQuantity = order.fishOrderId.sumOf { fishOrderId ->
                val details =
                    detailsList.find { it["fishOrder"]?.let { fishOrder -> (fishOrder as FishOrder).id == fishOrderId } == true }
                val fishOrder = details?.get("fishOrder") as? FishOrder
                fishOrder?.quantity ?: 0
            }

            val totalPrice = order.fishOrderId.sumOf { fishOrderId ->
                val details =
                    detailsList.find { it["fishOrder"]?.let { fishOrder -> (fishOrder as FishOrder).id == fishOrderId } == true }
                val fishOrder = details?.get("fishOrder") as? FishOrder
                val fish = details?.get("fish") as? Fish
                if (fishOrder?.isFree == true) {
                    0.0
                } else {
                    (fish?.price?.times(fishOrder?.quantity?.toFloat() ?: 0f) ?: 0.0).toDouble()
                }
            }

            document.add(table)
            document.add(Paragraph())
            document.add(Paragraph())
            document.add(Paragraph("Total Quantity: $totalQuantity").setBold())
            document.add(Paragraph("Total Price (€): ${String.format("%.2f", totalPrice)}").setBold())
            document.close()

            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                null,
                null
            )

            Toast.makeText(this, "Exported to $fileName", Toast.LENGTH_SHORT).show()
        }


        fun Int.dpToPx(): Int {
            return (this * Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}
