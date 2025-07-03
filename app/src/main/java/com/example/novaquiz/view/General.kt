package com.example.novaquiz.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.novaquiz.adapter.GeneralForyouAdapter
import com.example.novaquiz.adapter.GeneralPopularAdapter
import com.example.novaquiz.databinding.ActivityGeneralBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class General : BottomSheetDialogFragment() {

    private var _binding: ActivityGeneralBinding? = null
    private val binding get() = _binding!!

    private lateinit var generalPopularAdapter: GeneralPopularAdapter
    private lateinit var generalForyouAdapter: GeneralForyouAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generalPopularAdapter = GeneralPopularAdapter()
        generalForyouAdapter = GeneralForyouAdapter()

        binding.rvgeneral.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvgeneral.adapter = generalPopularAdapter

        binding.rvforyou.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvforyou.adapter = generalForyouAdapter

        binding.btnclose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.setBackgroundResource(R.drawable.bg_bottom_sheet_rounded)

                val behavior = BottomSheetBehavior.from(sheet)

                // Set the BottomSheet to expanded state immediately
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                // This allows the sheet to expand fully (ignores fitToContents)
                behavior.isFitToContents = false

                // Set expanded offset to zero so it covers full screen height
                behavior.expandedOffset = 0

                // Optional: Disable dragging if you want to prevent user from swiping down
                behavior.isDraggable = false

                // Set the sheet layout height to match parent (full screen)
                val params = sheet.layoutParams
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                sheet.layoutParams = params
            }
        }
    }


    override fun getTheme(): Int = R.style.FullScreenBottomSheetDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
