package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- Bold Typography Design Palette ---
val BoldBg = Color(0xFFFDF8F6)            // Warm cream/clay background
val BoldTextPrimary = Color(0xFF1D1B20)    // High-contrast near-black charcoal
val BoldTextSecondary = Color(0xFF49454F)  // Muted charcoal-grey
val BoldOutline = Color(0xFFCAC4D0)        // Border outline grey
val BoldPurplePrimary = Color(0xFF6750A4)  // Accent/Button material purple
val BoldLavenderContainer = Color(0xFFE8DEF8) // Soft lavender tracking container
val BoldLavenderText = Color(0xFF21005D)   // High-contrast deep purple text
val BoldBlueContainer = Color(0xFFD3E4FF)     // Soft light-blue tracking container
val BoldBlueText = Color(0xFF001D35)       // High-contrast deep blue text
val BoldNavBg = Color(0xFFF3EDF7)          // Bottom navigation bar background
val BoldNavBorder = Color(0xFFE7E0EC)      // Bottom navigation bar top border

// Base/Unified Theme Alignments
val WaynessPrimary = BoldPurplePrimary
val WaynessSecondary = BoldLavenderContainer
val WaynessAccent = BoldBlueContainer
val WaynessYellow = Color(0xFFFFD54F)      // Golden points accent

// Dark/Fallback mappings (keeping high harmony)
val WaynessDarkBg = BoldBg
val WaynessDarkSurface = Color.White
val WaynessDarkCard = Color.White
val WaynessDarkTextPrimary = BoldTextPrimary
val WaynessDarkTextSecondary = BoldTextSecondary

// Light Theme Mappings
val WaynessLightBg = BoldBg
val WaynessLightSurface = Color.White
val WaynessLightCard = Color.White
val WaynessLightTextPrimary = BoldTextPrimary
val WaynessLightTextSecondary = BoldTextSecondary
