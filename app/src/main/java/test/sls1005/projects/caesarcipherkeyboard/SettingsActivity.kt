package test.sls1005.projects.caesarcipherkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import test.sls1005.projects.caesarcipherkeyboard.ui.theme.CaesarCipherKeyboardTheme

class SettingsActivity : ComponentActivity() {
    var capitalizationOffset = -32
    var userDefinedAdditionalOffset = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onResume() {
        super.onResume()
        getStoredData(filesDir).also { stored ->
            if (stored != null) {
                userDefinedAdditionalOffset = stored.userDefinedAdditionalOffset
                capitalizationOffset = stored.capitalizationOffset
            }
        }
        setContent {
            CaesarCipherKeyboardTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Settings", fontWeight = FontWeight.Bold)
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        var label1 by remember { mutableStateOf("$capitalizationOffset") }
                        var label2 by remember { mutableStateOf("$userDefinedAdditionalOffset") }
                        var showsDialogForInputtingNumber by remember { mutableStateOf(false) }
                        var numberDialogMode by remember { mutableStateOf(0) }
                        if (showsDialogForInputtingNumber) {
                            var input by remember { mutableStateOf("") }
                            input = when (numberDialogMode) {
                                1 -> "$capitalizationOffset"
                                2 -> "$userDefinedAdditionalOffset"
                                else -> ""
                            }
                            Dialog(
                                onDismissRequest = {
                                    val s = input
                                    var backwardCounter = run { // = allowed max input length
                                        var x = Int.MAX_VALUE
                                        var k = 0
                                        while (x != 0) { // 1 + int(log 10)
                                            x = x / 10
                                            k += 1
                                        }
                                        (k)
                                    }
                                    var errorFlag = false
                                    val validatedInput = buildString(s.length) {
                                        for (c in s) {
                                            if (backwardCounter == 0) {
                                                break
                                            }
                                            if (c.isDigit()) {
                                                append(c)
                                            } else if (c == '-') {
                                                append(c)
                                            } else {
                                                errorFlag = true
                                                break
                                            }
                                            backwardCounter -= 1
                                        }
                                    }
                                    if (!errorFlag) {
                                        try {
                                            validatedInput.toInt().also { result ->
                                                if (numberDialogMode == 1) {
                                                    capitalizationOffset = result
                                                    label1 = "$result"
                                                } else if (numberDialogMode == 2) {
                                                    userDefinedAdditionalOffset = result
                                                    label2 = "$result"
                                                }
                                            }
                                        } catch (_: NumberFormatException) {
                                            // do nothing
                                        }
                                    }
                                    showsDialogForInputtingNumber = false
                                }
                            ) {
                                Card(
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Enter a number", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                                        OutlinedTextField(
                                            input,
                                            textStyle = TextStyle(fontSize = 30.sp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            onValueChange = { input = it },
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Text("Capitalization offset", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier =  Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp))
                        Text(label1,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(20.dp).clickable(onClick = {
                                numberDialogMode = 1
                                showsDialogForInputtingNumber = true
                            })
                        )
                        Text("Additional offset", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier =  Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp))
                        Text(label2,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(20.dp).clickable(onClick = {
                                numberDialogMode = 2
                                showsDialogForInputtingNumber = true
                            })
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        storeData()
        super.onPause()
    }

    override fun onStop() {
        storeData()
        super.onStop()
    }

    private fun storeData() {
        storeData(filesDir,
            StoredData(
                userDefinedAdditionalOffset = userDefinedAdditionalOffset,
                capitalizationOffset = capitalizationOffset
            )
        )
    }
}
