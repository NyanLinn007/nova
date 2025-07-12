package com.example.novaquiz.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.novaquiz.adapter.GeneralForyouAdapter
import com.example.novaquiz.adapter.GeneralPopularAdapter
import com.example.novaquiz.databinding.ActivityGeneralBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.api.OnFavoriteQuotesFetched
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Tasks

import com.example.novaquiz.data.Quote

class General(private val listener: OnFavoriteQuotesFetched) : BottomSheetDialogFragment() {

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

        binding.myFavorite.setOnClickListener {
            fetchUserFavorites()
        }
        binding.general.setOnClickListener {
            fetchQuotesFromGeneral()
        }

    }
    private fun fetchQuotesFromGeneral() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Quotes")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val quotes = result.documents.mapNotNull { doc ->
                    val quote = doc.toObject(com.example.novaquiz.data.Quote::class.java)
                    quote?.apply { quoteId = doc.id }
                }

                listener.onFavoritesFetched(quotes)  // Using same callback to send quotes
                Toast.makeText(requireContext(), "Go To General", Toast.LENGTH_SHORT).show()

                dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load quotes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fetchUserFavorites() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("Favorite")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val quoteIds = result.documents.mapNotNull { it.getString("quoteId") }

                if (quoteIds.isEmpty()) {
                    Toast.makeText(requireContext(), "No favorites found", Toast.LENGTH_SHORT).show()
                    listener.onFavoritesFetched(emptyList())
                    dismiss()
                    return@addOnSuccessListener
                }

                val tasks = quoteIds.map { id ->
                    db.collection("Quotes").document(id).get()
                }

                com.google.android.gms.tasks.Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { documents ->
                        val favoriteQuotes = documents.mapNotNull { doc ->
                            val quote = doc.toObject(Quote::class.java)
                            quote?.apply {
                                quoteId = doc.id
                                favorite = true  // ✅ Mark as favorite
                            }
                        }

                        listener.onFavoritesFetched(favoriteQuotes)
                        Toast.makeText(requireContext(), "Go To Favorite", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error loading quotes: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load favorites: ${e.message}", Toast.LENGTH_SHORT).show()
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
                behavior.expandedOffset = 0
                behavior.isDraggable = false
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