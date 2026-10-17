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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Product
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import kotlinx.coroutines.launch

fun getProductIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "headphones" -> Icons.Default.Headphones
        "watch" -> Icons.Default.Watch
        "keyboard" -> Icons.Default.Keyboard
        "drone" -> Icons.Default.FlightTakeoff
        "gamepad" -> Icons.Default.VideogameAsset
        "battery" -> Icons.Default.BatteryChargingFull
        "camera" -> Icons.Default.CameraAlt
        "mouse" -> Icons.Default.Mouse
        "speaker" -> Icons.Default.VolumeUp
        else -> Icons.Default.Devices
    }
}

@Composable
fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Hero Visual Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DarkSurfaceElevated, Color(0xFF0F172A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Icon representation
                Icon(
                    imageVector = getProductIcon(product.iconKey),
                    contentDescription = product.title,
                    tint = BrandCyan,
                    modifier = Modifier.size(54.dp)
                )

                // Optional Badge
                if (product.badge.isNotBlank()) {
                    Surface(
                        color = when (product.badge) {
                            "HOT", "FLASH DEAL" -> Color(0xFFEF4444)
                            "BESTSELLER" -> BrandAmber
                            else -> BrandEmerald
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = product.badge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist Heart Button
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clickable {
                            scope.launch {
                                heartScale.animateTo(1.35f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                heartScale.animateTo(1.0f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
                            }
                            onToggleWishlist()
                        }
                        .testTag("wishlist_toggle_${product.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(heartScale.value)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Category pill
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = product.category,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BrandAmber,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${product.rating}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${product.reviewCount})",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = product.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Description
            Text(
                text = product.description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(30.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Add to Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = BrandEmerald
                    )
                    if (product.originalPrice > product.price) {
                        Text(
                            text = "$${String.format("%.2f", product.originalPrice)}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Button(
                    onClick = { onAddToCart(product) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_to_cart_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
