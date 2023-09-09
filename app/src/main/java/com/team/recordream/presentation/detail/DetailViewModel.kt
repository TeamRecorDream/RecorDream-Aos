package com.team.recordream.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.recordream.R
import com.team.recordream.domain.repository.DocumentRepository
import com.team.recordream.presentation.common.model.PlayButtonState
import com.team.recordream.presentation.detail.model.Content
import com.team.recordream.presentation.record.uistate.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
) : ViewModel() {
    var recordId: String = ""
        private set

    var recordingFilePath: String? = ""
        private set

    private val _background: MutableStateFlow<Int> = MutableStateFlow(0)
    val background: StateFlow<Int> get() = _background

    private val _icon: MutableStateFlow<Int> = MutableStateFlow(0)
    val icon: StateFlow<Int> get() = _icon

    private val _date: MutableStateFlow<String> = MutableStateFlow("")
    val date: StateFlow<String> get() = _date

    private val _title: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _tags: MutableStateFlow<List<Genre>> = MutableStateFlow(listOf())
    val tags: StateFlow<List<Genre>> get() = _tags

    private val _isRemoved: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRemoved: StateFlow<Boolean> get() = _isRemoved

    private val _content: MutableStateFlow<List<Content>> = MutableStateFlow(contentDefault)
    val content: StateFlow<List<Content>> get() = _content

    private val _recorderState: MutableStateFlow<PlayButtonState> =
        MutableStateFlow(PlayButtonState.RECORDER_PLAY)
    val recorderState: StateFlow<PlayButtonState> get() = _recorderState

    private val _progressRate: MutableStateFlow<Int> = MutableStateFlow(0)
    val progressRate: StateFlow<Int> get() = _progressRate

    private var timer: Timer? = null
    private var runningTime: Int = 0

    fun updateDetailRecord(id: String) {
        recordId = id
        viewModelScope.launch {
            documentRepository.getDetailRecord(recordId)
                .onSuccess {
                    _background.value = Emotion.findBackgroundById(it.emotion ?: 0)
                    _icon.value = Emotion.findIconById(it.emotion ?: 0)
                    _date.value = it.date.replace(Regex("[()]"), "")
                    _title.value = it.title
                    findTagByGenreId(it.genre)
                    _content.value = listOf(
                        Content(
                            CONTENT_CATEGORY_DREAM_RECORD,
                            it.content ?: BLANK,
                            it.voice != null,
                            _recorderState.value,
                        ),
                        Content(
                            CONTENT_CATEGORY_NOTE,
                            it.note ?: BLANK,
                            it.voice != null,
                            _recorderState.value,
                        ),
                    )
                    if (it.voice != null) recordingFilePath = it.voice.url
                }
                .onFailure {
                    Log.d("123123-DetailViewModel", it.message.toString())
                }
        }
    }

    fun updateRecorderState(selectedState: PlayButtonState) {
        when (selectedState) {
            PlayButtonState.RECORDER_PLAY -> {
                _recorderState.value = PlayButtonState.RECORDER_STOP
                _content.value =
                    content.value.map { it.copy(recorderState = PlayButtonState.RECORDER_STOP) }
                initProgressBar()
            }

            PlayButtonState.RECORDER_STOP -> {
                _recorderState.value = PlayButtonState.RECORDER_PLAY
                _content.value =
                    content.value.map { it.copy(recorderState = PlayButtonState.RECORDER_PLAY) }
            }
        }
    }

    fun updateRemovedRecord() {
        _isRemoved.value = true
        removeDetailRecord()
    }

    fun updateRunningTime(duration: Int) {
        runningTime = duration
    }

    private fun initProgressBar() {
//        timer = timer(period = runningTime.convertMilliseconds() / HUNDRED_PERCENT) {
//            if (progressTime > HUNDRED_PERCENT) {
//                cancel()
//            }
//            ++progressTime
//        }
    }

    private fun Int.convertMilliseconds(): Long = this * ONE_SECOND_LONG

    private fun removeDetailRecord() {
        viewModelScope.launch {
            documentRepository.deleteDetailRecord(recordId)
                .onSuccess {
                }
                .onFailure {
                }
        }
    }

    private fun findTagByGenreId(genre: List<Int>) {
        when (genre.contains(0)) {
            true -> _tags.value = listOf(Genre.getValue(-1))
            false -> {
                _tags.value = genre.map {
                    Genre.getValue(it)
                }
            }
        }
    }

    private enum class Emotion {
        JOY, SAD, SCARY, STRANGE, SHY, ALL;

        companion object {
            fun findBackgroundById(id: Int): Int {
                return when (Emotion.getValue(id)) {
                    JOY -> R.drawable.background_yellow
                    SAD -> R.drawable.background_blue
                    SCARY -> R.drawable.background_red
                    STRANGE -> R.drawable.background_purple
                    SHY -> R.drawable.background_pink
                    ALL -> R.drawable.background_white
                }
            }

            fun findIconById(id: Int): Int {
                return when (Emotion.getValue(id)) {
                    JOY -> R.drawable.feeling_l_joy
                    SAD -> R.drawable.feeling_l_sad
                    SCARY -> R.drawable.feeling_l_scary
                    STRANGE -> R.drawable.feeling_l_strange
                    SHY -> R.drawable.feeling_l_shy
                    ALL -> R.drawable.feeling_l_blank
                }
            }

            fun getValue(emotionId: Int): Emotion = values().find { emotion ->
                emotion.ordinal + 1 == emotionId
            } ?: ALL
        }
    }

    companion object {
        const val CONTENT_CATEGORY_DREAM_RECORD = "나의 꿈 기록"
        private const val CONTENT_CATEGORY_NOTE = "노트"
        private const val BLANK = ""
        private const val HUNDRED_PERCENT = 100
        private const val ONE_SECOND_LONG: Long = 1000
        private val contentDefault = listOf(
            Content(CONTENT_CATEGORY_DREAM_RECORD, BLANK, false, PlayButtonState.RECORDER_STOP),
            Content(CONTENT_CATEGORY_NOTE, BLANK, false, PlayButtonState.RECORDER_STOP),
        )
    }
}
