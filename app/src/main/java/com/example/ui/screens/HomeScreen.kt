package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Medicine
import com.example.data.model.PrescriptionScanResult
import com.example.ui.components.MedicineCard
import com.example.ui.components.MedicineImage
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberDark
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
import com.example.ui.theme.RxViolet
import com.example.ui.theme.RxVioletLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    medicines: List<Medicine>,
    wishlistIds: Set<String>,
    onToggleWishlist: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    medWalletBalance: Double,
    prescriptionResult: PrescriptionScanResult?,
    isScanningRx: Boolean,
    onSimulateScanRx: (onDone: () -> Unit) -> Unit,
    onAddToCart: (Medicine) -> Unit,
    onNavigateToRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRxScannerSheet by remember { mutableStateOf(false) }
    val categories = listOf(
        "All",
        "Skincare & Sunscreen",
        "Tablets",
        "Fever & Pain",
        "Antibiotics",
        "Vitamins",
        "Diabetes",
        "First Aid",
        "❤️ Wishlist"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedSlate)
    ) {
        // App Header: Location, MedWallet Chip, and Profile
        Surface(
            color = MedWhite,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MedicalBlueLight,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MedicalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mangaluru",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = MedTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EcoEmeraldLight
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(11.dp))
                                        Text("11 Mins", color = EcoEmeraldDark, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                            Text(
                                text = "Kankanady • Chemist Dispatch Ready",
                                fontSize = 11.sp,
                                color = MedTextSecondary
                            )
                        }
                    }

                    // MedWallet Balance Pill & Profile
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MedicalBlueLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedicalBlue.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clickable { onNavigateToRewards() }
                                .testTag("header_medwallet_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MedicalBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "₹${medWalletBalance.toInt()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MedicalBlueDark
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Chethan K.",
                            tint = MedTextSecondary,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Search Brand Name or Generic Salt (e.g. Dolo, Paracetamol)...",
                            fontSize = 12.sp,
                            color = MedTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MedicalBlue, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = MedTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MedWhite,
                        unfocusedContainerColor = MedSlate,
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MedBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }

        // Horizontal Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat.equals(selectedCategory, true)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MedicalBlue else MedWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MedicalBlue else MedBorder
                    ),
                    modifier = Modifier
                        .clickable { onCategoryChange(cat) }
                        .testTag("category_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else MedTextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Catalog Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Smart Prescription AI Verification Banner
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showRxScannerSheet = true }
                        .testTag("upload_rx_banner"),
                    colors = CardDefaults.cardColors(containerColor = MedWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(RxVioletLight.copy(alpha = 0.6f), MedicalBlueLight.copy(alpha = 0.4f))
                                )
                            )
                            .border(1.dp, RxViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = RxViolet,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (prescriptionResult?.isVerified == true) Icons.Default.CheckCircle else Icons.Default.DocumentScanner,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (prescriptionResult?.isVerified == true) "Rx VERIFIED BY AI" else "SMART Rx SCANNER",
                                            color = RxViolet,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        if (prescriptionResult?.isVerified == true) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                    Text(
                                        text = if (prescriptionResult?.isVerified == true) "Schedule-H Medicines Unlocked" else "Upload Prescription for Instant AI Verification",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MedTextPrimary
                                    )
                                    Text(
                                        text = if (prescriptionResult?.isVerified == true) "Dr. Rajesh Shenoy, MD • Valid for 30 Days" else "Extracts medicines & dosage in 2 seconds",
                                        fontSize = 11.sp,
                                        color = MedTextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = { showRxScannerSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RxViolet),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (prescriptionResult?.isVerified == true) "View" else "Scan Rx",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Derma Suncare Top Shelf Banner & Carousel
            if (selectedCategory.equals("All", true) || selectedCategory.contains("Sunscreen", true) || selectedCategory.contains("Skincare", true)) {
                item(span = { GridItemSpan(2) }) {
                    DermaSuncareTopShelf(
                        onAddToCart = onAddToCart,
                        onViewAllSuncare = { onCategoryChange("Skincare & Sunscreen") }
                    )
                }
            }

            // Products
            if (medicines.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = MedTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No medicines found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MedTextPrimary
                            )
                            Text(
                                text = "Try adjusting your search by generic salt name",
                                fontSize = 12.sp,
                                color = MedTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(medicines, key = { it.id }) { med ->
                    MedicineCard(
                        medicine = med,
                        isWishlisted = wishlistIds.contains(med.id),
                        onToggleWishlist = { onToggleWishlist(med.id) },
                        onAddToCart = onAddToCart,
                        onClick = {}
                    )
                }
            }
        }
    }

    // Prescription Scanner Modal Bottom Sheet
    if (showRxScannerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRxScannerSheet = false },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI PRESCRIPTION VERIFICATION",
                            color = RxViolet,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Smart Doctor's Rx Scanner",
                            color = MedTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = { showRxScannerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MedTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prescription Laser Scanner Graphic Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MedSlate)
                        .border(1.5.dp, if (prescriptionResult?.isVerified == true) EcoEmerald else RxViolet, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (prescriptionResult?.isVerified == true) EcoEmeraldLight else RxVioletLight,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (prescriptionResult?.isVerified == true) Icons.Default.CheckCircle else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (prescriptionResult?.isVerified == true) EcoEmeraldDark else RxViolet,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (prescriptionResult?.isVerified == true) "Prescription Verified & Valid" else "Dr. Rajesh Shenoy, MD • Mangaluru",
                            color = MedTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = if (prescriptionResult?.isVerified == true) "Extracted 4 Schedule-H Drugs: Augmentin, Azithral, Glycomet, Pan-D" else "Ready to scan and extract Schedule-H dosage instructions",
                            color = MedTextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // 2-Second Animated Laser Beam
                    if (isScanningRx) {
                        val laserOffset = remember { Animatable(0f) }
                        LaunchedEffect(Unit) {
                            laserOffset.animateTo(
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val y = laserOffset.value * size.height
                            drawLine(
                                color = Color(0xFFEF4444),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 5f
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
                        ) {
                            Text(
                                text = "Extracting Medicines & Dosage...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (prescriptionResult?.isVerified != true) {
                    Button(
                        onClick = {
                            onSimulateScanRx {
                                showRxScannerSheet = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RxViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("scan_sample_prescription_button")
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Sample Doctor Prescription", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = { showRxScannerSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Done • Schedule-H Medicines Unlocked", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DermaSuncareTopShelf(
    onAddToCart: (Medicine) -> Unit,
    onViewAllSuncare: () -> Unit
) {
    val sunscreens = listOf(
        Medicine(
            id = "sun_1",
            brandName = "The Derma Co 1% Hyaluronic Sunscreen Aqua Gel",
            genericSalt = "Hyaluronic Acid 1% + Vitamin E (SPF 50 PA++++)",
            manufacturer = "Honasa Consumer Ltd. • Dermatologist Tested",
            price = 449.00,
            mrp = 499.00,
            stock = 75,
            requiresRx = false,
            dosageForm = "Aqua Gel",
            category = "Skincare & Sunscreen",
            batchNumber = "TDC-HG2024"
        ),
        Medicine(
            id = "sun_2",
            brandName = "Minimalist Sunscreen SPF 50 Multi-Vitamin",
            genericSalt = "Niacinamide + Vitamin B5 + Vitamin F (Broad Spectrum PA++++)",
            manufacturer = "Uprising Science Pvt. Ltd. • Non-Comedogenic",
            price = 379.00,
            mrp = 399.00,
            stock = 90,
            requiresRx = false,
            dosageForm = "Fluid Gel",
            category = "Skincare & Sunscreen",
            batchNumber = "MNL-MV50"
        ),
        Medicine(
            id = "sun_3",
            brandName = "Dermatouch Undamaged Ultra Matte Silicone Sunscreen",
            genericSalt = "Zinc Oxide + Titanium Dioxide (SPF 50 PA+++ Matte Finish)",
            manufacturer = "Dermatouch Skin Science • Sweat & Water Resistant",
            price = 465.00,
            mrp = 499.00,
            stock = 50,
            requiresRx = false,
            dosageForm = "Silicone Gel",
            category = "Skincare & Sunscreen",
            batchNumber = "DMT-MS50"
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ExpiryAmberLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = ExpiryAmberDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Derma Suncare Top Shelf",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MedTextPrimary
                        )
                        Text(
                            text = "SPF 50+ & PA++++ Clinical Photoprotection",
                            fontSize = 11.sp,
                            color = MedTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedicalBlueLight,
                    modifier = Modifier.clickable { onViewAllSuncare() }
                ) {
                    Text(
                        text = "View All →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalBlueDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Suncare Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sunscreens.forEach { sun ->
                    SuncareShelfCard(medicine = sun, onAddToCart = { onAddToCart(sun) })
                }
            }
        }
    }
}

@Composable
private fun SuncareShelfCard(
    medicine: Medicine,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(190.dp)
            .padding(vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Product Photo Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedSlate)
            ) {
                MedicineImage(
                    medicine = medicine,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 10.dp
                )

                // Top Floating Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ExpiryAmber,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "SPF 50+ PA++++",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = medicine.brandName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MedTextPrimary,
                maxLines = 2,
                lineHeight = 15.sp
            )

            Text(
                text = medicine.genericSalt,
                fontSize = 10.sp,
                color = MedTextSecondary,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${medicine.price.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedTextPrimary
                    )
                    Text(
                        text = "MRP ₹${medicine.mrp.toInt()}",
                        fontSize = 10.sp,
                        color = MedTextMuted,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }

                Button(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

