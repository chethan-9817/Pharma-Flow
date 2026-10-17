package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalNavy

@Composable
fun EvaluatorPresenterBar(
    isLiveConnected: Boolean,
    onToggleSyncMode: () -> Unit,
    onSimulatePack: () -> Unit,
    onSimulateRiderOut: () -> Unit,
    onSimulateDelivered: () -> Unit,
    onGrantScratchCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MedicalNavy,
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Evaluator Tag
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "PRESENTER DOCK",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                // Supabase Sync Status Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isLiveConnected) EcoEmerald.copy(alpha = 0.2f) else ExpiryAmber.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isLiveConnected) EcoEmerald else ExpiryAmber
                    ),
                    modifier = Modifier
                        .clickable { onToggleSyncMode() }
                        .testTag("evaluator_sync_status")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLiveConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isLiveConnected) EcoEmerald else ExpiryAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLiveConnected) "Supabase: Live" else "Supabase: Local Cache",
                            color = if (isLiveConnected) EcoEmerald else ExpiryAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Simulate Chemist: Pack Order
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MedicalBlue.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedicalBlue),
                    modifier = Modifier
                        .clickable { onSimulatePack() }
                        .testTag("evaluator_pack_order")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("1. Pack Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // Simulate Rider: Out for Delivery
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier
                        .clickable { onSimulateRiderOut() }
                        .testTag("evaluator_rider_out")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("2. Rider En Route", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // Simulate Rider: Mark Delivered
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoEmerald.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald),
                    modifier = Modifier
                        .clickable { onSimulateDelivered() }
                        .testTag("evaluator_mark_delivered")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("3. Delivered", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // + Instant Demo Scratch Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ExpiryAmber.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExpiryAmber),
                    modifier = Modifier
                        .clickable { onGrantScratchCard() }
                        .testTag("evaluator_grant_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ExpiryAmber, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Demo Scratch Card", color = ExpiryAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
