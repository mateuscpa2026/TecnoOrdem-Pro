package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R

@Composable
fun AppLogoView(
    logoUri: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isCircular: Boolean = false
) {
    val context = LocalContext.current
    val shape = if (isCircular) CircleShape else RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .size(size)
            .then(if (!logoUri.isNullOrBlank()) Modifier.clip(shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUri.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(logoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Logotipo da Empresa",
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_app_logo),
                fallback = painterResource(id = R.drawable.ic_app_logo),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "Logotipo TecnoOrdem Pro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
