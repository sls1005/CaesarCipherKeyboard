package test.sls1005.projects.caesarcipherkeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import test.sls1005.projects.caesarcipherkeyboard.ui.theme.CaesarCipherKeyboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaesarCipherKeyboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            OutlinedButton(
                                onClick = { startActivity(Intent(ACTION_INPUT_METHOD_SETTINGS)) },
                                modifier = Modifier
                                    .size(width = 400.dp, height = 100.dp)
                                    .padding(10.dp)
                            ) {
                                Text("Enable/disable keyboards", fontSize=22.sp)
                            }
                            OutlinedButton(
                                onClick = { switchKeyboard(this@MainActivity) },
                                modifier = Modifier
                                    .size(width = 400.dp, height = 100.dp)
                                    .padding(10.dp)
                            ) {
                                Text("Change keyboard", fontSize=22.sp)
                            }
                            OutlinedButton(
                                onClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) },
                                modifier = Modifier
                                    .size(width = 400.dp, height = 100.dp)
                                    .padding(10.dp)
                            ) {
                                Text("Settings", fontSize=22.sp)
                            }
                            OutlinedButton(
                                onClick = { startActivity(Intent(this@MainActivity, DocumentationActivity::class.java)) },
                                modifier = Modifier
                                    .size(width = 400.dp, height = 100.dp)
                                    .padding(10.dp)
                            ) {
                                Text("Documentation", fontSize=22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun switchKeyboard(ctx: Context) {
    ctx.getSystemService(Context.INPUT_METHOD_SERVICE).also {
        if (it is InputMethodManager) {
            it.showInputMethodPicker()
        }
    }
}
