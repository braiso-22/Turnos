package com.braiso22.turnos.tasks.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.ui.theme.TurnosTheme

@Composable
fun ProfilePictureComponent(
    user: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(4.dp)

    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .fillMaxSize()
                .clip(
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            /*if (painter != null)
                AsyncImage(
                    model = "https://example.com/image.jpg",
                    contentDescription = "Translated description of what the image contains"
                )
            else*/
            Text(
                text = user.take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePictureComponentPreview() {
    val state by remember {
        mutableStateOf(
            "Hola"
        )
    }
    TurnosTheme {
        ProfilePictureComponent(
            user = state,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        )
    }
}