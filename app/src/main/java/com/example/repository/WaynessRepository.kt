package com.example.repository

import com.example.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class WaynessRepository(private val dao: WaynessDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()
    val allPosts: Flow<List<CommunityPost>> = dao.getAllPostsFlow()
    val allRewards: Flow<List<RewardItem>> = dao.getAllRewardsFlow()
    val allActivityLogs: Flow<List<ActivityLog>> = dao.getAllActivityLogsFlow()
    val allClaims: Flow<List<ClaimedReward>> = dao.getAllClaimsFlow()

    suspend fun saveProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addPost(post: CommunityPost) {
        dao.insertPost(post)
    }

    suspend fun addReward(reward: RewardItem) {
        dao.insertReward(reward)
    }

    suspend fun addActivityLog(log: ActivityLog) {
        // First fetch current profile to update accumulated points and stats
        val currentProfile = dao.getUserProfile() ?: UserProfile(
            name = "Guest User",
            email = "guest@wayness.com"
        )
        
        val updatedProfile = currentProfile.copy(
            wPoints = currentProfile.wPoints + log.wPointsEarned,
            totalCalories = currentProfile.totalCalories + log.amount, // simulate calorie add
            pushupsCount = currentProfile.pushupsCount + if (log.activityType == "Pushups") log.amount else 0,
            repsCount = currentProfile.repsCount + if (log.activityType == "Repetitions") log.amount else 0
        )
        
        dao.insertOrUpdateProfile(updatedProfile)
        dao.insertActivityLog(log)
    }

    suspend fun likePost(postId: Int) {
        val posts = dao.getAllPostsFlow().firstOrNull() ?: return
        val matched = posts.find { it.id == postId } ?: return
        
        val updatedPost = if (matched.isLikedByUser) {
            matched.copy(
                isLikedByUser = false,
                likesCount = (matched.likesCount - 1).coerceAtLeast(0)
            )
        } else {
            matched.copy(
                isLikedByUser = true,
                likesCount = matched.likesCount + 1
            )
        }
        dao.updatePost(updatedPost)
    }

    suspend fun commentOnPost(postId: Int) {
        val posts = dao.getAllPostsFlow().firstOrNull() ?: return
        val matched = posts.find { it.id == postId } ?: return
        val updatedPost = matched.copy(commentsCount = matched.commentsCount + 1)
        dao.updatePost(updatedPost)
    }

    suspend fun claimReward(reward: RewardItem, email: String): String? {
        val currentProfile = dao.getUserProfile() ?: return null
        if (currentProfile.wPoints < reward.pointsCost) {
            return null // Not enough points
        }
        
        // Generate pseudo random fenix code
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val code = "WAY-" + (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        
        // Deduct points
        val updatedProfile = currentProfile.copy(
            wPoints = currentProfile.wPoints - reward.pointsCost
        )
        dao.insertOrUpdateProfile(updatedProfile)
        
        // Record claim
        val claim = ClaimedReward(
            rewardTitle = reward.title,
            pointsCost = reward.pointsCost,
            couponCode = code
        )
        dao.insertClaim(claim)
        
        return code
    }

    suspend fun registerAsEntrepreneur(businessName: String, category: String) {
        val currentProfile = dao.getUserProfile() ?: return
        val updated = currentProfile.copy(
            role = "ENTREPRENEUR",
            name = "$businessName Proprietor (${currentProfile.name})"
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun prepopulateIfNeeded() {
        // Prepopulate Profile if missing
        val currentProfile = dao.getUserProfile()
        if (currentProfile == null) {
            dao.insertOrUpdateProfile(
                UserProfile(
                    name = "Nicolás Coronel",
                    email = "Nico.coronel@hotmail.es",
                    role = "USER",
                    wPoints = 55334, // Set original demo points from the screenshot
                    currentStreak = 5,
                    totalCalories = 495,
                    caloriesGoal = 1000,
                    hasConnectedHealth = true,
                    currentHeartRate = 85
                )
            )
        }

        // Prepopulate Posts if missing
        val currentPosts = dao.getAllPostsFlow().firstOrNull()
        if (currentPosts.isNullOrEmpty()) {
            dao.insertPosts(
                listOf(
                    CommunityPost(
                        id = 1,
                        authorName = "Jean Coutu",
                        authorAvatarUrl = "avatar_jean",
                        timestamp = "Hace 1 hora",
                        content = "¡Superé mis límites hoy en el entrenamiento HIIT! He alcanzado un promedio cardíaco de 142 bpm durante 25 minutos, lo que me ha aportado una gran motivación y puntos para el mercado.",
                        imageUrl = "workout_hiit",
                        likesCount = 516,
                        commentsCount = 256,
                        isLikedByUser = false
                    ),
                    CommunityPost(
                        id = 2,
                        authorName = "Laura Rossi",
                        authorAvatarUrl = "avatar_laura",
                        timestamp = "Hace 3 horas",
                        content = "Rutina matutina completada en la zona verde (90-109 bpm). Sostener el ritmo lento también suma puntos y cuida de mi salud cardiovascular de forma equilibrada. 🚶‍♀️🌸",
                        imageUrl = "workout_running",
                        likesCount = 125,
                        commentsCount = 42,
                        isLikedByUser = true
                    ),
                    CommunityPost(
                        id = 3,
                        authorName = "Nicolás Coronel",
                        authorAvatarUrl = "avatar_nico",
                        timestamp = "Hace 5 horas",
                        content = "Hoy comparto mi plan de flexiones y entrenamiento para el pecho. ¡Se vienen grandes progresos en Wayness! Únete al reto semanal para acumular hasta 5,000 WPoints adicionales.",
                        imageUrl = "workout_pushups",
                        likesCount = 426,
                        commentsCount = 88,
                        isLikedByUser = false
                    )
                )
            )
        }

        // Prepopulate Rewards if missing
        val currentRewards = dao.getAllRewardsFlow().firstOrNull()
        if (currentRewards.isNullOrEmpty()) {
            dao.insertRewards(
                listOf(
                    RewardItem(
                        id = 1,
                        title = "League of Legends",
                        pointsCost = 50000,
                        imageUrl = "reward_lol",
                        isBuiltIn = true,
                        providerName = "Riot Games",
                        description = "Canjea 50,000 WPoints por 5,000 Riot Points cargados a tu cuenta de inmediato para skins o campeones."
                    ),
                    RewardItem(
                        id = 2,
                        title = "Fortnite - Pase de Batalla",
                        pointsCost = 70000,
                        imageUrl = "reward_fortnite",
                        isBuiltIn = true,
                        providerName = "Epic Games",
                        description = "Accede al Pase de Batalla de la última temporada de Fortnite. ¡Para verdaderos supervivientes de la isla!"
                    ),
                    RewardItem(
                        id = 3,
                        title = "1kg Protein - MyProtein",
                        pointsCost = 10000,
                        imageUrl = "reward_myprotein",
                        isBuiltIn = true,
                        providerName = "MyProtein Spain",
                        description = "Asegura tu suplementación para la recuperación post-entrenamiento de suero lácteo de alta pureza."
                    ),
                    RewardItem(
                        id = 4,
                        title = "50% Beer Discount - The Drunken Ship",
                        pointsCost = 20000,
                        imageUrl = "reward_beer",
                        isBuiltIn = true,
                        providerName = "The Drunken Ship Pub",
                        description = "Porque el equilibrio entre esfuerzo y recompensa es clave. Disfruta un 50% de descuento en cervezas artesanales locales."
                    )
                )
            )
        }
    }
}
