package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScratchCard
import com.example.ui.audio.SoundManager
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberLight
import com.example.ui.theme.FoilBlueEnd
import com.example.ui.theme.FoilBlueMid
import com.example.ui.theme.FoilBlueStart
import com.example.ui.theme.MedBorder
import com.example.ui.theme.MedSlate
import com.example.ui.theme.MedTextPrimary
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueLight
import kotlin.math.sqrt

@Composable
fun ScratchCardModalView(
    card: ScratchCard,
    onCompleted: () -> Unit,
    onDismiss: () -> Unit,
    onApplyToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var isRevealed by remember { mutableStateOf(card.isScratched) }
    var scratchPercentage by remember { mutableFloatStateOf(if (card.isScratched) 100f else 0f) }

    val touchPoints = remember { mutableStateListOf<Offset>() }
    val currentPath = remember { Path() }

    // 16x16 grid for sampling transparent pixel coverage
    val gridRows = 14
    val gridCols = 14
    val scratchedCells = remember { mutableSetOf<Int>() }

    fun triggerFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        } catch (_: Exception) {}
        SoundManager.playScratchSound()
    }

    fun revealPrize() {
        if (!isRevealed) {
            isRevealed = true
            scratchPercentage = 100f
            triggerFeedback()
            SoundManager.playRevealSound()
            onCompleted()
        }
    }

    val metallicBlueFoil = listOf(FoilBlueStart, FoilBlueMid, Color(0xFF0369A1), FoilBlueStart)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MedicalBlueLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "MEDWALLET SCRATCH DROP",
                    color = MedicalBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = if (isRevealed) "100% Uncovered!" else "${scratchPercentage.toInt()}% Scratched",
                color = if (isRevealed) EcoEmeraldDark else MedTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar
        val animatedProgress by animateFloatAsState(
            targetValue = (scratchPercentage / 100f).coerceIn(0f, 1f),
            label = "scratch_bar"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MedicalBlue,
            trackColor = MedBorder
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 280px x 280px Interactive Canvas Area
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, MedicalBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("interactive_scratch_canvas"),
            contentAlignment = Alignment.Center
        ) {
            // Bottom Hidden Prize Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(listOf(MedicalBlueLight, MedWhite))
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when (card.prizeType) {
                            "ECO_POINTS" -> EcoEmeraldLight
                            "CASHBACK" -> MedicalBlueLight
                            else -> ExpiryAmberLight
                        },
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (card.prizeType) {
                                    "ECO_POINTS" -> Icons.Default.Eco
                                    "CASHBACK" -> Icons.Default.AccountBalanceWallet
                                    else -> Icons.Default.LocalOffer
                                },
                                contentDescription = null,
                                tint = when (card.prizeType) {
                                    "ECO_POINTS" -> EcoEmeraldDark
                                    "CASHBACK" -> MedicalBlue
                                    else -> ExpiryAmber
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val prizeHeading = when (card.prizeType) {
                        "CASHBACK" -> "₹${card.prizeValue.toInt()} CASHBACK"
                        "ECO_POINTS" -> "+${card.prizeValue.toInt()} ECO-KARMA"
                        "DISCOUNT_PERCENT" -> "${card.prizeValue.toInt()}% OFF"
                        else -> "FREE PRIORITY DELIVERY"
                    }

                    Text(
                        text = prizeHeading,
                        color = MedicalBlue,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = card.title,
                        color = MedTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MedWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
                    ) {
                        Text(
                            text = "CODE: ${card.couponCode}",
                            color = MedTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Top Metallic Foil Layer (Destination-Out Clear touch erasing)
            if (!isRevealed) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(card.id) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    touchPoints.add(offset)
                                    currentPath.moveTo(offset.x, offset.y)
                                    triggerFeedback()
                                },
                                onDrag = { change, _ ->
                                    val pos = change.position
                                    touchPoints.add(pos)
                                    currentPath.lineTo(pos.x, pos.y)

                                    // 32px erasing circle radius math
                                    val cellW = size.width / gridCols
                                    val cellH = size.height / gridRows
                                    val brushRadius = 55f // approx 32px scaled

                                    for (r in 0 until gridRows) {
                                        for (c in 0 until gridCols) {
                                            val cellIndex = r * gridCols + c
                                            if (cellIndex !in scratchedCells) {
                                                val cx = c * cellW + (cellW / 2)
                                                val cy = r * cellH + (cellH / 2)
                                                val dist = sqrt((pos.x - cx) * (pos.x - cx) + (pos.y - cy) * (pos.y - cy))
                                                if (dist < brushRadius) {
                                                    scratchedCells.add(cellIndex)
                                                }
                                            }
                                        }
                                    }

                                    val pct = (scratchedCells.size.toFloat() / (gridRows * gridCols)) * 100f
                                    scratchPercentage = pct.coerceIn(0f, 100f)

                                    // Auto reveal once >40% scratched
                                    if (pct > 40f) {
                                        revealPrize()
                                    } else {
                                        triggerFeedback()
                                    }
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    drawIntoCanvas { canvas ->
                        val bounds = Rect(0f, 0f, w, h)
                        val foilPaint = Paint()

                        canvas.saveLayer(bounds, foilPaint)

                        // Draw metallic medical-blue pattern
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = metallicBlueFoil,
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            size = size
                        )

                        // Shimmer lines
                        val step = 28f
                        var x = 0f
                        while (x < w + h) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.2f),
                                start = Offset(x, 0f),
                                end = Offset(0f, x),
                                strokeWidth = 2f
                            )
                            x += step
                        }

                        // Erase transparent pixels along path
                        val clearPaint = Paint().apply {
                            blendMode = BlendMode.Clear
                            style = PaintingStyle.Stroke
                            strokeWidth = 64f // 32px radius
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        }

                        canvas.drawPath(currentPath, clearPaint)

                        for (pt in touchPoints) {
                            canvas.drawCircle(pt, 32f, clearPaint)
                        }

                        canvas.restore()
                    }
                }

                // Scratch Instruction Text
                if (scratchPercentage < 10f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Scratch Here with Finger or Mouse",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Confetti on win
            if (isRevealed) {
                ConfettiEffect()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        if (!isRevealed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Later", color = MedTextSecondary)
                }

                Button(
                    onClick = { revealPrize() },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                    modifier = Modifier.weight(1f).testTag("quick_reveal_scratch_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Quick Reveal", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reward Credited to MedWallet!",
                        color = EcoEmeraldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onApplyToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("claim_and_apply_to_cart_button")
                ) {
                    Text("Claim & Apply to Cart", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
