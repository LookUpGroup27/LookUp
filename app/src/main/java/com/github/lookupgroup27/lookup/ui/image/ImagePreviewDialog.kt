package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImagePreviewDialog(uri: String?, username: String?, onDismiss: () -> Unit) {
    if (uri != null) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFF0D1023)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (username != null) {
                    Text(
                        text = "Posted by: $username",
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = Color.White
                    )
                }
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Image Preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text(text = "Close")
                }
            }
        }
    }
}
