package com.poolassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.net.Uri
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PoolAssistApp() }
    }
}

@Composable
fun PoolAssistApp() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var analyzed by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        analyzed = false
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF101214)) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pool Assist",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    "Visual billiards aiming assistant",
                    color = Color.LightGray
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { picker.launch("image/*") }) {
                        Text("Choose Screenshot")
                    }
                    Button(
                        enabled = imageUri != null,
                        onClick = { analyzed = true }
                    ) {
                        Text("Analyze")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    Modifier.fillMaxWidth().weight(1f)
                        .background(Color(0xFF1A1D20), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Text(
                            "Choose a screenshot of the table",
                            color = Color.Gray
                        )
                    } else {
                        Box(Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Pool table screenshot",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            if (analyzed) {
                                AimOverlay()
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    if (analyzed)
                        "Analysis complete • Suggested aiming line shown"
                    else
                        "Select a screenshot, then tap Analyze",
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun AimOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Prototype visual guide: table bounds and a suggested aiming line.
        val left = w * 0.10f
        val top = h * 0.18f
        val right = w * 0.90f
        val bottom = h * 0.82f

        drawRect(
            color = Color(0xFF00E676),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = Stroke(width = 3f)
        )

        val cue = Offset(w * 0.40f, h * 0.58f)
        val target = Offset(w * 0.72f, h * 0.38f)

        drawLine(
            color = Color(0xFFFFD740),
            start = cue,
            end = target,
            strokeWidth = 6f
        )

        drawCircle(Color.White, radius = 12f, center = cue)
        drawCircle(Color.Red, radius = 12f, center = target)

        // One-bounce guide
        val bounce = Offset(w * 0.90f, h * 0.25f)
        drawLine(
            color = Color(0xFF40C4FF),
            start = target,
            end = bounce,
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF40C4FF),
            start = bounce,
            end = Offset(w * 0.76f, h * 0.18f),
            strokeWidth = 4f
        )
    }
}
