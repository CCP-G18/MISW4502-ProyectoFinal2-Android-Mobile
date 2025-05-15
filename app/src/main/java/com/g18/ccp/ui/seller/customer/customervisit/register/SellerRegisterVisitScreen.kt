package com.g18.ccp.ui.seller.customer.customervisit.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.g18.ccp.R
import com.g18.ccp.presentation.seller.customervisit.register.SellerRegisterVisitViewModel
import com.g18.ccp.ui.seller.customer.customervisit.list.CustomerHeader
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.SecondaryColor
import com.g18.ccp.ui.theme.UltraLightGray
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerRegisterVisitScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerRegisterVisitViewModel = koinViewModel(),
    onVisitCompletedAndNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.loadInitialData()
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayUtcMidnightCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val todayUtcMidnightMillis = todayUtcMidnightCalendar.timeInMillis
                return utcTimeMillis <= todayUtcMidnightMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYearInUtc =
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR)
                return year <= currentYearInUtc
            }
        }
    )

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.onShowDatePicker(false) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onDateSelected(datePickerState.selectedDateMillis) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MainColor)
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onShowDatePicker(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MainColor)
                ) { Text(stringResource(R.string.cancel)) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = DatePickerDefaults.colors(
                containerColor = WhiteColor,
                titleContentColor = MainColor,
                headlineContentColor = MainColor,
                weekdayContentColor = BlackColor.copy(alpha = 0.7f),
                subheadContentColor = MainColor,
                yearContentColor = BlackColor,
                currentYearContentColor = MainColor,
                selectedYearContentColor = WhiteColor,
                selectedYearContainerColor = MainColor,
                dayContentColor = BlackColor,
                disabledDayContentColor = LightGray.copy(alpha = 0.5f),
                selectedDayContentColor = WhiteColor,
                selectedDayContainerColor = MainColor,
                todayContentColor = MainColor,
                todayDateBorderColor = MainColor,
                navigationContentColor = MainColor,
                dayInSelectionRangeContainerColor = SecondaryColor.copy(alpha = 0.3f),
                dayInSelectionRangeContentColor = MainColor,
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = WhiteColor,
                    titleContentColor = MainColor,
                    headlineContentColor = MainColor,
                    weekdayContentColor = BlackColor.copy(alpha = 0.7f),
                    subheadContentColor = MainColor,
                    yearContentColor = BlackColor,
                    currentYearContentColor = MainColor,
                    selectedYearContentColor = WhiteColor,
                    selectedYearContainerColor = MainColor,
                    dayContentColor = BlackColor,
                    disabledDayContentColor = LightGray.copy(alpha = 0.5f),
                    selectedDayContentColor = WhiteColor,
                    selectedDayContainerColor = MainColor,
                    todayContentColor = MainColor,
                    todayDateBorderColor = MainColor,
                    navigationContentColor = MainColor,
                    dayInSelectionRangeContainerColor = SecondaryColor.copy(alpha = 0.3f),
                    dayInSelectionRangeContentColor = MainColor,
                )
            )
        }
    }

    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearErrorMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header
        CustomerHeader(
            customerName = uiState.customerName
        )

        // Contenido desplazable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.selectedDate,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.visit_date_label)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onShowDatePicker(true) },
                    trailingIcon = {
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = stringResource(R.string.visit_date_label),
                            modifier = Modifier.clickable { viewModel.onShowDatePicker(true) },
                            tint = MainColor
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = BlackColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = LightGray,
                        disabledBorderColor = LightGray,
                        focusedLabelColor = MainColor,
                        unfocusedLabelColor = BlackColor.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.visit_observations_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = BlackColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 0.dp)
                )
                OutlinedTextField(
                    value = uiState.observations,
                    onValueChange = { viewModel.onObservationsChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 7,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = BlackColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = LightGray,
                        disabledBorderColor = LightGray,
                        focusedTextColor = BlackColor,
                        unfocusedTextColor = BlackColor,
                        focusedContainerColor = WhiteColor,
                        unfocusedContainerColor = UltraLightGray,
                        disabledContainerColor = WhiteColor
                    )
                )

                Spacer(modifier = Modifier.weight(1f, fill = true))

                Button(
                    onClick = {
                        viewModel.saveVisit {
                            onVisitCompletedAndNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = WhiteColor,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(R.string.visit_completed_button),
                            color = WhiteColor,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        )
    }
}
