package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.data.model.CardTier
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.data.model.ScratchCard
import com.example.ui.components.ScratchCardView
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FoilDiamond
import com.example.ui.theme.FoilGold
import com.example.ui.theme.FoilSilver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchScreen(
    cards: List<ScratchCard>,
    coupons: List<Coupon>,
    activeCard: ScratchCard?,
    onOpenCard: (ScratchCard) -> Unit,
    onCloseCard: () -> Unit,
    onCompleteScratch: (ScratchCard) -> Unit,
    onClaimDailyBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedCode by remember { mutableStateOf<String?>(null) }

    val unscratchedCards = cards.filter { !it.isScratched }
    val scratchedCards = cards.filter { it.isScratched }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Mystery Bonus Card Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF311042), Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.5.dp, BrandAmber, RoundedCornerShape(20.dp))
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
                                    color = BrandAmber.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = BrandAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "DAILY REWARD VAULT",
                                        color = BrandAmber,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Daily Mystery Scratcher",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Log in daily to claim free high-tier discount scratchers with up to 45% off coupons!",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onClaimDailyBonus,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_daily_bonus_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Claim Bonus Scratch Card", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Available Cards Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY UNTOUCHED CARDS (${unscratchedCards.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                if (unscratchedCards.isNotEmpty()) {
                    Text(
                        text = "Tap to Scratch",
                        color = BrandEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (unscratchedCards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
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
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Unscratched Cards Left",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Place an order in the Shop or complete daily quests to earn bonus mystery cards!",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(unscratchedCards, key = { it.id }) { card ->
                val tierColor = when (card.tier) {
                    CardTier.SILVER -> FoilSilver
                    CardTier.GOLD -> FoilGold
                    CardTier.DIAMOND -> FoilDiamond
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, tierColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { onOpenCard(card) }
                        .testTag("unscratched_card_${card.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tierColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.5f)),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = tierColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Surface(
                                    color = tierColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = card.tier.displayName.uppercase(),
                                        color = tierColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = card.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = card.subtitle,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Scratch",
                            tint = tierColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Revealed Coupons Wallet
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "WON COUPONS WALLET (${coupons.size})",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        if (coupons.isEmpty()) {
            item {
                Text(
                    text = "Scratch your cards above to reveal and save coupons in your wallet.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }
        } else {
            items(coupons, key = { it.code }) { coupon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (coupon.isUsed) DarkBorder else BrandEmerald.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp)
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
                                    color = if (coupon.isUsed) Color(0xFF334155) else BrandEmerald.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = coupon.code,
                                        color = if (coupon.isUsed) Color(0xFF94A3B8) else BrandEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                if (coupon.isUsed) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "REDEEMED",
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = coupon.description,
                                color = if (coupon.isUsed) Color(0xFF64748B) else Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }

                        if (!coupon.isUsed) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(coupon.code))
                                    copiedCode = coupon.code
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedCode == coupon.code) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = BrandEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copiedCode == coupon.code) "Copied" else "Copy",
                                    color = BrandEmerald,
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

    // Modal Active Scratching Bottom Sheet
    activeCard?.let { card ->
        ModalBottomSheet(
            onDismissRequest = onCloseCard,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkBg,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFF475569), RoundedCornerShape(2.dp))
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ScratchCardView(
                    card = card,
                    onCompleted = { onCompleteScratch(card) },
                    onDismiss = onCloseCard
                )
            }
        }
    }
}
