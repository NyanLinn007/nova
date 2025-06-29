package com.example.novaquiz.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.UnsplashAdapter
import com.example.novaquiz.databinding.FragmentUnsplashBinding
import com.example.novaquiz.api.RetrofitInstance
import com.example.novaquiz.data.UnsplashSearchResponse
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
            layoutManager = GridLayoutManager(requireContext(), 2)

        }

        // Fetch photos from Unsplash API
        fetchPhotos()
        setupKeyboardDismissOnOutsideTouch()


        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchPhotos(query)
                } else {
                    fetchPhotos()
                }
                // Hide keyboard
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun fetchPhotos() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val photos = RetrofitInstance.api.getPhotos(clientId = CLIENT_ID)
                photoAdapter.submitList(photos)
                Log.d("UnsplashBottomSheet", "Fetched ${photos.size} photos")
            } catch (e: CancellationException) {
                // Ignore cancel
            } catch (e: Exception) {
                Log.e("UnsplashBottomSheet", "Error fetching photos: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to load photos", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }


    private fun searchPhotos(query: String) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response: UnsplashSearchResponse = RetrofitInstance.api.searchPhotos(
                    clientId = CLIENT_ID,
                    query = query
                )
                photoAdapter.submitList(response.results)
                Log.d("UnsplashBottomSheet", "Search results for '$query': ${response.results.size}")
            } catch (e: Exception) {
                Log.e("UnsplashBottomSheet", "Error searching photos: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to search photos", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
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

                // Prevent dragging the sheet itself
                behavior.isDraggable = false

                val params = sheet.layoutParams
                params.height = (resources.displayMetrics.heightPixels * 0.90).toInt()
                sheet.layoutParams = params
            }
        }

        // Allow dismiss when tapping outside
        dialog?.setCanceledOnTouchOutside(true)
    }


    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
    private fun setupKeyboardDismissOnOutsideTouch() {
        // Set touch listener on recyclerView to clear focus and hide keyboard on touch
        binding.recyclerView.setOnTouchListener { _, _ ->
            binding.searchEditText.clearFocus()
            hideKeyboard()
            false
        }

        // Also set touch listener on root layout, just in case
        binding.root.setOnTouchListener { _, _ ->
            binding.searchEditText.clearFocus()
            hideKeyboard()
            false
        }
    }


    override fun getTheme(): Int = R.style.FullScreenBottomSheetDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
