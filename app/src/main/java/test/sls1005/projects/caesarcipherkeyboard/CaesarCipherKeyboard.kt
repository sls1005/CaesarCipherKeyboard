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
    val alphaKeys = intArrayOf(R.id.key_a, R.id.key_b, R.id.key_c, R.id.key_d, R.id.key_e, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_i, R.id.key_j, R.id.key_k, R.id.key_l, R.id.key_m, R.id.key_n, R.id.key_o, R.id.key_p, R.id.key_q, R.id.key_r, R.id.key_s, R.id.key_t, R.id.key_u, R.id.key_v, R.id.key_w, R.id.key_x, R.id.key_y, R.id.key_z).zip(0 .. 25).toMap()
    val digitKeys = intArrayOf(R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9).zip(0 .. 9).toMap()
    /* Zero key is always first here, as this input method is based on numbers and calculations; the user would expect smaller numbers first */
    val keyCodeKeys = arrayOf(
        Pair(R.id.key_backspace, KEYCODE_DEL),
        Pair(R.id.key_enter, KEYCODE_ENTER)
    ).toMap()
    val unrotatedKeys = arrayOf(
        Pair(R.id.key_comma, ','),
        Pair(R.id.key_comma, ','),
        Pair(R.id.key_space, ' '),
        Pair(R.id.key_period, '.')
    ).toMap()
    override fun onCreateInputView(): View {
        val keyCodeKeyListener = View.OnClickListener { view ->
            with(currentInputConnection) {
                arrayOf(ACTION_DOWN, ACTION_UP).forEach { action ->
                    sendKeyEvent(
                        KeyEvent.changeFlags(KeyEvent(action, keyCodeKeys[view.id]!!), FLAG_SOFT_KEYBOARD)
                    )
                }
            }
        }
        val unrotatedKeyListener = View.OnClickListener { view ->
            currentInputConnection.commitText(unrotatedKeys[view.id]!!.toString(), 1)
        }
        val alphaKeyListener = View.OnClickListener { view ->
            currentInputConnection.commitText(
                algorithm(
                    97 + alphaKeys[view.id]!! + (
                        if (upperKeyPressed) {
                            upperKeyPressed = false
                            capitalizationOffset
                        } else {
                            0
                        }
                    )
                ).toChar().toString(),
                1
            )
        }
        val digitKeyListener = View.OnClickListener { view ->
            currentInputConnection.commitText(
                algorithm(48 + digitKeys[view.id]!!).toChar().toString(),
                1
            )
        }
        getStoredData(filesDir).also { stored ->
            if (stored == null) {
                Toast.makeText(this@CaesarCipherKeyboard, "Error: couldn't retrieve user-defined offsets. Defaults are used instead.", Toast.LENGTH_SHORT).show()
            } else {
                userDefinedAdditionalOffset = stored.userDefinedAdditionalOffset
                capitalizationOffset = stored.capitalizationOffset
            }
        }
        val v = layoutInflater.inflate(R.layout.keyboard, null)
        for (id in alphaKeys.keys) {
            v.findViewById<Button>(id).setOnClickListener(alphaKeyListener)
        }
        for (id in digitKeys.keys) {
            v.findViewById<Button>(id).setOnClickListener(digitKeyListener)
        }
        for (id in unrotatedKeys.keys) {
            v.findViewById<Button>(id).setOnClickListener(unrotatedKeyListener)
        }
        for (id in keyCodeKeys.keys) {
            v.findViewById<ImageButton>(id).setOnClickListener(keyCodeKeyListener)
        }
        v.findViewById<ImageButton>(R.id.key_upper).setOnClickListener {
            upperKeyPressed = !upperKeyPressed
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
    private inline fun algorithm(x: Int): Int {
        return (32 + (
            (x - 32).mod(95) + cipherDiskOffset.mod(95) + userDefinedAdditionalOffset.mod(95)
        ).mod(95)).let { y ->
            if (y < 0) { // fail-safe
                y + 95
            } else {
                y
            }
        }
    }
}
