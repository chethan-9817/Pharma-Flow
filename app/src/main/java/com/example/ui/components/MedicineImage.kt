package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.Medicine
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberLight
import com.example.ui.theme.MedSlate
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueLight

object MedicineImageProvider {

    /**
     * Maps any medicine item to an optimized, high-fidelity pharmaceutical/cosmetic image URL.
     */
    fun getImageUrl(medicine: Medicine): String {
        return when (medicine.id) {
            // Premium Dermatological Sunscreens
            "sun_1" -> "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=480&q=80" // The Derma Co Aqua Gel
            "sun_2" -> "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=480&q=80" // Minimalist SPF 50
            "sun_3" -> "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=480&q=80" // Dermatouch Matte Silicone

            // Clinical Demand Tablets
            "tab_1", "med_1" -> "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=480&q=80" // Dolo 650 Paracetamol
            "tab_2", "med_2" -> "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?auto=format&fit=crop&w=480&q=80" // Augmentin 625 Duo
            "tab_3", "med_8" -> "https://images.unsplash.com/photo-1471864190281-a93a3070b6de?auto=format&fit=crop&w=480&q=80" // Montair-LC
            "tab_4" -> "https://images.unsplash.com/photo-1550572017-edd951aa8f72?auto=format&fit=crop&w=480&q=80" // Thyronorm 50mcg
            "tab_5" -> "https://images.unsplash.com/photo-1577401239170-897942555fb3?auto=format&fit=crop&w=480&q=80" // Zincovit Multivitamin

            // Common Chronic & Acute Catalog
            "med_3" -> "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?auto=format&fit=crop&w=480&q=80" // Azithral 500
            "med_4" -> "https://images.unsplash.com/photo-1585435557343-3b092031a831?auto=format&fit=crop&w=480&q=80" // Pan-D Capsules
            "med_5" -> "https://images.unsplash.com/photo-1550572017-edd951aa8f72?auto=format&fit=crop&w=480&q=80" // Glycomet-GP 1
            "med_6" -> "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?auto=format&fit=crop&w=480&q=80" // Telma 40
            "med_7" -> "https://images.unsplash.com/photo-1577401239170-897942555fb3?auto=format&fit=crop&w=480&q=80" // Shelcal 500
            "med_9" -> "https://images.unsplash.com/photo-1631549916768-4119b2e5f926?auto=format&fit=crop&w=480&q=80" // Benadryl Cough Syrup
            "med_10" -> "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=480&q=80" // Volini Pain Relief Gel
            "med_11" -> "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=480&q=80" // Betadine Ointment
            "med_12" -> "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=480&q=80" // Neurobion Forte

            // Eco Batches
            "eco_1" -> "https://images.unsplash.com/photo-1585435557343-3b092031a831?auto=format&fit=crop&w=480&q=80" // Amoxicillin Eco
            "eco_2" -> "https://images.unsplash.com/photo-1577401239170-897942555fb3?auto=format&fit=crop&w=480&q=80" // Celin 500 Chewable

            else -> getFallbackUrl(medicine.category, medicine.dosageForm)
        }
    }

    fun getFallbackUrl(category: String, dosageForm: String): String {
        val formLower = dosageForm.lowercase()
        val catLower = category.lowercase()
        return when {
            catLower.contains("sunscreen") || catLower.contains("skincare") ->
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=480&q=80"
            formLower.contains("syrup") ->
                "https://images.unsplash.com/photo-1631549916768-4119b2e5f926?auto=format&fit=crop&w=480&q=80"
            formLower.contains("gel") || formLower.contains("ointment") ->
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=480&q=80"
            formLower.contains("capsule") ->
                "https://images.unsplash.com/photo-1585435557343-3b092031a831?auto=format&fit=crop&w=480&q=80"
            catLower.contains("vitamin") ->
                "https://images.unsplash.com/photo-1577401239170-897942555fb3?auto=format&fit=crop&w=480&q=80"
            else ->
                "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=480&q=80"
        }
    }
}

/**
 * High-performance, smooth, hardware-accelerated image component for medicines and skincare products.
 * Includes silky smooth shimmer loading and elegant zero-jank vector fallbacks.
 */
@Composable
fun MedicineImage(
    medicine: Medicine,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageUrl = MedicineImageProvider.getImageUrl(medicine)
    MedicineImageFromUrl(
        imageUrl = imageUrl,
        category = medicine.category,
        dosageForm = medicine.dosageForm,
        isNearExpiry = medicine.isNearExpiry,
        modifier = modifier,
        cornerRadius = cornerRadius,
        contentScale = contentScale
    )
}

@Composable
fun MedicineImageFromUrl(
    imageUrl: String?,
    category: String = "Tablets",
    dosageForm: String = "Tablet",
    isNearExpiry: Boolean = false,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val imageRequest = rememberImageRequest(context, imageUrl)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MedSlate),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    ShimmerImagePlaceholder(isNearExpiry = isNearExpiry)
                },
                error = {
                    MedicineIconFallback(
                        category = category,
                        dosageForm = dosageForm,
                        isNearExpiry = isNearExpiry
                    )
                }
            )
        } else {
            MedicineIconFallback(
                category = category,
                dosageForm = dosageForm,
                isNearExpiry = isNearExpiry
            )
        }
    }
}

@Composable
private fun rememberImageRequest(context: android.content.Context, url: String?): ImageRequest {
    return androidx.compose.runtime.remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .crossfade(250)
            .allowHardware(true)
            .build()
    }
}

@Composable
fun ShimmerImagePlaceholder(
    isNearExpiry: Boolean = false,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val baseColor = if (isNearExpiry) ExpiryAmberLight.copy(alpha = 0.4f) else MedicalBlueLight.copy(alpha = 0.35f)
    val highlightColor = Color.White.copy(alpha = 0.65f)

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 400f, translateAnim - 400f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
fun MedicineIconFallback(
    category: String,
    dosageForm: String,
    isNearExpiry: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isSunscreen = category.contains("Sunscreen", ignoreCase = true) || dosageForm.contains("gel", ignoreCase = true)
    val isSyrup = dosageForm.equals("syrup", ignoreCase = true)
    val isCapsule = dosageForm.equals("capsule", ignoreCase = true)

    val iconVector = when {
        isSunscreen -> Icons.Default.Shield
        isSyrup -> Icons.Default.Vaccines
        isCapsule -> Icons.Default.MedicalServices
        else -> Icons.Default.Medication
    }

    val gradientColors = when {
        isNearExpiry -> listOf(ExpiryAmberLight, MedSlate)
        isSunscreen -> listOf(ExpiryAmberLight.copy(alpha = 0.7f), MedSlate)
        else -> listOf(MedicalBlueLight.copy(alpha = 0.7f), MedSlate)
    }

    val iconTint = when {
        isNearExpiry -> ExpiryAmber
        isSunscreen -> ExpiryAmber
        else -> MedicalBlue
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(36.dp)
        )
    }
}
