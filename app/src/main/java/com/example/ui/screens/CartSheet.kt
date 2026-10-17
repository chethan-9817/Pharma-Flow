package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.getProductIcon
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSheet(
    cartItems: List<CartItem>,
    availableCoupons: List<Coupon>,
    selectedCoupon: Coupon?,
    subtotal: Double,
    discount: Double,
    total: Double,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onSelectCoupon: (Coupon?) -> Unit,
    onApplyManualPromo: (String) -> String?,
    onCheckout: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCouponPicker by remember { mutableStateOf(false) }
    var manualPromoInput by remember { mutableStateOf("") }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var isPromoSuccess by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Cart Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = BrandEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YOUR BAG (${cartItems.sumOf { it.quantity }})",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your cart is empty",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Browse the catalog and add awesome gadgets!",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DarkSurfaceElevated,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = getProductIcon(item.product.iconKey),
                                                contentDescription = null,
                                                tint = BrandCyan,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = item.product.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", item.product.price)} each",
                                            color = BrandEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Quantity Controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = DarkSurfaceElevated,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { onUpdateQuantity(item.product.id, -1) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${item.quantity}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    Surface(
                                        shape = CircleShape,
                                        color = BrandEmerald,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { onUpdateQuantity(item.product.id, 1) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Increase",
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Manual Promo Code Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = manualPromoInput,
                        onValueChange = {
                            manualPromoInput = it
                            promoMessage = null
                        },
                        placeholder = { Text("Promo Code (e.g. LUCKY2026)", color = Color(0xFF64748B), fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = BrandCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_promo_input")
                    )

                    Button(
                        onClick = {
                            val err = onApplyManualPromo(manualPromoInput)
                            if (err != null) {
                                promoMessage = err
                                isPromoSuccess = false
                            } else {
                                promoMessage = "Promo Code Applied!"
                                isPromoSuccess = true
                                manualPromoInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("apply_promo_code_button")
                    ) {
                        Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                promoMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (isPromoSuccess) BrandEmerald else Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scratch Coupon Selector Strip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedCoupon != null) BrandEmerald else BrandAmber.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCouponPicker = !showCouponPicker }
                        .testTag("apply_scratch_coupon_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = if (selectedCoupon != null) BrandEmerald else BrandAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCoupon?.let { "Applied: ${it.code} (-$${String.format("%.2f", discount)})" }
                                    ?: "Choose from Won Scratch Coupons",
                                color = if (selectedCoupon != null) BrandEmerald else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = if (showCouponPicker) "Close" else "Choose",
                            color = BrandCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coupon Selection List Drawer
                AnimatedVisibility(visible = showCouponPicker) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (availableCoupons.isEmpty()) {
                            Text(
                                text = "No active coupons found. Scratch cards in the Scratch tab to win discounts!",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        } else {
                            availableCoupons.forEach { coupon ->
                                val isSelected = selectedCoupon?.code == coupon.code
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) BrandEmerald.copy(alpha = 0.2f) else DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) BrandEmerald else DarkBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectCoupon(if (isSelected) null else coupon)
                                            showCouponPicker = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = coupon.code,
                                                color = BrandAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = coupon.description,
                                                color = Color.White,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Text(
                                            text = if (isSelected) "Applied" else "Select",
                                            color = if (isSelected) BrandEmerald else Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Price Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text("$${String.format("%.2f", subtotal)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (discount > 0.0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount Savings", color = BrandEmerald, fontSize = 13.sp)
                        Text("-$${String.format("%.2f", discount)}", color = BrandEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text("FREE", color = BrandEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Due", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text(
                        "$${String.format("%.2f", total)}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Checkout Button with Mystery Scratch reward callout
                Button(
                    onClick = onCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("checkout_order_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Checkout & Earn Scratch Card",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OrderCelebrationDialog(
    order: Order,
    onDismiss: () -> Unit,
    onNavigateToScratch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurfaceElevated)
            .border(2.dp, BrandEmerald, RoundedCornerShape(24.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ConfettiEffect()

            Surface(
                shape = CircleShape,
                color = BrandEmerald,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Order Placed Successfully!",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )

            Text(
                text = "Receipt #${order.orderNumber}",
                color = BrandCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mystery Card Awarded Callout
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BrandAmber.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NEW SCRATCH CARD UNLOCKED!",
                            color = BrandAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${order.rewardCardTier.displayName} added to your vault for completing order #${order.orderNumber}!",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        onNavigateToScratch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                    modifier = Modifier.weight(1f).testTag("dialog_scratch_now_button")
                ) {
                    Text("Scratch Now!", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.weight(1f).testTag("dialog_done_button")
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
