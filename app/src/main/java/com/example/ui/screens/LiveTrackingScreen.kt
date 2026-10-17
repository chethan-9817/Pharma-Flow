package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberLight
import com.example.ui.theme.MedBorder
import com.example.ui.theme.MedSlate
import com.example.ui.theme.MedSurfaceElevated
import com.example.ui.theme.MedTextMuted
import com.example.ui.theme.MedTextPrimary
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueLight

@Composable
fun LiveTrackingScreen(
    activeOrder: Order?,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (activeOrder == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MedSlate)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = MedTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Active Delivery", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MedTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Place an order in the catalog to initiate live pharmacist verification and courier dispatch.",
                        fontSize = 12.sp,
                        color = MedTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToHome,
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Browse Medicines", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val stages = listOf(
        Triple("Order Placed", "Chemist notified at City Meds", 1),
        Triple("Verified & Packed", "Pharmacist verified batch & seal", 2),
        Triple("Out for Delivery", "Ramesh is 0.8 km away • 6 mins ETA", 3),
        Triple("Delivered", "Delivered safely at your doorstep", 4)
    )

    // Motorcycle progress along route (0.0 to 1.0)
    val targetRiderProgress = when (activeOrder.currentStage) {
        1 -> 0.05f
        2 -> 0.25f
        3 -> 0.68f
        4 -> 1.0f
        else -> 0.1f
    }

    val animatedRiderProgress by animateFloatAsState(
        targetValue = targetRiderProgress,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "rider_pos"
    )

    // Pulse animation for destination marker
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedSlate)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Order Header Bar
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ORDER #${activeOrder.orderNumber}",
                                color = MedicalBlue,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (activeOrder.currentStage) {
                                    4 -> EcoEmeraldLight
                                    3 -> MedicalBlueLight
                                    else -> ExpiryAmberLight
                                }
                            ) {
                                Text(
                                    text = activeOrder.status.replace("_", " ").uppercase(),
                                    color = when (activeOrder.currentStage) {
                                        4 -> EcoEmeraldDark
                                        3 -> MedicalBlue
                                        else -> ExpiryAmber
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Delivering to: Kankanady, Mangaluru",
                            color = MedTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // OTP Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MedSlate,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("DELIVERY OTP", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MedTextSecondary)
                            Text(activeOrder.otp, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MedicalBlue)
                        }
                    }
                }
            }
        }

        // Simulated Live GPS Map Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("live_gps_map"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Simulated city road network
                        val roadColor = Color(0xFFCBD5E1)
                        val roadStroke = Stroke(width = 16f, cap = StrokeCap.Round)

                        // Main avenues
                        drawLine(roadColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 20f)
                        drawLine(roadColor, Offset(0f, h * 0.72f), Offset(w, h * 0.72f), strokeWidth = 22f)
                        drawLine(roadColor, Offset(w * 0.28f, 0f), Offset(w * 0.28f, h), strokeWidth = 18f)
                        drawLine(roadColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 18f)

                        // Delivery Dispatch Route (From Pharmacy to Home)
                        val startPt = Offset(w * 0.18f, h * 0.35f)
                        val midPt = Offset(w * 0.75f, h * 0.35f)
                        val endPt = Offset(w * 0.75f, h * 0.72f)

                        // Route dashed highlight
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
                        val routeStroke = Stroke(width = 8f, pathEffect = dashEffect, cap = StrokeCap.Round)
                        drawLine(Color(0xFF0284C7), startPt, midPt, strokeWidth = 8f, pathEffect = dashEffect)
                        drawLine(Color(0xFF0284C7), midPt, endPt, strokeWidth = 8f, pathEffect = dashEffect)

                        // 1. Pharmacy Origin Pin
                        drawCircle(Color(0xFF0284C7), radius = 18f, center = startPt)
                        drawCircle(Color.White, radius = 9f, center = startPt)

                        // 2. Home Destination Pin
                        drawCircle(Color(0xFF10B981).copy(alpha = 0.3f), radius = 24f * pulseAnim.value, center = endPt)
                        drawCircle(Color(0xFF10B981), radius = 18f, center = endPt)
                        drawCircle(Color.White, radius = 9f, center = endPt)

                        // 3. Interpolated Rider Motorcycle Marker
                        val riderPos = if (animatedRiderProgress <= 0.65f) {
                            val f = animatedRiderProgress / 0.65f
                            Offset(startPt.x + (midPt.x - startPt.x) * f, startPt.y)
                        } else {
                            val f = (animatedRiderProgress - 0.65f) / 0.35f
                            Offset(midPt.x, midPt.y + (endPt.y - midPt.y) * f)
                        }

                        drawCircle(Color(0xFF0F172A), radius = 22f, center = riderPos)
                        drawCircle(Color(0xFF38BDF8), radius = 16f, center = riderPos)
                    }

                    // Map Overlay Pins Labels
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedWhite.copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("City Chemist Store", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedWhite.copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Your Home", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Rider Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(activeOrder.riderName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = ExpiryAmberLight) {
                                    Text("4.9 ★", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ExpiryAmber, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("Vaccines & Cold-Chain Certified Rider", fontSize = 11.sp, color = EcoEmeraldDark, fontWeight = FontWeight.SemiBold)
                            Text(activeOrder.riderPhone, fontSize = 11.sp, color = MedTextSecondary)
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = EcoEmeraldLight,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                Toast.makeText(context, "Calling ${activeOrder.riderName}...", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("call_rider_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = EcoEmeraldDark, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // 4-Stage Live Timeline Stepper
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FULFILLMENT & DELIVERY STATUS",
                        color = MedicalBlue,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    stages.forEachIndexed { index, (stageTitle, stageSubtitle, stageNum) ->
                        val isCompleted = stageNum <= activeOrder.currentStage
                        val isCurrent = stageNum == activeOrder.currentStage

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Number / Icon node
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(30.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = when {
                                        isCompleted -> EcoEmerald
                                        isCurrent -> MedicalBlue
                                        else -> Color(0xFFCBD5E1)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        } else {
                                            Text("$stageNum", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (index < stages.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(34.dp)
                                            .background(if (stageNum < activeOrder.currentStage) EcoEmerald else Color(0xFFE2E8F0))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                Text(
                                    text = stageTitle,
                                    color = if (isCompleted) MedTextPrimary else MedTextMuted,
                                    fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = stageSubtitle,
                                    color = if (isCompleted) MedTextSecondary else MedTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
