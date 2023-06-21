package com.recodream_aos.recordream.presentation.search

import android.os.Bundle
import com.recodream_aos.recordream.R
import com.recodream_aos.recordream.base.BindingActivity
import com.recodream_aos.recordream.databinding.ActivitySearchBinding
import com.recodream_aos.recordream.presentation.search.adapter.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        attachAdapter()
        setClickEvent()
    }

    private fun attachAdapter() {
        binding.rvSearchResult.adapter = searchAdapter
    }

    private fun setClickEvent() {
        binding.ivSearchBackBtn.setOnClickListener { finish() }
    }
}
