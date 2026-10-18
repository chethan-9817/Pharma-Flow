package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Medicine
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberLight
import com.example.ui.theme.MedBorder
import com.example.ui.theme.MedSlate
import com.example.ui.theme.MedTextMuted
import com.example.ui.theme.MedTextPrimary
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.RxViolet
import com.example.ui.theme.RxVioletLight
import kotlinx.coroutines.launch

@Composable
fun MedicineCard(
    medicine: Medicine,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: (Medicine) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (medicine.isNearExpiry) ExpiryAmber.copy(alpha = 0.5f) else MedBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .testTag("medicine_card_${medicine.id}"),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Visual Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (medicine.isNearExpiry) {
                            Brush.verticalGradient(listOf(ExpiryAmberLight.copy(alpha = 0.5f), MedSlate))
                        } else {
                            Brush.verticalGradient(listOf(MedicalBlueLight.copy(alpha = 0.5f), MedSlate))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Medicine / Skincare Icon
                Icon(
                    imageVector = when {
                        medicine.category.contains("Sunscreen") || medicine.dosageForm.lowercase().contains("gel") -> Icons.Default.Shield
                        medicine.dosageForm.lowercase() == "syrup" -> Icons.Default.Vaccines
                        medicine.dosageForm.lowercase() == "capsule" -> Icons.Default.MedicalServices
                        else -> Icons.Default.Medication
                    },
                    contentDescription = null,
                    tint = when {
                        medicine.category.contains("Sunscreen") -> ExpiryAmber
                        medicine.isNearExpiry -> ExpiryAmber
                        else -> MedicalBlue
                    },
                    modifier = Modifier.size(48.dp)
                )

                // Top Left: Schedule-H Rx / Eco / SPF 50 Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (medicine.category.contains("Sunscreen")) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ExpiryAmber,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "SPF 50+",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (medicine.requiresRx) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RxViolet,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Rx ONLY",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (medicine.isNearExpiry) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcoEmerald,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "50% OFF",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top Right: Wishlist Heart Button
                Surface(
                    shape = CircleShape,
                    color = MedWhite.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clickable {
                            scope.launch {
                                heartScale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                heartScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
                            }
                            onToggleWishlist()
                        }
                        .testTag("wishlist_${medicine.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(heartScale.value)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFEF4444) else MedTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Dosage Form Pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedWhite.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = medicine.dosageForm,
                        color = MedTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Near-expiry alert strip if applicable
            if (medicine.isNearExpiry) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ExpiryAmberLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Expires in ${medicine.expiryDaysLeft}d • Eco-Save",
                            color = ExpiryAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Brand Name
            Text(
                text = medicine.brandName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MedTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Generic Salt
            Text(
                text = medicine.genericSalt,
                fontSize = 11.sp,
                color = MedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Manufacturer
            Text(
                text = medicine.manufacturer,
                fontSize = 10.sp,
                color = MedTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Add to Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${String.format("%.2f", medicine.price)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (medicine.isNearExpiry) EcoEmeraldDark else MedicalBlue
                    )
                    if (medicine.mrp > medicine.price) {
                        Text(
                            text = "MRP ₹${String.format("%.2f", medicine.mrp)}",
                            fontSize = 10.sp,
                            color = MedTextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Button(
                    onClick = { onAddToCart(medicine) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (medicine.isNearExpiry) EcoEmerald else MedicalBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_to_cart_${medicine.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Add", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
