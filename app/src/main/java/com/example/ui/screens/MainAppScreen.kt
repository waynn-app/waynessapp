package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.db.CommunityPost
import com.example.db.RewardItem
import com.example.db.UserProfile
import com.example.ui.components.WaynessLogo
import com.example.viewmodel.AuthUiState
import com.example.viewmodel.WaynessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: WaynessViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showSplashFinished by remember { mutableStateOf(false) }

    // Start with Landing Carousel first if not completed, then Login, then Scaffold
    if (!showSplashFinished) {
        LandingCarousel(onFinished = { showSplashFinished = true })
    } else {
        when (val state = authState) {
            is AuthUiState.SignedOut -> {
                LoginSignupScreen(
                    onLoginSuccess = { email, name ->
                        viewModel.loginWithGoogle(email, name)
                    }
                )
            }
            is AuthUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF120E21)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WaynessLogo(size = 140.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(color = Color(0xFFE91E63))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Conectando de forma segura...",
                            color = Color.White.copy(0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            is AuthUiState.SignedIn -> {
                MainFeedScaffold(viewModel = viewModel, userProfile = userProfile)
            }
        }
    }
}

// 1. Landing Welcome Carousel
@Composable
fun LandingCarousel(onFinished: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val carouselTexts = listOf(
        "El modo mejor para obtener lo que realmente quieres" to "WAYNESS te acompaña a motivarte y equilibrar tus hábitos diarios mediante recompensas reales.",
        "Monitorea tu Esfuerzo en Tiempo Real" to "Suma WPoints al entrenar en las zonas de ritmo cardíaco adecuadas. ¡Tu pulso cardíaco es tu valioso combustible!",
        "La Comunidad que te Impulsa" to "Comparte rutinas, motiva a tus amigos, y descubre ofertas creadas por emprendedores de nuestro mercado interno."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoldBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                WaynessLogo(size = 100.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "WAYNESS",
                    color = BoldTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    letterSpacing = 2.sp
                )
            }

            // Central Banner Card or visual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Modern gradient ambient glow inside
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(BoldPurplePrimary.copy(0.12f), Color.Transparent),
                                    radius = 350f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (currentPage) {
                                0 -> Icons.Default.EmojiEvents
                                1 -> Icons.Default.Favorite
                                else -> Icons.Default.Groups
                            },
                            contentDescription = "Theme Icon",
                            tint = BoldPurplePrimary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            carouselTexts[currentPage].first.uppercase(),
                            color = BoldTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            letterSpacing = (-0.2).sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            carouselTexts[currentPage].second,
                            color = BoldTextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Indicators & Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Page indicator pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .width(if (currentPage == i) 24.dp else 8.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentPage == i) BoldPurplePrimary else BoldOutline
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < 2) {
                            currentPage++
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BoldPurplePrimary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        if (currentPage < 2) "CONTINUAR" else "COMENZAR",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// 2. Beautiful Signup & Login Screen
@Composable
fun LoginSignupScreen(
    onLoginSuccess: (String, String) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoldBg)
    ) {
        // Glowing brand cloud in warm theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(BoldPurplePrimary.copy(0.1f), Color.Transparent),
                        radius = 600f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                WaynessLogo(size = 90.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "WAYNESS",
                    color = BoldTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "El modo mejor para obtener lo que realmente quieres",
                    color = BoldTextSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Tab bar toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(BoldNavBg)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isSignUp) BoldPurplePrimary else Color.Transparent)
                            .clickable { isSignUp = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "INICIAR SESIÓN",
                            fontWeight = FontWeight.Black,
                            color = if (!isSignUp) Color.White else BoldTextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSignUp) BoldPurplePrimary else Color.Transparent)
                            .clickable { isSignUp = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "REGISTRARSE",
                            fontWeight = FontWeight.Black,
                            color = if (isSignUp) Color.White else BoldTextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Form Container matching visual specs (white background, bold outline)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isSignUp) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Nombre Completo") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_name_field"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary,
                                    unfocusedLabelColor = BoldTextSecondary
                                )
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico (Email)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BoldPurplePrimary,
                                focusedLabelColor = BoldPurplePrimary,
                                unfocusedBorderColor = BoldOutline,
                                focusedTextColor = BoldTextPrimary,
                                unfocusedTextColor = BoldTextPrimary,
                                unfocusedLabelColor = BoldTextSecondary
                            )
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field"),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BoldPurplePrimary,
                                focusedLabelColor = BoldPurplePrimary,
                                unfocusedBorderColor = BoldOutline,
                                focusedTextColor = BoldTextPrimary,
                                unfocusedTextColor = BoldTextPrimary,
                                unfocusedLabelColor = BoldTextSecondary
                            )
                        )

                        Button(
                            onClick = {
                                val nameInput = if (username.isBlank()) "Nicolás Coronel" else username
                                val emailInput = if (email.isBlank()) "Nico.coronel@hotmail.es" else email
                                onLoginSuccess(emailInput, nameInput)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                if (isSignUp) "ACEPTAR Y CREAR CUENTA" else "INGRESAR",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Text(
                    "O CONTINUAR CON:",
                    color = BoldTextSecondary,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Apple Button (Light with bold outline)
                Button(
                    onClick = { onLoginSuccess("apple_user@wayness.com", "Socio Apple") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_apple_button")
                ) {
                    Icon(imageVector = Icons.Filled.Smartphone, contentDescription = "Apple", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIGN UP WITH APPLE", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Google Button (Light with bold outline)
                Button(
                    onClick = { onLoginSuccess("Nico.coronel@hotmail.es", "Nicolás Coronel") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_google_button")
                ) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = "Google", tint = Color(0xFF4285F4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIGN UP WITH GOOGLE", color = BoldTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Facebook Button (Light with bold outline)
                Button(
                    onClick = { onLoginSuccess("fb_colleague@wayness.com", "Facebook Partner") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_facebook_button")
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Facebook", tint = Color(0xFF1877F2))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIGN UP WITH FACEBOOK", color = BoldTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// 3. Primary Dashboard Scaffold including Tab toggling
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScaffold(
    viewModel: WaynessViewModel,
    userProfile: UserProfile?
) {
    var selectedTab by remember { mutableStateOf("feed") }

    val lastClaimedCoupon by viewModel.lastClaimedCoupon.collectAsStateWithLifecycle()
    val toastMsg by viewModel.claimToastMessage.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BoldNavBg,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(68.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == "feed",
                    onClick = { selectedTab = "feed" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Feed", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BoldTextPrimary,
                        selectedTextColor = BoldPurplePrimary,
                        indicatorColor = BoldLavenderContainer,
                        unselectedIconColor = BoldTextSecondary,
                        unselectedTextColor = BoldTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "stats",
                    onClick = { selectedTab = "stats" },
                    icon = { Icon(Icons.Default.QueryStats, contentDescription = "Stats") },
                    label = { Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BoldTextPrimary,
                        selectedTextColor = BoldPurplePrimary,
                        indicatorColor = BoldLavenderContainer,
                        unselectedIconColor = BoldTextSecondary,
                        unselectedTextColor = BoldTextSecondary
                    )
                )
                
                // Centered Market Logo Button (Solid Deep Purple)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BoldPurplePrimary)
                        .clickable { selectedTab = "market" }
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    WaynessLogo(size = 36.dp)
                }

                NavigationBarItem(
                    selected = selectedTab == "entrepreneur",
                    onClick = { selectedTab = "entrepreneur" },
                    icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "Socio Emprendedor") },
                    label = { Text("Socio", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BoldTextPrimary,
                        selectedTextColor = BoldPurplePrimary,
                        indicatorColor = BoldLavenderContainer,
                        unselectedIconColor = BoldTextSecondary,
                        unselectedTextColor = BoldTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == "profile",
                    onClick = { selectedTab = "profile" },
                    icon = {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = "Perfil/Inbox")
                            if (notifications.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    },
                    label = { Text("Notis", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BoldTextPrimary,
                        selectedTextColor = BoldPurplePrimary,
                        indicatorColor = BoldLavenderContainer,
                        unselectedIconColor = BoldTextSecondary,
                        unselectedTextColor = BoldTextSecondary
                    )
                )
            }
        },
        containerColor = BoldBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "feed" -> FeedTab(viewModel = viewModel)
                "stats" -> StatsTab(viewModel = viewModel, userProfile = userProfile)
                "market" -> MarketTab(viewModel = viewModel, userProfile = userProfile)
                "entrepreneur" -> EntrepreneurTab(viewModel = viewModel, userProfile = userProfile)
                "profile" -> NotificationsTab(viewModel = viewModel, userProfile = userProfile)
            }

            // Coupon generation claims Alert popup (from Video Mockup)
            lastClaimedCoupon?.let { coupon ->
                Dialog(onDismissRequest = { viewModel.dismissClaimCoupon() }) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(20.dp, RoundedCornerShape(28.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            WaynessLogo(size = 80.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "YOUR EFFORT COUNTS!",
                                color = BoldTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Enjoy your choice!",
                                color = BoldPurplePrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "You will receive your discount coupon by E-Mail!",
                                color = BoldTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Coupon label
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BoldNavBg)
                                    .border(1.dp, BoldOutline, RoundedCornerShape(12.dp))
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    coupon,
                                    color = BoldPurplePrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 2.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.dismissClaimCoupon() },
                                colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ACEPTAR", fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
            }

            // Simple popup Toast
            toastMsg?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.dismissToast() }) {
                            Text("OK", color = Color(0xFFFFD54F))
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(msg)
                }
            }
        }
    }
}

// 4. Live Community Feed Tab
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTab(viewModel: WaynessViewModel) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    var postDraftText by remember { mutableStateOf("") }
    var showCommentDialogPostId by remember { mutableStateOf<Int?>(null) }
    var commentText by remember { mutableStateOf("") }

    val activeBpm by viewModel.currentHeartRate.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // App top header bar representing Wayness title & features
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WaynessLogo(size = 38.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "WAYNESS",
                    color = BoldTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { viewModel.pushAppNotification("Cámara", "Simulador de fotos de progreso abierto.") }) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camara", tint = BoldTextPrimary)
                }
                IconButton(onClick = { viewModel.triggerSimulatedCaloriesAndPoints() }) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync Fit", tint = BoldPurplePrimary)
                }
            }
        }

        // Horizontal online friends/stories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(2.dp, BoldPurplePrimary, CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(BoldNavBg)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add story", tint = BoldPurplePrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tu Historia", color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            val mockAvatars = listOf(
                "Nico" to Color(0xFF03A9F4),
                "Jean" to Color(0xFF4CAF50),
                "Laura" to Color(0xFFFF9800),
                "Aline" to BoldPurplePrimary,
                "Sergio" to Color(0xFF3F51B5),
                "Maria" to Color(0xFF009688)
            )
            items(mockAvatars) { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(2.dp, BoldOutline, CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(item.second),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            item.first.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.first, color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        HorizontalDivider(color = BoldOutline.copy(0.4f), thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Health & Points Dashboard Row (From requested mockup spec!)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Total Points
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        colors = CardDefaults.cardColors(containerColor = BoldLavenderContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "TOTAL POINTS",
                                    color = BoldLavenderText.copy(0.7f),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Points Icon",
                                    tint = BoldLavenderText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "55,334",
                                    color = BoldLavenderText,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1.5).sp
                                )
                                Text(
                                    "+120 este ciclo",
                                    color = BoldLavenderText.copy(0.8f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Card 2: Heart Rate
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        colors = CardDefaults.cardColors(containerColor = BoldBlueContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "HEART RATE",
                                    color = BoldBlueText.copy(0.7f),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Heart Rate Icon",
                                    tint = BoldBlueText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$activeBpm",
                                        color = BoldBlueText,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-1.5).sp
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        "bpm",
                                        color = BoldBlueText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                                Text(
                                    "Google Wealth",
                                    color = BoldBlueText.copy(0.8f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Drafting post card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Comparte tu motivación hoy".uppercase(),
                            color = BoldTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = postDraftText,
                            onValueChange = { postDraftText = it },
                            placeholder = { Text("¿Cómo va tu rutina? ¿Qué planes de entrenamiento tienes?", color = BoldTextSecondary.copy(0.5f), fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .testTag("feed_draft_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BoldPurplePrimary,
                                unfocusedBorderColor = BoldOutline,
                                focusedTextColor = BoldTextPrimary,
                                unfocusedTextColor = BoldTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { viewModel.pushAppNotification("Fotos", "Seleccionador de imagen mockup activado.") }) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Agregar foto", tint = BoldPurplePrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Añadir Foto", color = BoldTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    if (postDraftText.isNotBlank()) {
                                        viewModel.submitPost(postDraftText, null)
                                        postDraftText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("POSTEAR", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }

            // Post list cards
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_card_${post.id}"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Author header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(BoldPurplePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        post.authorName.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        post.authorName,
                                        color = BoldTextPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        post.timestamp,
                                        color = BoldTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.pushAppNotification("Reportes", "Opción de reporte activa.") }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = BoldTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Content text
                        Text(
                            post.content,
                            color = BoldTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulated workout background card graphic (Light thematic highlight)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, BoldOutline, RoundedCornerShape(20.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(BoldPurplePrimary.copy(0.04f), BoldPurplePrimary.copy(0.12f))
                                    )
                                )
                        ) {
                            // High contrast motiv banner inside
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BoldPurplePrimary)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("WAYNESS DEPORTE", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(0.9f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        "Esfuerzo Cardio: ¡Alcanza tus metas diarias!",
                                        color = BoldTextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactions actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.toggleLike(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (post.isLikedByUser) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Corazón",
                                        tint = if (post.isLikedByUser) BoldPurplePrimary else BoldTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        post.likesCount.toString(),
                                        color = BoldTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showCommentDialogPostId = post.id }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = "Comentario",
                                        tint = BoldTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        post.commentsCount.toString(),
                                        color = BoldTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Guardar",
                                tint = BoldTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Comment Box Dialog popup
    showCommentDialogPostId?.let { postId ->
        Dialog(onDismissRequest = { showCommentDialogPostId = null }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Añadir Comentario", color = BoldTextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Escribe tu comentario...", color = BoldTextSecondary.copy(0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = BoldTextPrimary,
                            unfocusedTextColor = BoldTextPrimary,
                            focusedContainerColor = BoldNavBg,
                            unfocusedContainerColor = BoldNavBg
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showCommentDialogPostId = null }) {
                            Text("Cancelar", color = BoldTextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.commentOnPost(postId, commentText)
                                    commentText = ""
                                    showCommentDialogPostId = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("COMENTAR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }
        }
    }
}

// 5. Statistics, Health Connection & Workout Engine Simulated Screen
@Composable
fun StatsTab(
    viewModel: WaynessViewModel,
    userProfile: UserProfile?
) {
    val isWorkoutActive by viewModel.isWorkoutActive.collectAsStateWithLifecycle()
    val activeBpm by viewModel.currentHeartRate.collectAsStateWithLifecycle()
    val activeMins by viewModel.workoutMinutes.collectAsStateWithLifecycle()
    val earnedPts by viewModel.workoutPointsEarned.collectAsStateWithLifecycle()
    val burnedCal by viewModel.workoutCaloriesBurned.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stats_tab_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Wave Header (as in the statistics screenshot showing 495 kcal & cumulative counts)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = BoldPurplePrimary)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ESTADÍSTICAS SEMANALES",
                                color = Color.White.copy(0.8f),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Real Calories count matching Screenshot exactly: "495 kcal"
                        Text(
                            "${userProfile?.totalCalories ?: 495} kcal",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 36.sp,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            "Calorías Totales Quemadas".uppercase(),
                            color = Color.White.copy(0.7f),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Metas: ${userProfile?.totalCalories ?: 495} / ${userProfile?.caloriesGoal ?: 1000} kcal",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        // Linear Progress indicator
                        val progressRatio = ((userProfile?.totalCalories ?: 495).toFloat() / (userProfile?.caloriesGoal ?: 1000).toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .padding(vertical = 4.dp)
                                .clip(CircleShape),
                            color = Color(0xFFFFD54F),
                            trackColor = Color.White.copy(0.25f),
                        )
                    }
                }
            }
        }

        // Active Simulation Workout Console (High-Fidelity Interaction layer to test PDF rules!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Consola de Entrenamiento".uppercase(),
                                color = BoldTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Simula entrenamiento cardíaco integrado",
                                color = BoldTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { viewModel.connectOrDisconnectHealthConnection() }) {
                            Icon(
                                imageVector = if (userProfile?.hasConnectedHealth == true) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                                contentDescription = "Health sync",
                                tint = if (userProfile?.hasConnectedHealth == true) BoldPurplePrimary else BoldTextSecondary.copy(0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Simulated live panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BoldNavBg)
                            .border(1.dp, BoldOutline, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PULSO CARDÍACO", color = BoldTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$activeBpm", color = Color(0xFFC62828), fontSize = 24.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("bpm", color = BoldTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 2.dp))
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DURACIÓN (SIM)", color = BoldTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$activeMins", color = BoldTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("min", color = BoldTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 2.dp))
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("WPOINTS", color = BoldTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Text("$earnedPts", color = BoldPurplePrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    // Intensitity indicator banner based on heart rate from startup's system rules (PDF)
                    val bpmZoneLabelAndColor = when {
                        activeBpm < 90 -> "Zona de Reposo (<90 bpm): 0 WEP/min" to Color.Gray
                        activeBpm in 90..109 -> "Zona Verde (90-109 bpm): 4 WEP/min" to Color(0xFF2E7D32)
                        activeBpm in 110..129 -> "Zona Naranja (110-129 bpm): 5 WEP/min" to Color(0xFFEF6C00)
                        activeBpm in 130..149 -> "Zona Roja (130-149 bpm): 6 WEP/min" to Color(0xFFC62828)
                        else -> "Zona Viola (+150 bpm): 7 WEP/min" to Color(0xFF6A1B9A)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bpmZoneLabelAndColor.second.copy(0.12f))
                            .border(1.5.dp, bpmZoneLabelAndColor.second, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            bpmZoneLabelAndColor.first,
                            color = bpmZoneLabelAndColor.second,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Sliding BPM Controller
                    Text(
                        "Frecuencia Cardíaca (Controles del simulador):",
                        color = BoldTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Slider(
                        value = activeBpm.toFloat(),
                        onValueChange = { viewModel.setHeartRate(it.toInt()) },
                        valueRange = 60f..180f,
                        colors = SliderDefaults.colors(
                            thumbColor = BoldPurplePrimary,
                            activeTrackColor = BoldPurplePrimary,
                            inactiveTrackColor = BoldOutline
                        )
                    )

                    // Start workout button
                    Button(
                        onClick = { viewModel.toggleWorkoutSession() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("workout_toggle_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWorkoutActive) Color(0xFFC62828) else BoldPurplePrimary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isWorkoutActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Session",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isWorkoutActive) "DETENER ENTRENAMIENTO" else "INICIAR ENTRENAMIENTO",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Section header
        item {
            Text(
                "Historial de actividades realizadas",
                color = BoldTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Sub items matching screenshot elements under stats (157 pushups / 12 repetitions)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FLEXIONES", color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${userProfile?.pushupsCount ?: 157}", color = BoldTextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("Pushups totales", color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("REPETICIONES", color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${userProfile?.repsCount ?: 12}", color = BoldTextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("Series completas", color = BoldTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Direct controls for manual fast additions (to easily show user points system)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = BoldNavBg)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Pruebas rápidas (Suma de puntos manual):",
                        color = BoldTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.pushAppNotification("¡Logro!", "Hiciste 10 flexiones.")
                                viewModel.addManualExercisePoints("Pushups", 10, 40)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+10 Pushups\n(+40 WP)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        }

                        Button(
                            onClick = {
                                viewModel.pushAppNotification("¡Logro!", "Hiciste 5 repeticiones.")
                                viewModel.addManualExercisePoints("Repetitions", 5, 30)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+5 Reps\n(+30 WP)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

// 6. Marketplace Reward Store Screen
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarketTab(
    viewModel: WaynessViewModel,
    userProfile: UserProfile?
) {
    val rewardList by viewModel.rewards.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Points balance banner header matching visual specs (light styled card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.5.dp, BoldOutline),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "Tu Balance de Esfuerzo".uppercase(),
                    color = BoldTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WaynessLogo(size = 32.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${userProfile?.wPoints ?: 55334}",
                        color = BoldTextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "WPoints",
                        color = BoldPurplePrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Offer lists grid
        Text(
            "Recompensas Disponibles".uppercase(),
            color = BoldTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rewardList) { reward ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reward_item_${reward.id}"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        // Product Cover preview banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(BoldPurplePrimary.copy(0.04f), BoldPurplePrimary.copy(0.12f))
                                    )
                                )
                                .border(BorderStroke(0.dp, Color.Transparent))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, BoldOutline, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ConfirmationNumber,
                                        contentDescription = "Points",
                                        tint = BoldPurplePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "${reward.pointsCost} WP",
                                        color = BoldPurplePrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    reward.title.uppercase(),
                                    color = BoldTextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        HorizontalDivider(color = BoldOutline, thickness = 1.dp)

                        // Bottom part with description & claim action
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Ofrecido por: ${reward.providerName}",
                                    color = BoldPurplePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )

                                if (!reward.isBuiltIn) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BoldLavenderContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PYME LOCAL", color = BoldPurplePrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                reward.description,
                                color = BoldTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.claimMarketReward(reward) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("claim_button_${reward.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("CANJEAR CUPÓN", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. Become entrepreneur form & Portal Tab
@Composable
fun EntrepreneurTab(
    viewModel: WaynessViewModel,
    userProfile: UserProfile?
) {
    var businessName by remember { mutableStateOf("") }
    var businessCategory by remember { mutableStateOf("") }
    
    // Reward creation fields
    var rewardTitle by remember { mutableStateOf("") }
    var rewardCost by remember { mutableStateOf("") }
    var rewardDesc by remember { mutableStateOf("") }

    val registered = userProfile?.role == "ENTREPRENEUR"

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = "Emprendedores",
                    tint = BoldPurplePrimary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Portal de Emprendedores y Pymes".uppercase(),
                    color = BoldTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Ingresa tu negocio del mercado interno de Wayness y vende cupones de descuento por puntos de esfuerzo de los usuarios.",
                    color = BoldTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!registered) {
                // Registration Form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "Registra tu Negocio Gratuito".uppercase(),
                                color = BoldTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )

                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { businessName = it },
                                label = { Text("Nombre del Negocio (Pyme / Marca)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("partner_business_name"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedLabelColor = BoldTextSecondary,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = businessCategory,
                                onValueChange = { businessCategory = it },
                                label = { Text("Categoría (Gimnasios, Nutrición, Gaming...)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("partner_category"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedLabelColor = BoldTextSecondary,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary
                                )
                            )

                            Button(
                                onClick = {
                                    if (businessName.isNotBlank() && businessCategory.isNotBlank()) {
                                        viewModel.submitEntrepreneurRegistration(businessName, businessCategory, "pyme")
                                    } else {
                                        viewModel.submitEntrepreneurRegistration("Gym Wayness Local", "Fitness", "gym")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("partner_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("ACEPTAR Y REGISTRARME", fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            } else {
                // Already listed. Show item creator
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Listo", tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("¡Tu Negocio está Registrado!", color = Color(0xFF1B5E20), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Apareces listado y puedes agregar productos.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    // Item Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, BoldOutline),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "Crear Nueva Oferta / Cupón".uppercase(),
                                color = BoldTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )

                            OutlinedTextField(
                                value = rewardTitle,
                                onValueChange = { rewardTitle = it },
                                label = { Text("Título de la Oferta (ej. 20% Gimnasio)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reward_title_field"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedLabelColor = BoldTextSecondary,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = rewardCost,
                                onValueChange = { rewardCost = it },
                                label = { Text("Costo en WPoints (ej. 15000)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reward_cost_field"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedLabelColor = BoldTextSecondary,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = rewardDesc,
                                onValueChange = { rewardDesc = it },
                                label = { Text("Detalles y condiciones del descuento") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("reward_desc_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BoldPurplePrimary,
                                    unfocusedBorderColor = BoldOutline,
                                    focusedLabelColor = BoldPurplePrimary,
                                    unfocusedLabelColor = BoldTextSecondary,
                                    focusedTextColor = BoldTextPrimary,
                                    unfocusedTextColor = BoldTextPrimary
                                )
                            )

                            Button(
                                onClick = {
                                    val costInt = rewardCost.toIntOrNull() ?: 15000
                                    if (rewardTitle.isNotBlank() && rewardDesc.isNotBlank()) {
                                        viewModel.addNewPartnerReward(rewardTitle, costInt, rewardDesc)
                                        rewardTitle = ""
                                        rewardCost = ""
                                        rewardDesc = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("reward_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BoldPurplePrimary),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("PUBLICAR CUPÓN EN EL MERCADO", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 8. Push Notifications Inbox Panel & Profile Logs Screen
@Composable
fun NotificationsTab(
    viewModel: WaynessViewModel,
    userProfile: UserProfile?
) {
    val notificationList by viewModel.notifications.collectAsStateWithLifecycle()
    val claimsHistory by viewModel.activityLogs.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Simple User profile summary with stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, BoldOutline),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BoldPurplePrimary.copy(0.08f))
                            .border(1.5.dp, BoldOutline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            userProfile?.name?.take(2)?.uppercase() ?: "NC",
                            color = BoldPurplePrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            userProfile?.name ?: "Nicolás Coronel",
                            color = BoldTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            userProfile?.email ?: "Nico.coronel@hotmail.es",
                            color = BoldTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BoldPurplePrimary.copy(0.1f))
                                    .border(1.dp, BoldPurplePrimary.copy(0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    userProfile?.role ?: "USER",
                                    color = BoldPurplePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF9C4))
                                    .border(1.dp, Color(0xFFFBC02D).copy(0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "Streak: ${userProfile?.currentStreak ?: 3} días",
                                    color = Color(0xFFF57F17),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section for Push notifications
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Buzón de Notificaciones".uppercase(),
                    color = BoldTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { viewModel.pushAppNotification("Alerta Test", "Notificación instantánea de prueba lanzada con éxito.") }) {
                    Text("+ Test Push", color = BoldPurplePrimary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        if (notificationList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes notificaciones push pendientes.", color = BoldTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            items(notificationList) { notify ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = BoldNavBg)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CircleNotifications,
                                contentDescription = "Alarm",
                                tint = BoldPurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    notify.title,
                                    color = BoldTextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                Text(
                                    notify.text,
                                    color = BoldTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.dismissNotification(notify.id) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete notify", tint = BoldTextSecondary.copy(0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Section for Workout logs
        item {
            Text(
                "Historial de Entrenamientos".uppercase(),
                color = BoldTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        if (claimsHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin entrenamientos grabados recientemente.", color = BoldTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            items(claimsHistory) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, BoldOutline),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BoldPurplePrimary.copy(0.08f))
                                    .border(1.dp, BoldOutline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = "Run", tint = BoldPurplePrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    log.activityType,
                                    color = BoldTextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "Duración: ${log.durationMinutes} mins | Prom: ${log.avgHeartRate} bpm",
                                    color = BoldTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "+${log.wPointsEarned} WP",
                                color = BoldPurplePrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                "${log.amount} kcal",
                                color = BoldTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
