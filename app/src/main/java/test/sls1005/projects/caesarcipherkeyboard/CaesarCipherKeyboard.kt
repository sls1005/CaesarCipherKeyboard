package test.sls1005.projects.caesarcipherkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.FLAG_SOFT_KEYBOARD
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast

class CaesarCipherKeyboard : InputMethodService() {
    var cipherDiskOffset = 0
    var capitalizationOffset = -32
    var userDefinedAdditionalOffset = 0
    var upperKeyPressed = false
    val alphaKeys = listOf(R.id.key_a, R.id.key_b, R.id.key_c, R.id.key_d, R.id.key_e, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_i, R.id.key_j, R.id.key_k, R.id.key_l, R.id.key_m, R.id.key_n, R.id.key_o, R.id.key_p, R.id.key_q, R.id.key_r, R.id.key_s, R.id.key_t, R.id.key_u, R.id.key_v, R.id.key_w, R.id.key_x, R.id.key_y, R.id.key_z)
    val digitKeys = listOf(R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9)
    /* Zero key is always first here, as this input method is based on numbers and calculations; the user would expect smaller numbers first */
    override fun onCreateInputView(): View {
        val keyCodeKeys = listOf(R.id.key_backspace, R.id.key_enter)
        val unrotatedKeys = listOf(R.id.key_comma, R.id.key_space, R.id.key_period)
        getStoredData(filesDir).also { stored ->
            if (stored == null) {
                Toast.makeText(this@CaesarCipherKeyboard, "Error: couldn't retrieve user-defined offsets. Defaults are used instead.", Toast.LENGTH_SHORT).show()
            } else {
                userDefinedAdditionalOffset = stored.userDefinedAdditionalOffset
                capitalizationOffset = stored.capitalizationOffset
            }
        }
        val clickListener = View.OnClickListener { view ->
            val id = view.id
            val keyCodeKeys = listOf(
                Pair(R.id.key_backspace, KEYCODE_DEL),
                Pair(R.id.key_enter, KEYCODE_ENTER)
            ).toMap()
            val unrotatedKeys = listOf(
                Pair(R.id.key_comma, ','),
                Pair(R.id.key_space, ' '),
                Pair(R.id.key_period, '.')
            ).toMap()
            val alphaKey = (id in alphaKeys)
            val digitKey = (id in digitKeys)
            if (alphaKey || digitKey) {
                currentInputConnection.commitText(
                    run {
                        val originalCode = (
                                if (alphaKey) {
                                    97 + alphaKeys.indexOf(view.id) + if (upperKeyPressed) {
                                        upperKeyPressed = false
                                        capitalizationOffset
                                    } else {
                                        0
                                    }
                                } else { //digitKey
                                    48 + digitKeys.indexOf(view.id)
                                }
                        )
                        (originalCode - 32).mod(95) + cipherDiskOffset.mod(95) + userDefinedAdditionalOffset.mod(95)
                    }.let { x ->
                        32 + x.mod(95).let { y ->
                            if (y < 0) { // fail-safe
                                y + 95
                            } else {
                                y
                            }
                        }
                    }.toChar().toString(),
                    1
                )
            } else if (id in unrotatedKeys.keys) {
                currentInputConnection.commitText(unrotatedKeys[id]!!.toString(), 1)
            } else if (id in keyCodeKeys.keys) {
                val code = keyCodeKeys[id]!!
                with(currentInputConnection) {
                    listOf(ACTION_DOWN, ACTION_UP).forEach { action ->
                        sendKeyEvent(
                            KeyEvent.changeFlags(KeyEvent(action, code), FLAG_SOFT_KEYBOARD)
                        )
                    }
                }
            } else if (id == R.id.key_upper) {
                upperKeyPressed = !upperKeyPressed
            }
        }
        val v = layoutInflater.inflate(R.layout.keyboard, null)
        for (id in alphaKeys + digitKeys + unrotatedKeys) {
            v.findViewById<Button>(id).setOnClickListener(clickListener)
        }
        for (id in keyCodeKeys + listOf(R.id.key_upper)) {
            v.findViewById<ImageButton>(id).setOnClickListener(clickListener)
        }
        v.findViewById<Button>(R.id.key_reset_disk).setOnClickListener {
            v.findViewById<SeekBar>(R.id.cipherDisk).setProgress(0, true)
        }
        v.findViewById<Button>(R.id.key_space).setOnLongClickListener {
            switchKeyboard(this@CaesarCipherKeyboard)
            (true)
        }
        v.findViewById<SeekBar>(R.id.cipherDisk).setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar, newScale: Int, isUser: Boolean) {
                    cipherDiskOffset = newScale
                    v.findViewById<Button>(R.id.key_reset_disk).text = String.format("%+03d", newScale)
                }
                override fun onStartTrackingTouch(s: SeekBar) {
                }
                override fun onStopTrackingTouch(s: SeekBar) {
                }
            }
        )
        return v
    }
}
