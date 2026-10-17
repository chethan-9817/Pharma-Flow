package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardTier
import com.example.data.model.DiscountType
import com.example.data.model.FoilStyle
import com.example.data.model.ScratchCard
import com.example.ui.audio.SoundManager
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.FoilDiamond
import com.example.ui.theme.FoilDiamondAccent
import com.example.ui.theme.FoilGold
import com.example.ui.theme.FoilGoldAccent
import com.example.ui.theme.FoilSilver
import com.example.ui.theme.FoilSilverAccent
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun ScratchCardView(
    card: ScratchCard,
    onCompleted: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val scope = rememberCoroutineScope()

    var isRevealed by remember { mutableStateOf(card.isScratched) }
    var scratchPercentage by remember { mutableFloatStateOf(if (card.isScratched) 100f else 0f) }
    var copiedToClipboard by remember { mutableStateOf(false) }
    var selectedFoilStyle by remember {
        mutableStateOf(
            when (card.tier) {
                CardTier.DIAMOND -> FoilStyle.HOLOGRAPHIC
                CardTier.GOLD -> FoilStyle.GOLD_LEAF
                CardTier.SILVER -> FoilStyle.CYBER_NEON
            }
        )
    }

    // Brush touch paths
    val touchPoints = remember { mutableStateListOf<Offset>() }
    val currentPath = remember { Path() }
    var dragTickCount by remember { mutableIntStateOf(0) }

    // Sample points grid for accurate scratch percentage calculation
    val gridRows = 12
    val gridCols = 16
    val scratchedCells = remember { mutableSetOf<Int>() }

    fun triggerHapticAndSound() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) {}
        SoundManager.playScratchSound()
    }

    fun revealPrize() {
        if (!isRevealed) {
            isRevealed = true
            scratchPercentage = 100f
            triggerHapticAndSound()
            SoundManager.playRevealSound()
            onCompleted()
        }
    }

    // Dynamic foil color brushes based on selected FoilStyle
    val foilColors = when (selectedFoilStyle) {
        FoilStyle.HOLOGRAPHIC -> listOf(
            Color(0xFFEC4899), // Pink
            Color(0xFF8B5CF6), // Violet
            Color(0xFF06B6D4), // Cyan
            Color(0xFF10B981), // Emerald
            Color(0xFFFBBF24), // Amber
            Color(0xFFEC4899)  // Loop
        )
        FoilStyle.GOLD_LEAF -> listOf(
            FoilGoldAccent,
            FoilGold,
            Color(0xFFB45309),
            FoilGoldAccent
        )
        FoilStyle.CYBER_NEON -> listOf(
            Color(0xFF06B6D4),
            Color(0xFF0F172A),
            Color(0xFF8B5CF6),
            Color(0xFF0284C7)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF131B2E))
            .border(2.dp, foilColors.first().copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Tier & Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = foilColors[1].copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, foilColors[1].copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = foilColors[1],
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = card.tier.displayName.uppercase(),
                            color = foilColors[1],
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = if (isRevealed) "100% Revealed!" else "${scratchPercentage.toInt()}% Scratched",
                    color = if (isRevealed) BrandEmerald else Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Foil Style Selector Tabs (Only if not yet fully scratched)
            if (!isRevealed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FoilStyle.values().forEach { style ->
                        val isSelected = style == selectedFoilStyle
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BrandEmerald.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BrandEmerald else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFoilStyle = style }
                        ) {
                            Text(
                                text = style.displayName.split(" ")[0],
                                color = if (isSelected) BrandEmerald else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Bar
            val animatedProgress by animateFloatAsState(
                targetValue = (scratchPercentage / 100f).coerceIn(0f, 1f),
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = foilColors[1],
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Scratch Card Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("scratch_canvas_card"),
                contentAlignment = Alignment.Center
            ) {
                // 1. Prize Artwork Layer (Base)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.5.dp, BrandEmerald.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = BrandEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val discountHeading = when (card.discountType) {
                            DiscountType.PERCENTAGE -> "${card.discountValue.toInt()}% OFF"
                            DiscountType.FLAT_AMOUNT -> "$${card.discountValue.toInt()} OFF"
                            DiscountType.FREE_SHIPPING -> "FREE SHIPPING"
                        }
                        Text(
                            text = discountHeading,
                            color = BrandEmerald,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = card.title,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Coupon Code Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0B0F19),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text(
                                text = "CODE: ${card.couponCode}",
                                color = BrandAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // 2. Interactive Foil Layer (Canvas with BlendMode.Clear destination-out)
                if (!isRevealed) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(card.id, selectedFoilStyle) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        touchPoints.add(offset)
                                        currentPath.moveTo(offset.x, offset.y)
                                        triggerHapticAndSound()
                                    },
                                    onDrag = { change, _ ->
                                        val pos = change.position
                                        touchPoints.add(pos)
                                        currentPath.lineTo(pos.x, pos.y)

                                        dragTickCount++
                                        if (dragTickCount % 3 == 0) {
                                            SoundManager.playScratchSound()
                                        }

                                        // Calculate grid coverage
                                        val cellW = size.width / gridCols
                                        val cellH = size.height / gridRows
                                        val brushRadius = 55f

                                        for (r in 0 until gridRows) {
                                            for (c in 0 until gridCols) {
                                                val cellIndex = r * gridCols + c
                                                if (cellIndex !in scratchedCells) {
                                                    val cellCenterX = c * cellW + (cellW / 2)
                                                    val cellCenterY = r * cellH + (cellH / 2)
                                                    val dist = sqrt(
                                                        (pos.x - cellCenterX) * (pos.x - cellCenterX) +
                                                                (pos.y - cellCenterY) * (pos.y - cellCenterY)
                                                    )
                                                    if (dist < brushRadius) {
                                                        scratchedCells.add(cellIndex)
                                                    }
                                                }
                                            }
                                        }

                                        val totalCells = gridRows * gridCols
                                        val pct = (scratchedCells.size.toFloat() / totalCells) * 100f
                                        scratchPercentage = pct.coerceIn(0f, 100f)

                                        if (pct >= 45f) {
                                            revealPrize()
                                        }
                                    }
                                )
                            }
                    ) {
                        val canvasW = size.width
                        val canvasH = size.height

                        drawIntoCanvas { canvas ->
                            val bounds = Rect(0f, 0f, canvasW, canvasH)
                            val foilPaint = Paint()

                            canvas.saveLayer(bounds, foilPaint)

                            // Draw selected metallic foil gradient
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = foilColors,
                                    start = Offset(0f, 0f),
                                    end = Offset(canvasW, canvasH)
                                ),
                                size = size
                            )

                            // Metallic Texture Glints
                            val step = 26f
                            var x = 0f
                            while (x < canvasW + canvasH) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.15f),
                                    start = Offset(x, 0f),
                                    end = Offset(0f, x),
                                    strokeWidth = 1.8f
                                )
                                x += step
                            }

                            // Erase touched points using destination-out Clear
                            val clearPaint = Paint().apply {
                                blendMode = BlendMode.Clear
                                style = PaintingStyle.Stroke
                                strokeWidth = 65f
                                strokeCap = StrokeCap.Round
                                strokeJoin = StrokeJoin.Round
                            }

                            canvas.drawPath(currentPath, clearPaint)

                            // Also draw circles at all sampled touch points to avoid gaps
                            for (pt in touchPoints) {
                                canvas.drawCircle(pt, 32f, clearPaint)
                            }

                            canvas.restore()
                        }
                    }

                    // Scratch Guide Overlay
                    if (scratchPercentage < 10f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BrandAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "DRAG FINGER TO SCRATCH FOIL",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Confetti particle burst overlay on complete
                if (isRevealed) {
                    ConfettiEffect()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (!isRevealed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).testTag("cancel_scratch_button")
                    ) {
                        Text("Save for Later")
                    }

                    Button(
                        onClick = { revealPrize() },
                        colors = ButtonDefaults.buttonColors(containerColor = foilColors[1]),
                        modifier = Modifier.weight(1f).testTag("instant_reveal_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Reveal", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else {
                // Revealed Success Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reward Saved to Your Wallet!",
                            color = BrandEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(card.couponCode))
                                copiedToClipboard = true
                            },
                            modifier = Modifier.weight(1f).testTag("copy_code_button")
                        ) {
                            Icon(
                                if (copiedToClipboard) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (copiedToClipboard) "Copied!" else "Copy Code")
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                            modifier = Modifier.weight(1f).testTag("use_in_shop_button")
                        ) {
                            Text("Use in Shop", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
