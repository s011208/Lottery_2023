package com.bj4.lottery2023.compose.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.bj4.lottery2023.BuildConfig
import com.bj4.lottery2023.R
import com.bj4.lottery2023.compose.appsettings.DropDatabase
import com.bj4.lottery2023.compose.appsettings.ResetDatabase
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableEvents
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.example.analytics.Analytics
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.*
import com.example.service.cache.DayNightMode
import com.example.service.cache.FontSize
import com.jamal.composeprefs3.ui.GroupHeader
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.ListPref
import com.jamal.composeprefs3.ui.prefs.SliderPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import org.koin.java.KoinJavaComponent
import kotlin.system.exitProcess

// https://github.com/JamalMulla/ComposePrefs3/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferenceScreen(onLotteryDataClick: () -> Unit = {}) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)
        analytics.trackScreen("PreferenceScreen")
    }

    val resetDatabaseDialogOpen = remember {
        mutableStateOf(false)
    }

    val dropDatabaseDialogOpen = remember {
        mutableStateOf(false)
    }

    ResetDatabase(dialogOpen = resetDatabaseDialogOpen)
    DropDatabase(dialogOpen = dropDatabaseDialogOpen)

    PrefsScreen(LocalContext.current.settingsDataStore) {
        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.settings_lottery_table_display),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                ListPref(
                    key = SETTINGS_KEY_FONT_SIZE,
                    title = stringResource(id = R.string.font_size),
                    entries = mapOf(
                        FontSize.EXTRA_SMALL.toString() to stringResource(id = R.string.x_small),
                        FontSize.SMALL.toString() to stringResource(id = R.string.small),
                        FontSize.NORMAL.toString() to stringResource(id = R.string.normal),
                        FontSize.LARGE.toString() to stringResource(id = R.string.large),
                        FontSize.EXTRA_LARGE.toString() to stringResource(id = R.string.x_large),
                    ),
                    defaultValue = FontSize.NORMAL.toString(),
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_LTO_TABLE,
                    title = stringResource(id = R.string.settings_lto_table_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 3f,
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE,
                    title = stringResource(id = R.string.settings_lto_big_table_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 2f,
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_LTO_HK_TABLE,
                    title = stringResource(id = R.string.settings_lto_hk_table_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 2f,
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_LTO_539_TABLE,
                    title = stringResource(id = R.string.settings_lto_539_table_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 5f,
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_LIST_TABLE,
                    title = stringResource(id = R.string.settings_list_table_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 10f,
                )

                CheckBoxPref(
                    key = SETTINGS_SHOW_DIVIDE_LINE,
                    title = stringResource(id = R.string.settings_show_divide_line),
                )
            }
        }

        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.settings_plus_minus_display),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                ListPref(
                    key = SETTINGS_FONT_SIZE_PLUS_MINUS,
                    title = stringResource(id = R.string.font_size),
                    entries = mapOf(
                        FontSize.EXTRA_SMALL.toString() to stringResource(id = R.string.x_small),
                        FontSize.SMALL.toString() to stringResource(id = R.string.small),
                        FontSize.NORMAL.toString() to stringResource(id = R.string.normal),
                        FontSize.LARGE.toString() to stringResource(id = R.string.large),
                        FontSize.EXTRA_LARGE.toString() to stringResource(id = R.string.x_large),
                    ),
                    defaultValue = FontSize.NORMAL.toString(),
                )

                SliderPref(
                    key = SETTINGS_EXTRA_SPACING_PLUS_MINUS,
                    title = stringResource(id = R.string.settings_plus_minus_extra_spacing),
                    valueRange = 0f..100f,
                    showValue = true,
                    steps = 99,
                    defaultValue = 20f,
                )

                CheckBoxPref(
                    key = SETTINGS_SHOW_DIVIDE_LINE_PLUS_MINUS,
                    title = stringResource(id = R.string.settings_show_divide_line),
                    defaultChecked = false,
                )
            }
        }

        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.settings_app),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                ListPref(
                    key = SETTINGS_KEY_DAY_NIGHT_MODE,
                    title = stringResource(id = R.string.day_night_settings),
                    entries = mapOf(
                        DayNightMode.DAY.toString() to stringResource(id = R.string.mode_day),
                        DayNightMode.NIGHT.toString() to stringResource(id = R.string.mode_night),
                        DayNightMode.AUTO.toString() to stringResource(id = R.string.mode_system),
                    ),
                    defaultValue = DayNightMode.DAY.toString(),
                )
            }
        }


        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.settings_lottery_data),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                TextPref(title = stringResource(id = R.string.update_lto), onClick = {
                    Toast.makeText(context, R.string.update_lto, Toast.LENGTH_LONG).show()
                    viewModel.handleEvent(LotteryTableEvents.UpdateData)
                }, enabled = true)
            }

            prefsItem {
                TextPref(title = stringResource(id = R.string.reset), onClick = {
                    resetDatabaseDialogOpen.value = true
                }, enabled = true)
            }

            prefsItem {
                TextPref(title = stringResource(id = R.string.drop), onClick = {
                    dropDatabaseDialogOpen.value = true
                }, enabled = true)
            }

            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.Lto))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_big), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.LtoBig))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_hk), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.LtoHK))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_539), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.Lto539))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_cf5), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.LtoCF5))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_list3), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.LtoList3))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
            prefsItem {
                TextPref(title = stringResource(id = R.string.drop_lto_list4), onClick = {
                    viewModel.handleEvent(LotteryTableEvents.ClearLotteryData(LotteryType.LtoList4))
                    Toast.makeText(context, R.string.drop_done, Toast.LENGTH_LONG).show()
                }, enabled = true)
            }
        }

        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.settings_app_data),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                TextPref(
                    title = stringResource(id = R.string.settings_app_version_name),
                    summary = BuildConfig.VERSION_NAME
                )
            }
            prefsItem {
                TextPref(
                    title = stringResource(id = R.string.settings_app_version_code),
                    summary = BuildConfig.VERSION_CODE.toString()
                )
            }
            prefsItem {
                TextPref(
                    title = stringResource(id = R.string.google_play_link),
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${context.packageName}")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                )
                            )
                        }
                    }, enabled = true
                )
            }
        }

        prefsGroup({
            GroupHeader(
                title = stringResource(id = R.string.danger_zone),
                color = MaterialTheme.colorScheme.secondary
            )
        }) {
            prefsItem {
                TextPref(title = stringResource(id = R.string.lottery_log), onClick = {
                    onLotteryDataClick()
                }, enabled = true)
            }

            prefsItem {
                TextPref(
                    title = stringResource(id = R.string.force_close),
                    onClick = {
                        exitProcess(0)
                    },
                    enabled = true
                )
            }
        }
    }
}