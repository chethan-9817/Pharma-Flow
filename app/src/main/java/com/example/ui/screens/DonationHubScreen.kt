package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MedicineDonation
import com.example.data.model.NgoPartner
import com.example.ui.components.ConfettiEffect
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
import com.example.ui.theme.MedSurfaceElevated
import com.example.ui.theme.MedTextMuted
import com.example.ui.theme.MedTextPrimary
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalNavy
import com.example.ui.viewmodel.StoreViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DonationHubScreen(
    viewModel: StoreViewModel,
    onNavigateToRewards: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val medicineDonations by viewModel.medicineDonations.collectAsState()
    val ecoKarmaPoints by viewModel.ecoKarmaPoints.collectAsState()
    val ngoPartners = viewModel.ngoPartners

    var activeTab by remember { mutableIntStateOf(0) }
    var celebrationKarmaAwarded by remember { mutableIntStateOf(0) }
    var showCelebrationModal by remember { mutableStateOf(false) }

    // Pre-selected NGO from collection points view
    var activeRecipientNgo by remember {
        mutableStateOf(ngoPartners.firstOrNull() ?: NgoPartner("ngo_1", "Snehalaya Charitable Healthcare", "", "", 1.4, "", emptyList()))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedSlate)
            .testTag("donation_hub_screen")
    ) {
        // Hero Header & Community Impact Banner
        item {
            DonationHubHeader(
                ecoKarmaPoints = ecoKarmaPoints,
                donatedCount = medicineDonations.size,
                onNavigateToRewards = onNavigateToRewards
            )
        }

        // Segmented Navigation Tabs
        item {
            DonationHubTabs(
                activeTab = activeTab,
                onTabSelect = { activeTab = it },
                donationCount = medicineDonations.size,
                ngoCount = ngoPartners.size
            )
        }

        when (activeTab) {
            0 -> {
                // Tab 0: Main Donation Flow (Input Medicine Details + Upload Photo + Select NGO)
                item {
                    DonationInputFormSection(
                        ngoPartners = ngoPartners,
                        selectedNgo = activeRecipientNgo,
                        onSelectNgo = { activeRecipientNgo = it },
                        onExploreNgosClick = { activeTab = 1 },
                        onSubmitDonation = { medName, brand, cat, units, batch, expiry, cond, ngo, photoUrl ->
                            viewModel.createMedicineDonation(
                                medicineName = medName,
                                brandName = brand,
                                category = cat,
                                dosageOrUnits = units,
                                batchNumber = batch,
                                expiryDate = expiry,
                                packageCondition = cond,
                                recipientNgo = ngo,
                                photoUrl = photoUrl,
                                onSuccess = { points ->
                                    celebrationKarmaAwarded = points
                                    showCelebrationModal = true
                                    activeTab = 2 // Move to My Donations view
                                }
                            )
                        }
                    )
                }
            }

            1 -> {
                // Tab 1: Interactive Nearby NGO Collection Points Directory & Map Guide
                item {
                    NearbyNgoCollectionPointsDirectory(
                        ngoPartners = ngoPartners,
                        selectedNgo = activeRecipientNgo,
                        onChooseNgoForDonation = { chosen ->
                            activeRecipientNgo = chosen
                            activeTab = 0 // Return to donation form with pre-selected NGO
                        }
                    )
                }
            }

            2 -> {
                // Tab 2: My Pledged & Delivered Donations Tracker
                item {
                    MyDonationsTrackingSection(
                        donations = medicineDonations,
                        onDonateMore = { activeTab = 0 },
                        onNavigateToRewards = onNavigateToRewards
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Celebration modal when donation is successfully pledged
    if (showCelebrationModal) {
        DonationCelebrationDialog(
            karmaPoints = celebrationKarmaAwarded,
            ngoName = activeRecipientNgo.name,
            onDismiss = { showCelebrationModal = false },
            onViewRewards = {
                showCelebrationModal = false
                onNavigateToRewards()
            }
        )
    }
}

@Composable
private fun DonationHubHeader(
    ecoKarmaPoints: Int,
    donatedCount: Int,
    onNavigateToRewards: () -> Unit
) {
    Surface(
        color = MedWhite,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(EcoEmerald, MedicalBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Donation Hub",
                            tint = MedWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ZERO-WASTE DONATION HUB",
                                color = EcoEmeraldDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EcoEmeraldLight
                            ) {
                                Text(
                                    text = "100% NGO DIRECT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoEmeraldDark,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Donate Surplus Medicine",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTextPrimary
                        )
                    }
                }

                // Karma balance pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EcoEmeraldLight,
                    border = BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { onNavigateToRewards() }
                        .testTag("donation_hub_karma_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EcoEmeraldDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Column {
                            Text(
                                text = "$ecoKarmaPoints Karma",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EcoEmeraldDark
                            )
                            Text(
                                text = "Rewards Hub ›",
                                fontSize = 9.sp,
                                color = EcoEmeraldDark.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle banner with impact description
            Text(
                text = "Donate unused, unexpired, sealed medicines to verified local charities and free clinics. Every verified strip earns tiered Karma points and bonus Scratch Cards.",
                fontSize = 12.sp,
                color = MedTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Trust checklist banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedicalBlueLight.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadge(icon = Icons.Default.Verified, label = "80G Certified NGOs")
                TrustBadge(icon = Icons.Default.LocalShipping, label = "Free Doorstep Courier")
                TrustBadge(icon = Icons.Default.CardGiftcard, label = "Instant Scratch Bonus")
            }
        }
    }
}

@Composable
private fun TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MedicalBlueDark,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MedicalBlueDark
        )
    }
}

@Composable
private fun DonationHubTabs(
    activeTab: Int,
    onTabSelect: (Int) -> Unit,
    donationCount: Int,
    ngoCount: Int
) {
    TabRow(
        selectedTabIndex = activeTab,
        containerColor = MedWhite,
        contentColor = MedicalBlue,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                color = MedicalBlue,
                height = 3.dp
            )
        }
    ) {
        Tab(
            selected = activeTab == 0,
            onClick = { onTabSelect(0) },
            modifier = Modifier.testTag("tab_donate_form"),
            text = {
                Text(
                    text = "Donate Medicine",
                    fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        )

        Tab(
            selected = activeTab == 1,
            onClick = { onTabSelect(1) },
            modifier = Modifier.testTag("tab_ngo_collection_points"),
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NGO Points",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = CircleShape,
                        color = if (activeTab == 1) MedicalBlue else MedSurfaceElevated,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$ngoCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) MedWhite else MedTextSecondary
                            )
                        }
                    }
                }
            }
        )

        Tab(
            selected = activeTab == 2,
            onClick = { onTabSelect(2) },
            modifier = Modifier.testTag("tab_my_donations"),
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My Donations",
                        fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    if (donationCount > 0) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface(
                            shape = CircleShape,
                            color = EcoEmeraldDark,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$donationCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedWhite
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

// ======================================================================================
// TAB 0: MAIN DONATION FORM (MEDICINE INPUT + PHOTO UPLOAD + NGO PICKER)
// ======================================================================================

@Composable
private fun DonationInputFormSection(
    ngoPartners: List<NgoPartner>,
    selectedNgo: NgoPartner,
    onSelectNgo: (NgoPartner) -> Unit,
    onExploreNgosClick: () -> Unit,
    onSubmitDonation: (
        medName: String,
        brand: String,
        cat: String,
        units: String,
        batch: String,
        expiry: String,
        cond: String,
        ngo: NgoPartner,
        photoUrl: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form inputs
    var medicineName by remember { mutableStateOf("Dolo 650 (Paracetamol)") }
    var brandName by remember { mutableStateOf("Micro Labs Ltd.") }
    var selectedCategory by remember { mutableStateOf("Fever & Pain") }
    var dosageOrUnits by remember { mutableStateOf("2 Strips (20 Tablets)") }
    var batchNumber by remember { mutableStateOf("BATCH-DL921") }
    var expiryDate by remember { mutableStateOf("11/2026") }
    var packageCondition by remember { mutableStateOf("Factory Sealed Blister") }
    var storageRequirement by remember { mutableStateOf("Room Temperature (<25°C)") }
    var donorNotes by remember { mutableStateOf("") }
    var deliveryMethod by remember { mutableStateOf("DOORSTEP_PICKUP") } // or DROP_OFF

    // Photo Upload State
    var uploadedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedPhotoFallbackUrl by remember {
        mutableStateOf<String?>(MedicineImageProvider.getFallbackUrl("Fever & Pain", "Tablets"))
    }
    var isOcrScanning by remember { mutableStateOf(false) }
    var ocrScanCompleted by remember { mutableStateOf(false) }

    // Quick Medicine templates for easy one-tap filling
    val quickMedicineTemplates = listOf(
        QuickMedicineTemplate("Dolo 650", "Micro Labs", "Fever & Pain", "2 Strips (20 Tablets)", "DL-9218", "12/2026", "Factory Sealed Blister"),
        QuickMedicineTemplate("Augmentin 625", "GSK", "Antibiotics", "1 Strip (10 Tablets)", "AUG-440", "08/2026", "Factory Sealed Blister"),
        QuickMedicineTemplate("Azithral 500", "Alembic", "Antibiotics", "1 Strip (5 Tablets)", "AZ-1029", "09/2026", "Factory Sealed Blister"),
        QuickMedicineTemplate("Shelcal 500", "Torrent Pharma", "Vitamins & Calcium", "15 Tablets Bottle", "SH-881", "10/2026", "Unopened Bottle"),
        QuickMedicineTemplate("Volini Pain Gel", "Sun Pharma", "First Aid", "50g Tube", "VOL-731", "04/2027", "Tamper Seal Intact"),
        QuickMedicineTemplate("Derma Co SPF 50", "Derma Co", "Skincare & Sunscreen", "50ml Bottle", "DER-229", "06/2027", "Factory Sealed Box")
    )

    // Android Photo Picker Launcher (Zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedPhotoUri = uri
            uploadedPhotoFallbackUrl = null
            ocrScanCompleted = false
        }
    }

    // Dynamic points calculation
    val estimatedPoints = remember(selectedCategory, dosageOrUnits, uploadedPhotoUri, uploadedPhotoFallbackUrl) {
        var base = 200
        if (selectedCategory.contains("Sunscreen", true) || selectedCategory.contains("Skincare", true)) base += 50
        if (selectedCategory.contains("Antibiotics", true)) base += 50
        if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) base += 50
        base
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // -------------------------------------------------------------
        // SECTION 1: SELECTED RECIPIENT NGO & COLLECTION POINT
        // -------------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 1 OF 3: SELECT RECIPIENT NGO",
                    color = MedicalBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Charitable Collection Center",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MedTextPrimary
                )
            }

            Text(
                text = "View All (${ngoPartners.size}) ›",
                color = MedicalBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { onExploreNgosClick() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal NGO Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ngoPartners) { ngo ->
                val isSelected = ngo.id == selectedNgo.id
                NgoSelectionMiniCard(
                    ngo = ngo,
                    isSelected = isSelected,
                    onClick = { onSelectNgo(ngo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -------------------------------------------------------------
        // SECTION 2: MEDICINE DETAILS FORM
        // -------------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 2 OF 3: MEDICINE DETAILS",
                    color = MedicalBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Enter Surplus Medicine Info",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MedTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoEmeraldLight
            ) {
                Text(
                    text = "Earns +$estimatedPoints Karma",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoEmeraldDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick template prefill chips
        Text(
            text = "Quick pre-fill common donations:",
            fontSize = 11.sp,
            color = MedTextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickMedicineTemplates.forEach { t ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MedWhite,
                    border = BorderStroke(1.dp, MedBorder),
                    modifier = Modifier.clickable {
                        medicineName = t.name
                        brandName = t.brand
                        selectedCategory = t.category
                        dosageOrUnits = t.dosage
                        batchNumber = t.batch
                        expiryDate = t.expiry
                        packageCondition = t.condition
                        uploadedPhotoFallbackUrl = MedicineImageProvider.getFallbackUrl(t.category, t.dosage)
                    }
                ) {
                    Text(
                        text = "+ ${t.name}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedicalBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            border = BorderStroke(1.dp, MedBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Medicine Name
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine or Product Name") },
                    placeholder = { Text("e.g. Dolo 650 / Augmentin 625 Duo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MedBorder
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalBlue)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brand & Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Manufacturer / Brand") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("brand_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = dosageOrUnits,
                        onValueChange = { dosageOrUnits = it },
                        label = { Text("Quantity / Strips") },
                        placeholder = { Text("e.g. 2 Strips") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dosage_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Batch Number & Expiry Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number") },
                        placeholder = { Text("e.g. BATCH-981") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("batch_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        )
                    )

                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry (MM/YYYY)") },
                        placeholder = { Text("11/2026") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expiry_date_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalBlue,
                            unfocusedBorderColor = MedBorder
                        ),
                        trailingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Valid Expiry", tint = EcoEmeraldDark, modifier = Modifier.size(18.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Packaging Seal Condition Selector
                Text(
                    text = "Packaging Seal Condition (Charity Standard):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Factory Sealed Blister", "Unopened Bottle", "Intact Foil Strips", "Tamper Seal Intact").forEach { cond ->
                        val isSelected = packageCondition == cond
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MedicalBlueLight else MedBackground,
                            border = BorderStroke(1.dp, if (isSelected) MedicalBlue else MedBorder),
                            modifier = Modifier.clickable { packageCondition = cond }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = cond,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MedicalBlueDark else MedTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage condition requirement
                Text(
                    text = "Storage Requirement:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Room Temperature (<25°C)", "Refrigerated (2-8°C)", "Cool & Dry").forEach { req ->
                        val isSel = storageRequirement == req
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) EcoEmeraldLight else MedBackground,
                            border = BorderStroke(1.dp, if (isSel) EcoEmerald else MedBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { storageRequirement = req }
                        ) {
                            Text(
                                text = req,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) EcoEmeraldDark else MedTextSecondary,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Donor Remarks (optional)
                OutlinedTextField(
                    value = donorNotes,
                    onValueChange = { donorNotes = it },
                    label = { Text("Special notes for charity clinic (Optional)") },
                    placeholder = { Text("e.g. Stored in climate-controlled cabinet, sealed pack") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MedBorder
                    ),
                    maxLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -------------------------------------------------------------
        // SECTION 3: PHOTO OF MEDICINE LABEL UPLOAD & OCR VERIFICATION
        // -------------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 3 OF 3: MEDICINE LABEL PHOTO",
                    color = MedicalBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Upload Packaging / Label Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MedTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) EcoEmeraldLight else ExpiryAmberLight
            ) {
                Text(
                    text = if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) "Photo Attached ✓" else "Photo Recommended",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) EcoEmeraldDark else ExpiryAmberDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Medicine Label Upload Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            border = BorderStroke(
                1.dp,
                if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) EcoEmerald else MedBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("medicine_photo_upload_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (uploadedPhotoUri != null || uploadedPhotoFallbackUrl != null) {
                    // Preview of uploaded label photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedSlate)
                    ) {
                        if (uploadedPhotoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(uploadedPhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Medicine label photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            MedicineImageFromUrl(
                                imageUrl = uploadedPhotoFallbackUrl ?: "",
                                category = selectedCategory,
                                dosageForm = dosageOrUnits,
                                cornerRadius = 12.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Gradient Scrim
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                        startY = 100f
                                    )
                                )
                        )

                        // Top Badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EcoEmeraldDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedWhite, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Label Attached",
                                        color = MedWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Remove button
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        uploadedPhotoUri = null
                                        uploadedPhotoFallbackUrl = null
                                        ocrScanCompleted = false
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = MedWhite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Bottom Action: Auto OCR Scan Simulation
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Medicine Label Verified",
                                color = MedWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Batch: $batchNumber | Exp: $expiryDate",
                                color = Color(0xFFD1FAE5),
                                fontSize = 11.sp
                            )
                        }

                        // OCR laser scan animation line when scanning
                        if (isOcrScanning) {
                            val infiniteTransition = rememberInfiniteTransition()
                            val scanProgress by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .padding(top = (scanProgress * 160).dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color.Transparent, EcoEmerald, Color.White, EcoEmerald, Color.Transparent)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons below photo: Scan with OCR + Retake
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (isOcrScanning) return@OutlinedButton
                                isOcrScanning = true
                                coroutineScope.launch {
                                    delay(1200)
                                    isOcrScanning = false
                                    ocrScanCompleted = true
                                    // Simulated OCR detection result
                                    batchNumber = "BATCH-DL921"
                                    expiryDate = "11/2026"
                                }
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .testTag("ocr_scan_label_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (ocrScanCompleted) EcoEmerald else MedicalBlue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (ocrScanCompleted) EcoEmeraldDark else MedicalBlue
                            )
                        ) {
                            if (isOcrScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MedicalBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scanning Label...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = if (ocrScanCompleted) Icons.Default.CheckCircle else Icons.Default.DocumentScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (ocrScanCompleted) "Label Verified ✓" else "Auto-Fill via OCR Scan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MedSurfaceElevated,
                                contentColor = MedTextPrimary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retake", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    // Empty state: Tap to upload
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedSlate)
                            .border(BorderStroke(1.5.dp, MedicalBlue.copy(alpha = 0.35f)), RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MedicalBlueLight,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload Medicine Label",
                                    tint = MedicalBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Take Photo or Upload Medicine Label",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MedTextPrimary
                        )

                        Text(
                            text = "Capturing batch number & expiry date enables instant 1-tap verification and unlocks +50 bonus Karma points.",
                            fontSize = 11.sp,
                            color = MedTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MedicalBlue,
                                contentColor = MedWhite
                            ),
                            modifier = Modifier.testTag("pick_photo_button")
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Gallery / Camera", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick sample demo photo options for emulator
                        Text(
                            text = "Or attach sample verified label:",
                            fontSize = 10.sp,
                            color = MedTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MedWhite,
                                border = BorderStroke(1.dp, MedBorder),
                                modifier = Modifier.clickable {
                                    uploadedPhotoFallbackUrl = MedicineImageProvider.getFallbackUrl("Fever & Pain", "Tablets")
                                }
                            ) {
                                Text(
                                    text = "Blister Pack Label",
                                    fontSize = 10.sp,
                                    color = MedicalBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MedWhite,
                                border = BorderStroke(1.dp, MedBorder),
                                modifier = Modifier.clickable {
                                    uploadedPhotoFallbackUrl = MedicineImageProvider.getFallbackUrl("Respiratory", "Syrup")
                                }
                            ) {
                                Text(
                                    text = "Syrup Bottle Label",
                                    fontSize = 10.sp,
                                    color = MedicalBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Verification criteria summary
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedSurfaceElevated)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LabelCriteriaRow(isMet = expiryDate.isNotBlank(), text = "Clear Expiry Date printed & unexpired")
                    LabelCriteriaRow(isMet = batchNumber.isNotBlank(), text = "Batch number matches pharmaceutical standard")
                    LabelCriteriaRow(isMet = packageCondition.isNotBlank(), text = "Sealed protective packaging confirmed")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -------------------------------------------------------------
        // SECTION 4: DELIVERY / DROP-OFF METHOD
        // -------------------------------------------------------------
        Text(
            text = "HOW WOULD YOU LIKE TO DELIVER?",
            color = MedicalBlue,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeliveryMethodOption(
                title = "Doorstep Courier",
                subtitle = "Free rider pickup within 2 hours",
                icon = Icons.Default.LocalShipping,
                isSelected = deliveryMethod == "DOORSTEP_PICKUP",
                modifier = Modifier.weight(1f),
                onClick = { deliveryMethod = "DOORSTEP_PICKUP" }
            )

            DeliveryMethodOption(
                title = "Drop at NGO",
                subtitle = "Self drop-off at ${selectedNgo.name.take(16)}...",
                icon = Icons.Default.Place,
                isSelected = deliveryMethod == "DROP_OFF",
                modifier = Modifier.weight(1f),
                onClick = { deliveryMethod = "DROP_OFF" }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -------------------------------------------------------------
        // SECTION 5: FINAL CONFIRMATION & DONATION SUBMIT
        // -------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL IMPACT REWARDS",
                            color = EcoEmerald,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "+$estimatedPoints Eco Karma Points",
                            color = MedWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ExpiryAmber.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, ExpiryAmber)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = ExpiryAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "1x Bonus Scratch Card",
                                color = ExpiryAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Recipient: ${selectedNgo.name} (${selectedNgo.location}) • Distance: ${selectedNgo.distanceKm} km",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val photoToPass = uploadedPhotoUri?.toString() ?: uploadedPhotoFallbackUrl
                        onSubmitDonation(
                            medicineName,
                            brandName,
                            selectedCategory,
                            dosageOrUnits,
                            batchNumber,
                            expiryDate,
                            packageCondition,
                            selectedNgo,
                            photoToPass
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_donation_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoEmerald,
                        contentColor = MedWhite
                    )
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm & Pledge Medicine Donation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelCriteriaRow(isMet: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isMet) EcoEmeraldDark else ExpiryAmberDark,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isMet) MedTextPrimary else MedTextSecondary
        )
    }
}

@Composable
private fun DeliveryMethodOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MedicalBlueLight else MedWhite,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MedicalBlue else MedBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MedicalBlueDark else MedTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MedicalBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isSelected) MedicalBlueDark else MedTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MedTextSecondary,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun NgoSelectionMiniCard(
    ngo: NgoPartner,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() }
            .testTag("ngo_mini_card_${ngo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MedicalBlueLight.copy(alpha = 0.5f) else MedWhite
        ),
        border = BorderStroke(
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
                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MedicalBlue, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ngo.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MedTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = ngo.location,
                fontSize = 10.sp,
                color = MedTextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ExpiryAmberLight
            ) {
                Text(
                    text = ngo.urgencyTag.take(28) + if (ngo.urgencyTag.length > 28) "..." else "",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpiryAmberDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1
                )
            }
        }
    }
}

// ======================================================================================
// TAB 1: NEARBY NGO COLLECTION POINTS DIRECTORY & MAP GUIDE
// ======================================================================================

@Composable
private fun NearbyNgoCollectionPointsDirectory(
    ngoPartners: List<NgoPartner>,
    selectedNgo: NgoPartner,
    onChooseNgoForDonation: (NgoPartner) -> Unit
) {
    var filterCategory by remember { mutableStateOf("ALL") }

    val filteredNgos = remember(filterCategory, ngoPartners) {
        when (filterCategory) {
            "CLOSEST" -> ngoPartners.sortedBy { it.distanceKm }
            "FEVER" -> ngoPartners.filter { it.neededCategories.any { c -> c.contains("Fever", true) } }
            "ANTIBIOTICS" -> ngoPartners.filter { it.neededCategories.any { c -> c.contains("Antibiotics", true) } }
            "VITAMINS" -> ngoPartners.filter { it.neededCategories.any { c -> c.contains("Vitamins", true) } }
            else -> ngoPartners
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Directory Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COMMUNITY CLINICS & DROP CENTERS",
                    color = MedicalBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Nearby NGO Collection Points",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MedTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MedicalBlueLight
            ) {
                Text(
                    text = "Mangaluru Region",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalBlueDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Map Representation Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Collection Grid",
                            color = MedWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "4 Centers Active",
                        fontSize = 11.sp,
                        color = EcoEmeraldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "All collection points are equipped with temperature-regulated storage, pharmacist quality check stations, and Govt. 80G tax exemption verification.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Map landmarks row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MapPointPill(name = "Kodialbail", distance = "2.3 km", isSelected = selectedNgo.location.contains("Kodialbail"))
                    MapPointPill(name = "Kadri Hills", distance = "3.1 km", isSelected = selectedNgo.location.contains("Kadri"))
                    MapPointPill(name = "Kankanady", distance = "4.2 km", isSelected = selectedNgo.location.contains("Kankanady"))
                    MapPointPill(name = "Border", distance = "1.4 km", isSelected = selectedNgo.location.contains("Border"))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NgoFilterChip(label = "All Clinics", isSelected = filterCategory == "ALL", onClick = { filterCategory = "ALL" })
            NgoFilterChip(label = "Closest Distance", isSelected = filterCategory == "CLOSEST", onClick = { filterCategory = "CLOSEST" })
            NgoFilterChip(label = "Fever & Pain Needs", isSelected = filterCategory == "FEVER", onClick = { filterCategory = "FEVER" })
            NgoFilterChip(label = "Antibiotics Needs", isSelected = filterCategory == "ANTIBIOTICS", onClick = { filterCategory = "ANTIBIOTICS" })
            NgoFilterChip(label = "Vitamins & Chronic", isSelected = filterCategory == "VITAMINS", onClick = { filterCategory = "VITAMINS" })
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Full Detailed NGO Cards
        filteredNgos.forEach { ngo ->
            val isSelected = ngo.id == selectedNgo.id
            NgoDetailedPointCard(
                ngo = ngo,
                isSelected = isSelected,
                onSelect = { onChooseNgoForDonation(ngo) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MapPointPill(name: String, distance: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) EcoEmerald else Color(0xFF334155),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isSelected) MedicalNavy else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = name, fontSize = 9.sp, color = MedWhite, fontWeight = FontWeight.Bold)
        Text(text = distance, fontSize = 8.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun NgoFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MedicalBlue else MedWhite,
        border = BorderStroke(1.dp, if (isSelected) MedicalBlue else MedBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MedWhite else MedTextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun NgoDetailedPointCard(
    ngo: NgoPartner,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MedicalBlueLight.copy(alpha = 0.25f) else MedWhite),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MedicalBlue else MedBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ngo_card_${ngo.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Distance + Reg badge
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
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${ngo.distanceKm} km from you",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoEmeraldDark
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedicalBlueLight
                ) {
                    Text(
                        text = ngo.verifiedBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalBlueDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = ngo.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MedTextPrimary
            )

            Text(
                text = ngo.tagline,
                fontSize = 12.sp,
                color = MedTextSecondary,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            // Location address & hours
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = MedicalBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ngo.location,
                    fontSize = 11.sp,
                    color = MedTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Collection Box Open: 8:30 AM - 7:00 PM (Mon-Sat)",
                    fontSize = 11.sp,
                    color = MedTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Urgent needs alert banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ExpiryAmberLight,
                border = BorderStroke(1.dp, ExpiryAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = ExpiryAmberDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ngo.urgencyTag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpiryAmberDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categories needed tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ngo.neededCategories.forEach { cat ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MedSurfaceElevated
                    ) {
                        Text(
                            text = cat,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MedTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selection button
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) EcoEmeraldDark else MedicalBlue,
                    contentColor = MedWhite
                )
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Selected Recipient • Click to Donate", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Donate to this Clinic", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ======================================================================================
// TAB 2: MY DONATIONS & TRACKING SECTION
// ======================================================================================

@Composable
private fun MyDonationsTrackingSection(
    donations: List<MedicineDonation>,
    onDonateMore: () -> Unit,
    onNavigateToRewards: () -> Unit
) {
    val totalKarma = donations.sumOf { it.karmaPointsEarned }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Impact Metric Cards
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "YOUR COMMUNITY HEALTH IMPACT",
                    color = EcoEmerald,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ImpactStatColumn(label = "Medicines Donated", value = "${donations.size}", icon = Icons.Default.LocalPharmacy)
                    ImpactStatColumn(label = "Karma Earned", value = "$totalKarma", icon = Icons.Default.AutoAwesome)
                    ImpactStatColumn(label = "Patients Helped", value = "${donations.size * 3}", icon = Icons.Default.Favorite)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToRewards,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoEmerald,
                        contentColor = MedWhite
                    )
                ) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Redeem Karma in Rewards Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Donation Pledges & Deliveries (${donations.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MedTextPrimary
            )

            Text(
                text = "+ New Donation",
                color = MedicalBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { onDonateMore() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (donations.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedWhite),
                border = BorderStroke(1.dp, MedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EcoEmeraldLight,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "No Donations Pledged Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MedTextPrimary
                    )

                    Text(
                        text = "Turn your unexpired unused medicines into hope for patients in need.",
                        fontSize = 12.sp,
                        color = MedTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDonateMore,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                    ) {
                        Text("Make Your First Donation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            donations.forEach { donation ->
                DonationPledgeCard(donation = donation)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ImpactStatColumn(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = EcoEmerald, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MedWhite)
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun DonationPledgeCard(donation: MedicineDonation) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedWhite),
        border = BorderStroke(1.dp, MedBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (donation.status) {
                        "DELIVERED_TO_NGO" -> EcoEmeraldLight
                        "COURIER_ASSIGNED" -> MedicalBlueLight
                        else -> ExpiryAmberLight
                    }
                ) {
                    Text(
                        text = when (donation.status) {
                            "DELIVERED_TO_NGO" -> "Delivered to Clinic ✓"
                            "COURIER_ASSIGNED" -> "Rider Assigned"
                            else -> "Pledge Active"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (donation.status) {
                            "DELIVERED_TO_NGO" -> EcoEmeraldDark
                            "COURIER_ASSIGNED" -> MedicalBlueDark
                            else -> ExpiryAmberDark
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "+${donation.karmaPointsEarned} Karma",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = EcoEmeraldDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = donation.medicineName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MedTextPrimary
            )

            Text(
                text = "${donation.brandName} • ${donation.dosageOrUnits} • Batch: ${donation.batchNumber}",
                fontSize = 11.sp,
                color = MedTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Recipient: ${donation.recipientNgoName} (${donation.recipientNgoLocation})",
                fontSize = 11.sp,
                color = MedicalBlueDark,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Visual tracking timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimelineSegment(title = "Pledged", isDone = true, modifier = Modifier.weight(1f))
                TimelineSegment(title = "Verified", isDone = donation.status != "PLEDGED", modifier = Modifier.weight(1f))
                TimelineSegment(title = "At Clinic", isDone = donation.status == "DELIVERED_TO_NGO", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TimelineSegment(title: String, isDone: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isDone) EcoEmerald else MedBorder)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            color = if (isDone) EcoEmeraldDark else MedTextMuted,
            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ======================================================================================
// CELEBRATION MODAL (CONFIRMATION + SCRATCH CARD REVEAL)
// ======================================================================================

@Composable
private fun DonationCelebrationDialog(
    karmaPoints: Int,
    ngoName: String,
    onDismiss: () -> Unit,
    onViewRewards: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MedWhite),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ConfettiEffect(modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EcoEmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = EcoEmeraldDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Medicine Donation Pledged!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Redirected to $ngoName for patients who need it most.",
                        fontSize = 12.sp,
                        color = MedTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Karma Points Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoEmeraldLight,
                        border = BorderStroke(1.dp, EcoEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "EARNED REWARDS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = EcoEmeraldDark)
                                Text(text = "+$karmaPoints Karma Points", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EcoEmeraldDark)
                            }
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EcoEmeraldDark, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bonus Scratch Card Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ExpiryAmberLight,
                        border = BorderStroke(1.dp, ExpiryAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = ExpiryAmberDark, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Bonus Scratch Card Unlocked!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpiryAmberDark)
                                Text(text = "Scratch to reveal pharmacy cashback & discounts", fontSize = 10.sp, color = ExpiryAmberDark.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onViewRewards,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalNavy)
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scratch Bonus Card Now", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MedBorder)
                    ) {
                        Text("Back to Donation Hub", color = MedTextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private data class QuickMedicineTemplate(
    val name: String,
    val brand: String,
    val category: String,
    val dosage: String,
    val batch: String,
    val expiry: String,
    val condition: String
)
