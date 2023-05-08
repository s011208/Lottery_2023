package com.bj4.lottery2023.compose.settings

import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

// https://github.com/JamalMulla/ComposePrefs3/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferenceScreen(onLotteryDataClick: () -> Unit = {}) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val context = LocalContext.current

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

                ListPref(
                    key = SETTINGS_KEY_DAY_NIGHT_MODE,
                    title = stringResource(id = R.string.day_night_settings),
                    entries = mapOf(
                        DayNightMode.DAY.toString() to stringResource(id = R.string.mode_day),
                        DayNightMode.NIGHT.toString() to stringResource(id = R.string.mode_night),
                        DayNightMode.AUTO.toString() to stringResource(id = R.string.mode_system),
                    ),
                    defaultValue = DayNightMode.AUTO.toString(),
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
                TextPref(title = stringResource(id = R.string.lottery_log), onClick = {
                    onLotteryDataClick()
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
        }
    }
}