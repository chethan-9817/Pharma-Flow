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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.example.data.model.Order
import com.example.data.model.PrescriptionScanResult
import com.example.ui.components.ConfettiEffect
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
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.RxViolet
import com.example.ui.theme.RxVioletLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSheet(
    cartItems: List<CartItem>,
    subtotal: Double,
    ecoSavings: Double,
    walletDiscount: Double,
    grandTotal: Double,
    medWalletBalance: Double,
    useWalletCashback: Boolean,
    onToggleWalletCashback: () -> Unit,
    prescriptionResult: PrescriptionScanResult?,
    onScanRxPrompt: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: (paymentMethod: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    val totalItemsCount = cartItems.sumOf { it.quantity }
    val hasScheduleHDrugs = cartItems.any { it.medicine.requiresRx }
    val isRxBlocked = hasScheduleHDrugs && (prescriptionResult?.isVerified != true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
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
                        tint = MedicalBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MEDICINE BAG ($totalItemsCount)",
                        color = MedTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MedTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MedTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your bag is empty",
                            color = MedTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Explore medicines and 50% off near-expiry clearance!",
                            color = MedTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Schedule-H Prescription Banner in Cart
                if (hasScheduleHDrugs) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (prescriptionResult?.isVerified == true) EcoEmeraldLight else RxVioletLight,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (prescriptionResult?.isVerified == true) EcoEmerald else RxViolet
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (prescriptionResult?.isVerified == true) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (prescriptionResult?.isVerified == true) EcoEmeraldDark else RxViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (prescriptionResult?.isVerified == true) "Schedule-H Drugs Verified" else "Requires Doctor's Rx",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (prescriptionResult?.isVerified == true) EcoEmeraldDark else RxViolet
                                    )
                                    Text(
                                        text = if (prescriptionResult?.isVerified == true) "Approved by ${prescriptionResult.doctorName}" else "Upload prescription for instant AI validation",
                                        fontSize = 11.sp,
                                        color = MedTextSecondary
                                    )
                                }
                            }

                            if (prescriptionResult?.isVerified != true) {
                                Button(
                                    onClick = onScanRxPrompt,
                                    colors = ButtonDefaults.buttonColors(containerColor = RxViolet),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Verify Rx", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems, key = { it.medicine.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedSlate,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
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
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (item.medicine.isNearExpiry) ExpiryAmberLight else MedicalBlueLight,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Medication,
                                                contentDescription = null,
                                                tint = if (item.medicine.isNearExpiry) ExpiryAmber else MedicalBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = item.medicine.brandName,
                                            color = MedTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "₹${String.format("%.2f", item.medicine.price)} • ${item.medicine.dosageForm}",
                                            color = if (item.medicine.isNearExpiry) EcoEmeraldDark else MedicalBlue,
                                            fontSize = 11.sp,
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
                                        color = MedWhite,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clickable { onUpdateQuantity(item.medicine.id, -1) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                tint = MedTextPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${item.quantity}",
                                        color = MedTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    Surface(
                                        shape = CircleShape,
                                        color = MedicalBlue,
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clickable { onUpdateQuantity(item.medicine.id, 1) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Increase",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MedWallet Cashback Deduction Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MedicalBlueLight.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedicalBlue.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleWalletCashback() }
                        .testTag("apply_medwallet_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = useWalletCashback,
                                onCheckedChange = { onToggleWalletCashback() },
                                colors = CheckboxDefaults.colors(checkedColor = MedicalBlue)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Use MedWallet Balance",
                                    color = MedicalBlueDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Available: ₹${medWalletBalance.toInt()} • Save instantly",
                                    color = MedTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (walletDiscount > 0.0) {
                            Text(
                                text = "-₹${walletDiscount.toInt()}",
                                color = EcoEmeraldDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MedBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Price Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Medicines Subtotal", color = MedTextSecondary, fontSize = 12.sp)
                    Text("₹${String.format("%.2f", subtotal)}", color = MedTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                if (ecoSavings > 0.0) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Eco-Clearance Savings (50%)", color = EcoEmeraldDark, fontSize = 12.sp)
                        Text("-₹${String.format("%.2f", ecoSavings)}", color = EcoEmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (walletDiscount > 0.0) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("MedWallet Cashback Applied", color = MedicalBlue, fontSize = 12.sp)
                        Text("-₹${String.format("%.2f", walletDiscount)}", color = MedicalBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hyperlocal Courier (Mangaluru)", color = MedTextSecondary, fontSize = 12.sp)
                    Text("FREE (11 Mins)", color = EcoEmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grand Total", color = MedTextPrimary, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text(
                        "₹${String.format("%.2f", grandTotal)}",
                        color = MedicalBlue,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Checkout Button
                Button(
                    onClick = { onCheckout(selectedPaymentMethod) },
                    enabled = !isRxBlocked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalBlue,
                        disabledContainerColor = MedBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("checkout_order_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRxBlocked) "Scan Prescription to Unlock Checkout" else "Place Order & Earn Mystery Scratch Card",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OrderCelebrationDialog(
    order: Order,
    onDismiss: () -> Unit,
    onNavigateToRewards: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MedWhite)
            .border(2.dp, EcoEmerald, RoundedCornerShape(24.dp))
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
                color = EcoEmeraldLight,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EcoEmeraldDark,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Order Placed Successfully!",
                color = MedTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )

            Text(
                text = "Order #${order.orderNumber} • Delivery OTP: ${order.otp}",
                color = MedicalBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mystery Card Awarded Callout
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ExpiryAmberLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, ExpiryAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ExpiryAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NEW SCRATCH CARD UNLOCKED!",
                            color = ExpiryAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Mystery Scratch voucher deposited in your Rewards tab for completing order #${order.orderNumber}!",
                            color = MedTextPrimary,
                            fontSize = 11.sp
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
                        onNavigateToRewards()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpiryAmber),
                    modifier = Modifier.weight(1f).testTag("dialog_scratch_now_button")
                ) {
                    Text("Scratch Card", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalNavy),
                    modifier = Modifier.weight(1f).testTag("dialog_done_button")
                ) {
                    Text("Track Order", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
