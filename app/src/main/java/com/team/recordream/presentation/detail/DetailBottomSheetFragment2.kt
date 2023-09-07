package com.team.recordream.presentation.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.team.recordream.R
import com.team.recordream.databinding.FragmentDetailBinding
import com.team.recordream.presentation.common.model.PlayButtonState
import com.team.recordream.presentation.detail.adapter.ContentAdapter
import com.team.recordream.presentation.detail.adapter.GenreTagAdapter
import com.team.recordream.util.recorder.Recorder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailBottomSheetFragment2 private constructor(
    private val recordId: String,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding ?: error(R.string.error_basefragment)
    private val documentViewModel: DetailViewModel by viewModels()
    private val contentAdapter: ContentAdapter by lazy { ContentAdapter(documentViewModel::updateRecorderState) }
    private val genreTagAdapter: GenreTagAdapter by lazy { GenreTagAdapter() }
    private val recorder: Recorder by lazy { Recorder(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        collectState()
        attachAdapter()
        setEventOnClick()

        dialog?.let {
            val behavior =
                BottomSheetBehavior.from(it.findViewById(com.google.android.material.R.id.design_bottom_sheet))
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isHideable = true
            it.setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            this.setOnShowListener { dialog ->
                val bottomSheetDialog =
                    (dialog as BottomSheetDialog).also { setCanceledOnTouchOutside(false) }

                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    ?.let {
                        val behaviour = BottomSheetBehavior.from(it)
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        setupFullHeight(it)
                    }
            }
        }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun setupBinding() {
        binding.viewModel = documentViewModel
        binding.lifecycleOwner = this
    }

    private fun collectState() {
        collectWithLifecycle(documentViewModel.content) { contents ->
            contentAdapter.submitList(contents)
        }

        collectWithLifecycle(documentViewModel.tags) { genre ->
            genreTagAdapter.submitList(genre)
        }

        collectWithLifecycle(documentViewModel.isRemoved) { isRemoved ->
            if (isRemoved) dismiss()
        }

        collectWithLifecycle(documentViewModel.recorderState) { recorderState ->
            when (recorderState) {
                PlayButtonState.RECORDER_STOP -> handleRecorderPlayState()
                PlayButtonState.RECORDER_PLAY -> handleRecorderStopState()
            }
        }

        collectWithLifecycle(documentViewModel.progressRate) { progressRate ->
            // TODO: 아이템 뷰 -> 프래그먼트
            // TODO: 액티비티 -> 바텀시트 프래그먼트
            // TODO: 가끔 나의꿈기록 늦게 업데이트 됨. 프래그먼트로 교체하면 해결
        }
    }

    private fun handleRecorderStopState() {
        recorder.stopPlaying()
    }

    private fun handleRecorderPlayState() {
        val file = documentViewModel.recordingFilePath ?: throw IllegalArgumentException()

        recorder.apply {
            documentViewModel.updateRunningTime(getDuration(file))
            startPlaying(file)
        }
    }

    private fun attachAdapter() {
        binding.rvDocumentChip.adapter = genreTagAdapter
        binding.rvDocumentChip.setHasFixedSize(true)
        binding.vpDocumentContent.adapter = contentAdapter
        TabLayoutMediator(binding.tlDocument, binding.vpDocumentContent) { _, _ -> }.attach()
    }

    private fun setEventOnClick() {
        binding.ivDocumentMore.setOnClickListener { showBottomSheet() }
        binding.ivDocumentClose.setOnClickListener { dismiss() }
    }

    private fun showBottomSheet() {
        val detailBottomSheetFragment = DetailBottomSheetFragment()
        detailBottomSheetFragment.show(childFragmentManager, detailBottomSheetFragment.tag)
    }

    private fun setupView() {
        documentViewModel.updateDetailRecord(recordId)
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    private inline fun <T> collectWithLifecycle(
        flow: Flow<T>,
        crossinline action: (T) -> Unit,
    ) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest { value ->
                    action(value)
                }
            }
        }
    }

    companion object {
        fun getInstance(id: String): DetailBottomSheetFragment2 = DetailBottomSheetFragment2(id)
    }
}
