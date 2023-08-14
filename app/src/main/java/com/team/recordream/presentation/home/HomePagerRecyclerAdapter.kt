package com.team.recordream.presentation.home // ktlint-disable filename

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.team.recordream.R
import com.team.recordream.data.entity.remote.response.ResponseHome
import com.team.recordream.databinding.HomeCardItemBinding
import com.team.recordream.util.RecordreamMapping

class HomeViewPagerAdapter(private val itemClick: (ResponseHome.Record) -> (Unit)) :
    RecyclerView.Adapter<HomeViewPagerAdapter.PagerViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, diffResult)

    private var homeCardList = mutableListOf<ResponseHome>()

    fun updateList(list: List<ResponseHome.Record>) {
//        Log.i("list.size", homeCardList.size.toString())
//        homeCardList = list
//        this.notifyDataSetChanged
        asyncDiffer.submitList(list)
    }

    class PagerViewHolder(
        private val binding: HomeCardItemBinding,
        private val itemClick: (ResponseHome.Record) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ResponseHome.Record) {
            binding.root.setOnClickListener {
                itemClick(data)
            }
            binding.tvHomeDate.text = data.date
            binding.tvHomeCardTitle.text = data.title
            binding.clHomeCard.setBackgroundResource(checkEmotionBackground(data.emotion))
            binding.ivHomeEmoticon.setImageResource(checkEmotionIcon(data.emotion))
            binding.tvHomeContent.text = data.content
//            if (data.content == null && data.voice !== null) {
//                binding.tvHomeContent.visibility = View.INVISIBLE
//                binding.tvHomeMic.visibility = View.VISIBLE
//                binding.ivHomeMic.visibility = View.VISIBLE
//            } else {
//                binding.tvHomeContent.visibility = View.VISIBLE
//                binding.tvHomeMic.visibility = View.INVISIBLE
//                binding.ivHomeMic.visibility = View.INVISIBLE
//            }
            applyData(data)
        }

        val recorDreamMapping = RecordreamMapping()
        private fun applyData(response: ResponseHome.Record) {
            val applyGenre = response.let { recorDreamMapping.genreMapping(it.genre) }

            binding.tvHomeCardTitle.text = response.title
            binding.tvHomeDate.text = response.date
            if (response.genre.size == 1) {
                binding.tvHomeGenre1.text = "#${applyGenre[0]}"
            }
            if (response.genre.size == 2) {
                binding.tvHomeGenre1.text = "#${applyGenre[0]}"
                binding.tvHomeGenre2.text = "#${applyGenre[1]}"
            }
            if (response.genre.size == 3) {
                binding.tvHomeGenre1.text = "#${applyGenre[0]}"
                binding.tvHomeGenre2.text = "#${applyGenre[1]}"
                binding.tvHomeGenre3.text = "#${applyGenre[2]}"
            }
            when (response.genre.size) {
                1 -> {
                    binding.tvHomeGenre1.visibility = View.VISIBLE
                    binding.tvHomeGenre2.visibility = View.INVISIBLE
                    binding.tvHomeGenre3.visibility = View.INVISIBLE
                }

                2 -> {
                    binding.tvHomeGenre1.visibility = View.VISIBLE
                    binding.tvHomeGenre2.visibility = View.VISIBLE
                    binding.tvHomeGenre3.visibility = View.INVISIBLE
                }

                3 -> {
                    binding.tvHomeGenre1.visibility = View.VISIBLE
                    binding.tvHomeGenre2.visibility = View.VISIBLE
                    binding.tvHomeGenre3.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding: HomeCardItemBinding = HomeCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagerViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.onBind(asyncDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    companion object {
        private val diffResult = object : DiffUtil.ItemCallback<ResponseHome.Record>() {
            override fun areItemsTheSame(
                oldItem: ResponseHome.Record,
                newItem: ResponseHome.Record
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ResponseHome.Record,
                newItem: ResponseHome.Record
            ): Boolean {
                return oldItem == newItem
            }
        }

        fun checkEmotionIcon(color: Int) = when (color) {
            1 -> R.drawable.feeling_l_joy
            2 -> R.drawable.feeling_l_sad
            3 -> R.drawable.feeling_l_scary
            4 -> R.drawable.feeling_l_strange
            5 -> R.drawable.feeling_l_shy
            else -> R.drawable.feeling_l_blank
        }

        fun checkEmotionBackground(color: Int) = when (color) {
            1 -> R.drawable.card_m_yellow
            2 -> R.drawable.card_m_blue
            3 -> R.drawable.card_m_red
            4 -> R.drawable.card_m_purple
            5 -> R.drawable.card_m_pink
            else -> R.drawable.card_m_white
        }
    }
}
