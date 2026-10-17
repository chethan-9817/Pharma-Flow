package com.example.ui.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object SoundManager {
    private const val TAG = "SoundManager"
    private var toneGenerator: ToneGenerator? = null

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val audioScope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            Log.w(TAG, "ToneGenerator not available on this device", e)
        }
    }

    fun toggleSound(): Boolean {
        val newState = !_isSoundEnabled.value
        _isSoundEnabled.value = newState
        return newState
    }

    fun playScratchSound() {
        if (!_isSoundEnabled.value) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
        } catch (_: Exception) {}
    }

    fun playRevealSound() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            try {
                // Celebration ascending arpeggio
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 70)
                delay(80)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_4, 70)
                delay(80)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_8, 70)
                delay(80)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
            } catch (_: Exception) {}
        }
    }

    fun playCheckoutSound() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            try {
                // Cash register chime
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 60)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 140)
            } catch (_: Exception) {}
        }
    }

    fun playHeartSound() {
        if (!_isSoundEnabled.value) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 40)
        } catch (_: Exception) {}
    }
}
