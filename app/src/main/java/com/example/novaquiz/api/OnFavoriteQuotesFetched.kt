package com.example.novaquiz.api



import com.example.novaquiz.data.Quote

interface OnFavoriteQuotesFetched {
    fun onFavoritesFetched(favoriteQuotes: List<Quote>)
}