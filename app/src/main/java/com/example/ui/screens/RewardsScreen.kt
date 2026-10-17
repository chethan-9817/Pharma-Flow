package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScratchCard
import com.example.ui.components.ScratchCardModalView
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    cards: List<ScratchCard>,
    medWalletBalance: Double,
    ecoKarmaPoints: Int,
    activeCard: ScratchCard?,
    onOpenCard: (ScratchCard) -> Unit,
    onCloseCard: () -> Unit,
    onCompleteScratch: (ScratchCard) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedCode by remember { mutableStateOf<String?>(null) }

    val unscratchedCards = cards.filter { !it.isScratched }
    val scratchedCards = cards.filter { it.isScratched }

    val animatedBalance by animateFloatAsState(targetValue = medWalletBalance.toFloat(), label = "wallet_anim")
    val animatedKarma by animateIntAsState(targetValue = ecoKarmaPoints, label = "karma_anim")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedSlate)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // MedWallet & Eco-Karma Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(MedicalBlueLight.copy(alpha = 0.8f), EcoEmeraldLight.copy(alpha = 0.5f), MedWhite)
                            )
                        )
                        .border(1.5.dp, MedicalBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MedicalBlue,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MEDWALLET & REWARDS",
                                        color = MedicalBlue,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Your Pharmacy Credits",
                                        color = MedTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Balances Split Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // MedWallet Cashback
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MedWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalBlue.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Cashback Balance", fontSize = 11.sp, color = MedTextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "₹${String.format("%.0f", animatedBalance)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = MedicalBlue
                                    )
                                    Text("Applicable at checkout", fontSize = 10.sp, color = MedTextMuted)
                                }
                            }

                            // Eco-Karma Points
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MedWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Eco-Karma Points", fontSize = 11.sp, color = MedTextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$animatedKarma pts",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = EcoEmeraldDark
                                    )
                                    Text("Earned from returns & deals", fontSize = 10.sp, color = MedTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Available Mystery Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MYSTERY SCRATCH CARDS (${unscratchedCards.size})",
                    color = MedTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )

                if (unscratchedCards.isNotEmpty()) {
                    Text(
                        text = "Tap to Scratch",
                        color = MedicalBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (unscratchedCards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MedWhite),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MedTextMuted,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Mystery Cards Left", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedTextPrimary)
                        Text(
                            "Complete an order or schedule a safe medicine return to earn bonus scratch cards!",
                            fontSize = 12.sp,
                            color = MedTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(unscratchedCards, key = { it.id }) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenCard(card) }
                        .testTag("scratch_card_${card.id}"),
                    colors = CardDefaults.cardColors(containerColor = MedWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MedicalBlueLight.copy(alpha = 0.5f), MedWhite)
                                )
                            )
                            .border(1.dp, MedicalBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MedicalBlue,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MedicalBlueLight
                                    ) {
                                        Text(
                                            text = "READY TO REVEAL",
                                            color = MedicalBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = card.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MedTextPrimary
                                    )
                                    Text(
                                        text = card.subtitle,
                                        fontSize = 11.sp,
                                        color = MedTextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MedicalBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Scratched Vouchers History
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "REVEALED REWARDS & COUPONS (${scratchedCards.size})",
                color = MedTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }

        if (scratchedCards.isEmpty()) {
            item {
                Text(
                    text = "Scratch cards above to uncover cashback credits and discount coupons.",
                    fontSize = 12.sp,
                    color = MedTextMuted
                )
            }
        } else {
            items(scratchedCards, key = { it.id }) { card ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MedWhite),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EcoEmeraldLight
                                ) {
                                    Text(
                                        text = card.couponCode,
                                        color = EcoEmeraldDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CLAIMED",
                                    color = MedTextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = card.subtitle,
                                color = MedTextPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(card.couponCode))
                                copiedCode = card.couponCode
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (copiedCode == card.couponCode) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = MedicalBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (copiedCode == card.couponCode) "Copied" else "Copy", fontSize = 11.sp, color = MedicalBlue)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Modal Active Scratching Bottom Sheet
    activeCard?.let { card ->
        ModalBottomSheet(
            onDismissRequest = onCloseCard,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MedWhite,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(MedBorder, RoundedCornerShape(2.dp))
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                ScratchCardModalView(
                    card = card,
                    onCompleted = { onCompleteScratch(card) },
                    onDismiss = onCloseCard,
                    onApplyToCart = {
                        onCloseCard()
                        onNavigateToHome()
                    }
                )
            }
        }
    }
}
