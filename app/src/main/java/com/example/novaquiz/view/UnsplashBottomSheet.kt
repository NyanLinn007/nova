package com.example.novaquiz.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.UnsplashAdapter
import com.example.novaquiz.databinding.FragmentUnsplashBinding
import com.example.novaquiz.api.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class UnsplashBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentUnsplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoAdapter: UnsplashAdapter

    private val CLIENT_ID = "eN3XunRd_cl-tYES_IJLhm5_pQMlGc3QxWWWgOSGLIE"  // Replace with your Unsplash API key

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUnsplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoAdapter = UnsplashAdapter()

        binding.recyclerView.apply {
            adapter = photoAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Fetch photos from Unsplash API
        fetchPhotos()
    }

    private fun fetchPhotos() {
        lifecycleScope.launch {
            try {
                val photos = RetrofitInstance.api.getPhotos(clientId = CLIENT_ID)
                photoAdapter.submitList(photos)
                Log.d("UnsplashBottomSheet", "Fetched ${photos.size} photos")
                if (photos.isNotEmpty()) {
                    Log.d("UnsplashBottomSheet", "First photo ID: ${photos[0].id}, URL: ${photos[0].urls.small}")
                }
            } catch (e: CancellationException) {
                // Coroutine cancelled, ignore
            } catch (e: Exception) {
                Log.e("UnsplashBottomSheet", "Error fetching photos: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to load photos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.setBackgroundResource(R.drawable.bg_bottom_sheet_rounded)

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isFitToContents = false
                behavior.expandedOffset = (resources.displayMetrics.heightPixels * 0.10).toInt()

                val params = sheet.layoutParams
                params.height = (resources.displayMetrics.heightPixels * 0.90).toInt()
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
