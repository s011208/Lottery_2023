package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.compose.AppToolbar
import com.example.myapplication.compose.MainScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.vm.MyEvents
import com.example.myapplication.vm.MyViewModel
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val viewModel: MyViewModel by KoinJavaComponent.inject(MyViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                LaunchedEffect(this) {
                    viewModel.eventStateSharedFlow.collect { event ->
                        when (event) {
                            is MyEvents.SyncFailed -> {
                                Toast.makeText(
                                    context,
                                    event.textResource,
                                    Toast.LENGTH_LONG
                                ).show()
                                Timber.w("event: $event")
                            }
                            is MyEvents.ChangeDayNightSettings -> {
                                Utils.setMode(context, event.dayNightSettings)
                            }
                            else -> {}
                        }
                    }
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        AppToolbar()
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        MainScreen()
    }
}