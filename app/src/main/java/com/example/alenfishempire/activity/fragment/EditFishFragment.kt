package com.example.alenfishempire.activity.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.viewmodel.AdministrationViewModel
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishDTO
import com.example.alenfishempire.databinding.FragmentEditFishBinding

class EditFishFragment : DialogFragment() {

    private val viewModel: AdministrationViewModel by activityViewModels()
    private var _binding: FragmentEditFishBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(fish: Fish?): EditFishFragment {
            val fragment = EditFishFragment()
            val args = Bundle()
            args.putSerializable("fish", fish)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentEditFishBinding.inflate(layoutInflater)

        val fish = arguments?.getSerializable("fish") as? Fish
        fish?.let {
            binding.etFishName.setText(it.name)
            binding.etFishPrice.setText("€${it.price}")

            binding.btnSaveFish.setOnClickListener {
                val name = binding.etFishName.text.toString()
                val price = binding.etFishPrice.text.toString()
                    .replace("€", "")
                    .replace("[^0-9.]".toRegex(), "")
                    .toFloatOrNull()


                if (name.isNotEmpty() && price != null) {
                    val updatedFish = fish?.copy(name = name, price = price)
                    updatedFish?.let { fishToUpdate ->
                        viewModel.updateFish(requireContext(), fishToUpdate)
                    }
                    viewModel.fetchAllFish(requireContext())
                    dismiss()
                } else {
                    binding.etFishName.error = "Name cannot be empty"
                    binding.etFishPrice.error = "Price must be a valid number"
                }
            }

        } ?: run {
            binding.etFishName.text.clear()
            binding.etFishPrice.text.clear()

            binding.btnSaveFish.setOnClickListener {
                val name = binding.etFishName.text.toString()
                val price = binding.etFishPrice.text.toString().toFloatOrNull()

                if (name.isNotEmpty() && price != null) {
                    val newFish = Fish(name = name, price = price, id = 0)
                    newFish.let { nf ->
                        viewModel.saveNewFish(requireContext(), nf)
                    }
                    viewModel.fetchAllFish(requireContext())
                    dismiss()
                } else {
                    binding.etFishName.error = "Name cannot be empty"
                    binding.etFishPrice.error = "Price must be a valid number"
                }
            }
        }



        binding.btnDeleteFish.setOnClickListener {
            fish?.let { fishToDelete ->
                viewModel.deleteFish(requireContext(), fishToDelete)
            }
            viewModel.fetchAllFish(requireContext())
            dismiss()
        }


        val dialog = Dialog(requireContext(), R.style.CustomDialogTheme)
        dialog.setContentView(binding.root)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Čišćenje bindinga
    }
}
