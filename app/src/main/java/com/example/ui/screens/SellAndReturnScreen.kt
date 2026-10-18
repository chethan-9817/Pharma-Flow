package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProductListing
import com.example.ui.components.MedicineImageFromUrl
import com.example.ui.components.MedicineImageProvider
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.EcoEmeraldLight
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.ExpiryAmberDark
import com.example.ui.theme.ExpiryAmberLight
import com.example.ui.theme.MedBackground
import com.example.ui.theme.MedBorder
import com.example.ui.theme.MedCardBackground
import com.example.ui.theme.MedSlate
import com.example.ui.theme.MedTextPrimary
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellAndReturnScreen(
    viewModel: StoreViewModel,
    onNavigateToEcoDeals: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userListings by viewModel.userListings.collectAsState()
    val medWalletBalance by viewModel.medWalletBalance.collectAsState()
    val ecoKarmaPoints by viewModel.ecoKarmaPoints.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sell Form, 1: My Listings, 2: Return Expired Strips
    var showSuccessNotification by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedBackground),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedWhite)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(EcoEmeraldLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Sell,
                                        contentDescription = null,
                                        tint = EcoEmeraldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sell & Return Hub",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedTextPrimary
                                )
                            }
                            Text(
                                text = "Monetize sealed products or recycle expired strips",
                                fontSize = 12.sp,
                                color = MedTextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Wallet & Karma Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EcoEmeraldLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "₹${medWalletBalance.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EcoEmeraldDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Circular Economy Metric Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = "Active Listings",
                            value = "${userListings.size}",
                            icon = Icons.Default.Sell,
                            accentColor = MedicalBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Eco-Karma",
                            value = "$ecoKarmaPoints pts",
                            icon = Icons.Default.Eco,
                            accentColor = EcoEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Doorstep Pickup",
                            value = "Free ⚡",
                            icon = Icons.Default.LocalShipping,
                            accentColor = ExpiryAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Segmented Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MedWhite,
                contentColor = MedicalBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MedicalBlue,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MedBorder)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Sell Product",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "My Listings",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            if (userListings.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(EcoEmerald),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${userListings.size}",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Return Expired",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Sell Form Tab
                item {
                    SellProductForm(
                        onSubmit = { name, brand, cat, dosage, batch, expiry, mrp, price, condition, photo ->
                            viewModel.createUserListing(
                                productName = name,
                                brandName = brand,
                                category = cat,
                                dosageOrVolume = dosage,
                                batchNumber = batch,
                                expiryDate = expiry,
                                originalMrp = mrp,
                                listingPrice = price,
                                packageCondition = condition,
                                photoUrl = photo,
                                onSuccess = {
                                    showSuccessNotification = true
                                    selectedTab = 1 // Switch to active listings
                                }
                            )
                        }
                    )
                }
            }
            1 -> {
                // Active Listings Tab
                if (userListings.isEmpty()) {
                    item {
                        EmptyListingsState(onStartListing = { selectedTab = 0 })
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Circular Marketplace Items",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MedTextPrimary
                                )
                                Text(
                                    text = "${userListings.size} active",
                                    fontSize = 12.sp,
                                    color = MedTextSecondary
                                )
                            }
                        }
                    }

                    items(userListings, key = { it.id }) { listing ->
                        UserListingCard(
                            listing = listing,
                            onApprove = { viewModel.approveUserListing(listing.id) },
                            onDelete = { viewModel.deleteUserListing(listing.id) }
                        )
                    }
                }
            }
            2 -> {
                // Return Expired Strips Tab
                item {
                    ReturnExpiredStripsCard(
                        viewModel = viewModel,
                        onScheduleSuccess = {
                            selectedTab = 1
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MedCardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 10.sp, color = MedTextSecondary, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedTextPrimary)
        }
    }
}

