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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.compose.lotterytable.LotteryTableMainScreen
import com.example.myapplication.compose.lotterylog.LotteryLog
import com.example.myapplication.compose.lotterylog.SimpleNavBackToolbar
import com.example.myapplication.compose.lotterytable.LotteryTableToolbar
import com.example.myapplication.compose.lotterytable.vm.LotteryTableEvents
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import com.example.myapplication.compose.possibility.PossibilityScreen
import com.example.myapplication.compose.settings.PreferenceScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class MainActivity : ComponentActivity() {

    companion object {
        const val SCREEN_NAME_MAIN = "Main"
        const val SCREEN_NAME_LOTTERY_LOG = "lottery_log"
        const val SCREEN_NAME_PREFERENCE = "preference"
        const val SCREEN_NAME_POSSIBILITY = "possibility"
    }

    private val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                LaunchedEffect(this) {
                    viewModel.eventStateSharedFlow.collect { event ->
                        when (event) {
                            is LotteryTableEvents.SyncFailed -> {
                                Toast.makeText(
                                    context,
                                    event.textResource,
                                    Toast.LENGTH_LONG
                                ).show()
                                Timber.w("event: $event")
                            }
                            is LotteryTableEvents.ChangeDayNightSettings -> {
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
                    NavigationScreen()
                }
            }
        }
    }
}

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MainActivity.SCREEN_NAME_MAIN) {
        composable(MainActivity.SCREEN_NAME_MAIN) {
            Column {
                LotteryTableToolbar(navController)
                LotteryTableMainScreen()
            }
        }
        composable(MainActivity.SCREEN_NAME_LOTTERY_LOG) {
            Column {
                SimpleNavBackToolbar(navController, stringResource(id = R.string.lottery_log))
                LotteryLog()
            }
        }
        composable(MainActivity.SCREEN_NAME_PREFERENCE) {
            Column {
                SimpleNavBackToolbar(navController, stringResource(id = R.string.settings))
                PreferenceScreen {
                    navController.navigate(MainActivity.SCREEN_NAME_LOTTERY_LOG)
                }
            }
        }
        composable(MainActivity.SCREEN_NAME_POSSIBILITY) {
            Column {
                SimpleNavBackToolbar(navController, stringResource(id = R.string.possibility))
                PossibilityScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        NavigationScreen()
    }
}