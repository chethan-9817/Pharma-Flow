package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineDonation
import com.example.data.model.NgoPartner
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
fun DonateAndReturnScreen(
    viewModel: StoreViewModel,
    onNavigateToRewards: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val medicineDonations by viewModel.medicineDonations.collectAsState()
    val ecoKarmaPoints by viewModel.ecoKarmaPoints.collectAsState()
    val ngoPartners = viewModel.ngoPartners

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var lastAwardedKarma by remember { mutableIntStateOf(200) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedSlate)
    ) {
        // Top Header
        item {
            Surface(
                color = MedWhite,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = EcoEmeraldLight,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.VolunteerActivism,
                                            contentDescription = null,
                                            tint = EcoEmeraldDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "COMMUNITY HEALTH HUB",
                                        color = EcoEmeraldDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Donate Medicine to NGOs",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MedTextPrimary
                                    )
                                }
                            }
                        }

                        // Karma Wallet Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EcoEmeraldLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { onNavigateToRewards() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = EcoEmeraldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$ecoKarmaPoints Karma",
                                    color = EcoEmeraldDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Don't let surplus unexpired medicines go to waste. Donate sealed blister packs and syrups to nearby verified charitable clinics and earn rewards.",
                        fontSize = 12.sp,
                        color = MedTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Tabs
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
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Donate to NGO",
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
                                text = "My Donations",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            if (medicineDonations.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = EcoEmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${medicineDonations.size}",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                            text = "Safe Return",
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
                // Tab 0: Donate Form with NGO Selector
                item {
                    DonateToNgoSection(
                        ngoPartners = ngoPartners,
                        onSubmitDonation = { medName, brand, cat, units, batch, expiry, condition, ngo, photo ->
                            viewModel.createMedicineDonation(
                                medicineName = medName,
                                brandName = brand,
                                category = cat,
                                dosageOrUnits = units,
                                batchNumber = batch,
                                expiryDate = expiry,
                                packageCondition = condition,
                                recipientNgo = ngo,
                                photoUrl = photo,
                                onSuccess = { points ->
                                    lastAwardedKarma = points
                                    showCelebrationDialog = true
                                    selectedTab = 1
                                }
                            )
                        }
                    )
                }
            }

            1 -> {
                // Tab 1: My Donations & Impact Tracker
                if (medicineDonations.isEmpty()) {
                    item {
                        EmptyDonationsState(onStartDonation = { selectedTab = 0 })
                    }
                } else {
                    item {
                        DonationImpactSummaryCard(
                            totalDonations = medicineDonations.size,
                            totalKarmaEarned = medicineDonations.sumOf { it.karmaPointsEarned },
                            onNavigateToRewards = onNavigateToRewards
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Your Pledged & Delivered Donations",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary
                            )
                        }
                    }

                    items(medicineDonations, key = { it.id }) { donation ->
                        MedicineDonationCard(
                            donation = donation,
                            onVerifyDelivered = { viewModel.markDonationDelivered(donation.id) },
                            onDelete = { viewModel.deleteDonation(donation.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            2 -> {
                // Tab 2: Safe Return & Reverse Logistics
                item {
                    SafeDisposalSection(
                        onSubmitDisposal = { count, meds, addr, slot ->
                            viewModel.scheduleReverseDisposal(count, meds, addr, slot) {
                                selectedTab = 1
                            }
                        }
                    )
                }
            }
        }
    }

    // Celebration modal for completed donation
    if (showCelebrationDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showCelebrationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MedWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EcoEmeraldLight,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = EcoEmeraldDark,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Pledge Confirmed!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MedTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Thank you for saving lives! Your medicine donation has been scheduled for chemist pickup and dispatch to the NGO clinic.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MedTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoEmeraldLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "+$lastAwardedKarma ECO-KARMA CREDITED",
                                color = EcoEmeraldDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "🎁 +1 Bonus Scratch Card Unlocked in Rewards!",
                                color = MedTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            showCelebrationDialog = false
                            onNavigateToRewards()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoEmeraldDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scratch Bonus Gift Card", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DonateToNgoSection(
    ngoPartners: List<NgoPartner>,
    onSubmitDonation: (
        medName: String,
        brand: String,
        cat: String,
        units: String,
        batch: String,
        expiry: String,
        condition: String,
        ngo: NgoPartner,
        photo: String?
    ) -> Unit
) {
    var selectedNgo by remember { mutableStateOf(ngoPartners.firstOrNull() ?: NgoPartner("1", "Snehalaya", "", "", 1.0, "", emptyList())) }
    var medicineName by remember { mutableStateOf("Dolo 650 (Paracetamol)") }
    var brandName by remember { mutableStateOf("Micro Labs Ltd.") }
    var selectedCategory by remember { mutableStateOf("Fever & Pain") }
    var dosageOrUnits by remember { mutableStateOf("2 Strips (20 Tablets)") }
    var batchNumber by remember { mutableStateOf("BATCH-DL921") }
    var expiryDate by remember { mutableStateOf("10/2026") }
    var packageCondition by remember { mutableStateOf("Factory Sealed Blister") }
    var hasUploadedPhoto by remember { mutableStateOf(true) }

    val estimatedPoints = when {
        selectedCategory.contains("Sunscreen", true) || selectedCategory.contains("Skincare", true) -> 250
        dosageOrUnits.contains("Strip", true) || dosageOrUnits.contains("Tab", true) -> 200
        else -> 150
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Step 1: Select Recipient NGO
        Text(
            text = "STEP 1: SELECT NEARBY HEALTHCARE NGO",
            color = MedicalBlue,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Vetted Charitable Clinics & Dispensaries",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MedTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal NGO Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ngoPartners.forEach { ngo ->
                val isSelected = ngo.id == selectedNgo.id
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { selectedNgo = ngo },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) MedicalBlueLight.copy(alpha = 0.4f) else MedWhite),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MedicalBlue else MedBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EcoEmeraldLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.NearMe, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${ngo.distanceKm} km away",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoEmeraldDark
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ngo.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MedTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = ngo.tagline,
                            fontSize = 10.sp,
                            color = MedTextSecondary,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ExpiryAmberLight
                        ) {
                            Text(
                                text = ngo.urgencyTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpiryAmberDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Step 2: Medicine Details
        Text(
            text = "STEP 2: MEDICINE VERIFICATION DETAILS",
            color = MedicalBlue,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Enter Surplus Medicine / Skincare Info",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MedTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine or Skincare Name") },
                    placeholder = { Text("e.g. Dolo 650 / Minimalist SPF 50") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MedBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Manufacturer / Brand") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = dosageOrUnits,
                        onValueChange = { dosageOrUnits = it },
                        label = { Text("Quantity / Pack Size") },
                        placeholder = { Text("2 Strips / 50g Bottle") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry (MM/YYYY)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Condition chips
                Text(text = "Seal Condition:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Factory Sealed Blister", "Unopened Bottle", "Tamper Seal Intact").forEach { cond ->
                        val isSel = packageCondition == cond
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MedicalBlueLight else MedBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MedicalBlue else MedBorder),
                            modifier = Modifier.clickable { packageCondition = cond }
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

                // Photo Verification Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasUploadedPhoto) EcoEmeraldLight.copy(alpha = 0.4f) else MedBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hasUploadedPhoto) EcoEmerald else MedBorder),
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
                                    imageUrl = MedicineImageProvider.getFallbackUrl(selectedCategory, dosageOrUnits),
                                    category = selectedCategory,
                                    dosageForm = dosageOrUnits,
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
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = MedicalBlueLight,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasUploadedPhoto) "Package Photo Verified (Label & Expiry Clear)" else "Take Snapshot of Medicine Pack",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (hasUploadedPhoto) EcoEmeraldDark else MedTextPrimary
                            )
                            Text(
                                text = if (hasUploadedPhoto) "Tap to retake photo • OCR batch verified" else "Clear photo showing sealed strip and expiry date",
                                fontSize = 10.sp,
                                color = MedTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Donor Rewards Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = EcoEmeraldLight,
            border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DONOR REWARDS PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = EcoEmeraldDark
                        )
                        Text(
                            text = "+$estimatedPoints Eco-Karma Points + 1 Gift Scratcher",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (medicineName.isNotBlank() && batchNumber.isNotBlank()) {
                    onSubmitDonation(
                        medicineName,
                        brandName,
                        selectedCategory,
                        dosageOrUnits,
                        batchNumber,
                        expiryDate,
                        packageCondition,
                        selectedNgo,
                        MedicineImageProvider.getFallbackUrl(selectedCategory, dosageOrUnits)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_pledge_donation"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoEmeraldDark),
            enabled = medicineName.isNotBlank() && batchNumber.isNotBlank()
        ) {
            Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pledge Donation to ${selectedNgo.name.take(18)}...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun DonationImpactSummaryCard(
    totalDonations: Int,
    totalKarmaEarned: Int,
    onNavigateToRewards: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YOUR COMMUNITY HEALTH IMPACT",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = EcoEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EcoEmeraldLight
                ) {
                    Text(
                        text = "80G CERTIFIED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoEmeraldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$totalDonations", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MedTextPrimary)
                    Text(text = "Medicines Donated", fontSize = 11.sp, color = MedTextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "+$totalKarmaEarned", fontSize = 22.sp, fontWeight = FontWeight.Black, color = EcoEmeraldDark)
                    Text(text = "Karma Points Earned", fontSize = 11.sp, color = MedTextSecondary)
                }
            }
        }
    }
}

@Composable
fun MedicineDonationCard(
    donation: MedicineDonation,
    onVerifyDelivered: () -> Unit,
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
                // Photo Thumbnail
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MedSlate)
                ) {
                    MedicineImageFromUrl(
                        imageUrl = donation.photoUrl ?: MedicineImageProvider.getFallbackUrl(donation.category, donation.dosageOrUnits),
                        category = donation.category,
                        dosageForm = donation.dosageOrUnits,
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
                                text = donation.brandName.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalBlue
                            )
                            Text(
                                text = donation.medicineName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${donation.category} • ${donation.dosageOrUnits}",
                                fontSize = 11.sp,
                                color = MedTextSecondary,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }

                        // Status Badge
                        val isDelivered = donation.status == "DELIVERED_TO_NGO"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDelivered) EcoEmeraldLight else ExpiryAmberLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDelivered) EcoEmerald else ExpiryAmber)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDelivered) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isDelivered) EcoEmeraldDark else ExpiryAmberDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDelivered) "Delivered to NGO" else "Chemist Transit",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDelivered) EcoEmeraldDark else ExpiryAmberDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // NGO and Batch metadata row
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MedBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recipient: ${donation.recipientNgoName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MedTextPrimary
                            )
                        }
                        Text(
                            text = "+${donation.karmaPointsEarned} Karma",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoEmeraldDark
                        )
                    }
                    Text(
                        text = "Batch: ${donation.batchNumber} • Expiry: ${donation.expiryDate} • ${donation.packageCondition}",
                        fontSize = 10.sp,
                        color = MedTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (donation.status == "DELIVERED_TO_NGO") "Receipt verified by clinic doctor" else "Free doorstep pickup by MedCourier",
                    fontSize = 11.sp,
                    color = MedTextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (donation.status != "DELIVERED_TO_NGO") {
                        OutlinedButton(
                            onClick = onVerifyDelivered,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Simulate Delivery", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MedTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDonationsState(onStartDonation: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = EcoEmeraldLight,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = EcoEmeraldDark,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Medicine Donations Yet",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MedTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pledge your unused, sealed medicines to nearby community free clinics and unlock instant gift scratch cards.",
                fontSize = 12.sp,
                color = MedTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStartDonation,
                colors = ButtonDefaults.buttonColors(containerColor = EcoEmeraldDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Donate to Nearby NGO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SafeDisposalSection(
    onSubmitDisposal: (stripCount: Int, meds: String, address: String, slot: String) -> Unit
) {
    var stripCount by remember { mutableStateOf("3") }
    var medicineNames by remember { mutableStateOf("Expired Paracetamol & Cough Syrup") }
    var pickupAddress by remember { mutableStateOf("Flat 402, Kadri Hills, Mangaluru") }
    var pickupSlot by remember { mutableStateOf("Tomorrow, 10:00 AM - 1:00 PM") }
    var showSuccess by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, MedBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color(0xFFE0F2FE), modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "REVERSE LOGISTICS SAFE RETURN", color = Color(0xFF0284C7), fontWeight = FontWeight.Black, fontSize = 11.sp)
                        Text(text = "Safe Medicine Incineration", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedTextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Expired medicines must not be flushed or thrown in household trash. Schedule a certified chemist courier to pick them up for scientific high-temperature incineration.",
                    fontSize = 12.sp,
                    color = MedTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = stripCount,
                    onValueChange = { stripCount = it },
                    label = { Text("Number of Strips / Bottles") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0284C7), unfocusedBorderColor = MedBorder)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = medicineNames,
                    onValueChange = { medicineNames = it },
                    label = { Text("Expired Medicine Names") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0284C7), unfocusedBorderColor = MedBorder)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pickupAddress,
                    onValueChange = { pickupAddress = it },
                    label = { Text("Doorstep Pickup Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0284C7), unfocusedBorderColor = MedBorder)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val count = stripCount.toIntOrNull() ?: 1
                        onSubmitDisposal(count, medicineNames, pickupAddress, pickupSlot)
                        showSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Schedule Free Disposal Courier (+50 Karma)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (showSuccess) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EcoEmeraldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pickup confirmed for tomorrow! Rider Ramesh K. assigned.",
                            color = EcoEmeraldDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}
