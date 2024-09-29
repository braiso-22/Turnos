package com.braiso22.turnos.receipts.presentation.open.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.R
import com.braiso22.turnos.ui.theme.TurnosTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptListComponent(
    state: Map<String, List<ReceiptUiState>>,
    onClickConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier
    ) {
        if (state.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_pending_receipts),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        state.forEach { (date, receipts) ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(date, style = MaterialTheme.typography.titleLarge)
                }
            }
            items(receipts) { receipt ->
                ReceiptComponent(
                    state = receipt,
                    onClickConfirm = onClickConfirm,
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptListComponentPreview() {
    val day = LocalDate.now()

    TurnosTheme {
        ReceiptListComponent(
            state = mapOf(
                day.toUi() to List(3) {
                    ReceiptUiState(
                        id = "1",
                        taskName = "Take away the stinky",
                        userName = "Braiso22",
                        time = "1$it:00",
                    )
                },
                day.plusDays(1).toUi() to List(2) {
                    ReceiptUiState(
                        id = "1",
                        taskName = "Take away the stinky",
                        userName = "Braiso22",
                        time = "1$it:00",
                    )
                }
            ),
            onClickConfirm = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun LocalDate.toUi() = this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))