@Composable
fun SellProductForm(
    onSubmit: (
        name: String,
        brand: String,
        category: String,
        dosage: String,
        batch: String,
        expiry: String,
        mrp: Double,
        price: Double,
        condition: String,
        photo: String?
    ) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("The Derma Co") }
    var category by remember { mutableStateOf("Skincare & Sunscreen") }
    var dosageOrVolume by remember { mutableStateOf("50g Aqua Gel Tube") }
    var batchNumber by remember { mutableStateOf("BATCH-2024-X1") }
    var expiryDate by remember { mutableStateOf("11/2026") }
    var mrpText by remember { mutableStateOf("499") }
    var priceText by remember { mutableStateOf("320") }
    var condition by remember { mutableStateOf("Factory Sealed Box") }
    var hasUploadedPhoto by remember { mutableStateOf(true) }

    val mrp = mrpText.toDoubleOrNull() ?: 0.0
    val price = priceText.toDoubleOrNull() ?: 0.0
    val estimatedPayout = price * 0.90

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Verification & Safety Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MedicalBlueLight.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MedicalBlue.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = MedicalBlueDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pharmacist Quality & Authenticity Guarantee",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MedicalBlueDark
                    )
                    Text(
                        text = "All user products must be factory sealed with intact seals, batch number, and at least 3 months shelf life remaining. 90% payout credited upon rider pickup.",
                        fontSize = 11.sp,
                        color = MedTextPrimary,
                        modifier = Modifier.padding(top = 2.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Category Selector
        Text(
            text = "1. Select Category",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MedTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Skincare & Sunscreen", "Tablets", "General Health").forEach { cat ->
                val isSelected = category == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MedicalBlue else MedCardBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MedicalBlue else MedBorder
                    ),
                    modifier = Modifier
                        .clickable {
                            category = cat
                            if (cat == "Skincare & Sunscreen") {
                                productName = "1% Hyaluronic Sunscreen Aqua Gel"
                                brandName = "The Derma Co"
                                dosageOrVolume = "50g Tube"
                                mrpText = "499"
                                priceText = "340"
                            } else if (cat == "Tablets") {
                                productName = "Augmentin 625 Duo"
                                brandName = "GlaxoSmithKline"
                                dosageOrVolume = "10 Tablets Strip"
                                mrpText = "205"
                                priceText = "140"
                            }
                        }
                ) {
                    Text(
                        text = cat,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else MedTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Popular Brand Quick Chips
        Text(
            text = "2. Quick Brand Presets",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MedTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("The Derma Co", "Minimalist", "Dermatouch", "Cipla", "Micro Labs").forEach { b ->
                val isSelected = brandName == b
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) EcoEmeraldLight else MedWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) EcoEmerald else MedBorder
                    ),
                    modifier = Modifier.clickable {
                        brandName = b
                        if (b == "The Derma Co") {
                            productName = "1% Hyaluronic Sunscreen Aqua Gel SPF 50"
                            mrpText = "499"
                            priceText = "330"
                        } else if (b == "Minimalist") {
                            productName = "SPF 50 Multi-Vitamin Sunscreen"
                            mrpText = "399"
                            priceText = "270"
                        } else if (b == "Dermatouch") {
                            productName = "Undamaged Ultra Matte Silicone Sunscreen"
                            mrpText = "499"
                            priceText = "340"
                        }
                    }
                ) {
                    Text(
                        text = b,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        color = if (isSelected) EcoEmeraldDark else MedTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Details Fields
        Text(
            text = "3. Product & Verification Details",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MedTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name & Specification") },
                    placeholder = { Text("e.g. 1% Hyaluronic Sunscreen Aqua Gel") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MedBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Brand / Maker") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = dosageOrVolume,
                        onValueChange = { dosageOrVolume = it },
                        label = { Text("Pack Size / Form") },
                        placeholder = { Text("e.g. 50g Gel / 10 Tabs") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number") },
                        placeholder = { Text("e.g. BATCH-7721") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_batch_number"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry (MM/YYYY)") },
                        placeholder = { Text("e.g. 12/2026") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_expiry_date"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price and Payout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = mrpText,
                        onValueChange = { mrpText = it },
                        label = { Text("Original MRP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Asking Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_listing_price"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time payout indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EcoEmeraldLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Net Doorstep Payout",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EcoEmeraldDark
                            )
                            Text(
                                text = "90% credited to MedWallet upon doorstep chemist handoff",
                                fontSize = 10.sp,
                                color = MedTextSecondary
                            )
                        }
                        Text(
                            text = "₹${"%.1f".format(estimatedPayout)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoEmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Package Seal Condition
                Text(
                    text = "Package Condition:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MedTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Factory Sealed Box", "Intact Blister", "Tamper Seal Present").forEach { cond ->
                        val isSel = condition == cond
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MedicalBlueLight else MedBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) MedicalBlue else MedBorder
                            ),
                            modifier = Modifier.clickable { condition = cond }
                        ) {
                            Text(
                                text = cond,
                                fontSize = 10.sp,
                                color = if (isSel) MedicalBlueDark else MedTextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo Upload Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasUploadedPhoto) EcoEmeraldLight.copy(alpha = 0.4f) else MedBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hasUploadedPhoto) EcoEmerald else MedBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasUploadedPhoto = !hasUploadedPhoto }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasUploadedPhoto) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MedSlate)
                            ) {
                                MedicineImageFromUrl(
                                    imageUrl = MedicineImageProvider.getFallbackUrl(category, dosageOrVolume),
                                    category = category,
                                    dosageForm = dosageOrVolume,
                                    cornerRadius = 8.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(EcoEmeraldDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = MedicalBlueLight,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = MedicalBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasUploadedPhoto) "Package Photo Verified (Batch & Expiry Clear)" else "Upload Product & Expiry Photo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (hasUploadedPhoto) EcoEmeraldDark else MedTextPrimary
                            )
                            Text(
                                text = if (hasUploadedPhoto) "Tap to retake photo • OCR verified batch label" else "Camera snapshot of front label and embossed expiry date",
                                fontSize = 10.sp,
                                color = MedTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (productName.isNotBlank() && batchNumber.isNotBlank() && price > 0) {
                    onSubmit(
                        productName,
                        brandName,
                        category,
                        dosageOrVolume,
                        batchNumber,
                        expiryDate,
                        mrp,
                        price,
                        condition,
                        "https://images.unsplash.com/photo-1556228720-195a672e8a03"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_submit_listing"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoEmeraldDark),
            enabled = productName.isNotBlank() && price > 0
        ) {
            Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "List Product for Verification & Payout",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UserListingCard(
    listing: UserProductListing,
    onApprove: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Listing Product Thumbnail
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MedBackground)
                ) {
                    val imageUrl = listing.photoUrl ?: MedicineImageProvider.getFallbackUrl(listing.category, listing.dosageOrVolume)
                    MedicineImageFromUrl(
                        imageUrl = imageUrl,
                        category = listing.category,
                        dosageForm = listing.dosageOrVolume,
                        cornerRadius = 10.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = listing.brandName.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalBlue
                            )
                            Text(
                                text = listing.productName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${listing.category} • ${listing.dosageOrVolume}",
                                fontSize = 11.sp,
                                color = MedTextSecondary,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (listing.verificationStatus) {
                                "APPROVED" -> EcoEmeraldLight
                                "SOLD" -> MedicalBlueLight
                                else -> ExpiryAmberLight
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (listing.verificationStatus) {
                                    "APPROVED" -> EcoEmerald
                                    "SOLD" -> MedicalBlue
                                    else -> ExpiryAmber
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (listing.verificationStatus) {
                                        "APPROVED" -> Icons.Default.Verified
                                        "SOLD" -> Icons.Default.CheckCircle
                                        else -> Icons.Default.HourglassTop
                                    },
                                    contentDescription = null,
                                    tint = when (listing.verificationStatus) {
                                        "APPROVED" -> EcoEmeraldDark
                                        "SOLD" -> MedicalBlueDark
                                        else -> ExpiryAmberDark
                                    },
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (listing.verificationStatus) {
                                        "APPROVED" -> "Approved"
                                        "SOLD" -> "Sold"
                                        else -> "Review"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (listing.verificationStatus) {
                                        "APPROVED" -> EcoEmeraldDark
                                        "SOLD" -> MedicalBlueDark
                                        else -> ExpiryAmberDark
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batch: ${listing.batchNumber}",
                    fontSize = 11.sp,
                    color = MedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Expires: ${listing.expiryDate}",
                    fontSize = 11.sp,
                    color = MedTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = listing.packageCondition,
                    fontSize = 10.sp,
                    color = EcoEmeraldDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${listing.listingPrice.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "MRP ₹${listing.originalMrp.toInt()}",
                            fontSize = 11.sp,
                            color = MedTextSecondary,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                    Text(
                        text = "Net Payout: ₹${"%.1f".format(listing.estimatedPayout)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoEmeraldDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (listing.verificationStatus == "PENDING_REVIEW") {
                        OutlinedButton(
                            onClick = onApprove,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(text = "Approve (Demo)", fontSize = 10.sp, color = MedicalBlueDark)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ExpiryAmberDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyListingsState(onStartListing: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MedicalBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Sell,
                    contentDescription = null,
                    tint = MedicalBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Products Listed Yet",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MedTextPrimary
            )
            Text(
                text = "Have unneeded sealed sunscreen bottles, unopened ointments, or extra tablet strips? Sell them here for direct MedWallet cash!",
                fontSize = 12.sp,
                color = MedTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                lineHeight = 16.sp
            )
            Button(
                onClick = onStartListing,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "List a Product Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReturnExpiredStripsCard(
    viewModel: StoreViewModel,
    onScheduleSuccess: () -> Unit
) {
    var stripCountText by remember { mutableStateOf("4") }
    var medicineNames by remember { mutableStateOf("Amoxicillin, Cetirizine, Paracetamol") }
    var pickupAddress by remember { mutableStateOf("Flat 402, Kadri Hills, Mangaluru") }
    var selectedSlot by remember { mutableStateOf("Today, 4:00 PM - 6:00 PM") }
    var isSubmitted by remember { mutableStateOf(false) }

    val stripCount = stripCountText.toIntOrNull() ?: 1

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EcoEmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Eco,
                        contentDescription = null,
                        tint = EcoEmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Reverse Logistics Safe Disposal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MedTextPrimary
                    )
                    Text(
                        text = "Schedule doorstep rider pickup for expired medicines",
                        fontSize = 11.sp,
                        color = MedTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EcoEmeraldLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reward: +50 Eco-Karma & Mystery Scratcher",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoEmeraldDark
                    )
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = stripCountText,
                onValueChange = { stripCountText = it },
                label = { Text("Approximate Strip / Bottle Count") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue,
                    unfocusedBorderColor = MedBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = medicineNames,
                onValueChange = { medicineNames = it },
                label = { Text("Names of Expired Medicines") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue,
                    unfocusedBorderColor = MedBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Doorstep Pickup Address (Mangaluru)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue,
                    unfocusedBorderColor = MedBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select Delivery Rider Pickup Slot:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MedTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            listOf("Today, 4:00 PM - 6:00 PM", "Tomorrow Morning, 10:00 AM", "Tomorrow Evening, 5:00 PM").forEach { slot ->
                val isSel = selectedSlot == slot
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) MedicalBlueLight else MedBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) MedicalBlue else MedBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { selectedSlot = slot }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isSel) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (isSel) MedicalBlueDark else MedTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = slot,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) MedicalBlueDark else MedTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.scheduleReverseDisposal(
                        stripCount = stripCount,
                        medicineNames = medicineNames,
                        address = pickupAddress,
                        slot = selectedSlot,
                        onSuccess = {
                            isSubmitted = true
                            onScheduleSuccess()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoEmeraldDark)
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm Free Rider Doorstep Pickup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
