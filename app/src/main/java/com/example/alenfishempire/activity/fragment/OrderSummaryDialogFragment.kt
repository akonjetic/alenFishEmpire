package com.example.alenfishempire.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.databinding.FragmentOrderSummaryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderSummaryDialogFragment : DialogFragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_ORDER = "order"

        fun newInstance(order: OrderWithDetails): OrderSummaryDialogFragment {
            val fragment = OrderSummaryDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = arguments?.getSerializable(ARG_ORDER) as? OrderWithDetails

        order?.let {
            binding.orderId.text = "Order #${it.id}"
            binding.orderDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.date)
            binding.totalQuantity.text = "Total Quantity: ${it.totalQuantity}"
            binding.totalPrice.text = "Total Price: €${String.format("%.2f", it.totalPrice)}"

            displayFishDetails(it)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun displayFishDetails(order: OrderWithDetails) {
        binding.fishDetailsContainer.removeAllViews()

        order.fishOrderList.forEach { fish ->
            val textView = TextView(requireContext()).apply {
                text = "${fish.fishName}: ${fish.quantity} pcs - €${String.format("%.2f", fish.price)}"
                textSize = 14f
                setPadding(8, 4, 8, 4)
            }
            binding.fishDetailsContainer.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
