package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "local_user",
    val name: String,
    val email: String,
    val role: String = "USER", // USER, ENTREPRENEUR, ADMIN
    val wPoints: Int = 1250, // Starts with some welcome points
    val currentStreak: Int = 3,
    val totalCalories: Int = 495,
    val caloriesGoal: Int = 1000,
    val hasConnectedHealth: Boolean = false,
    val currentHeartRate: Int = 72,
    val pushupsCount: Int = 157,
    val repsCount: Int = 12
)

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorAvatarUrl: String,
    val timestamp: String,
    val content: String,
    val imageUrl: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByUser: Boolean = false
)

@Entity(tableName = "reward_items")
data class RewardItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val pointsCost: Int,
    val imageUrl: String? = null,
    val isBuiltIn: Boolean = false,
    val providerName: String = "Wayness",
    val description: String = ""
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityType: String, // e.g. "Repetitions", "Pushups", "Cardio Run"
    val amount: Int,
    val durationMinutes: Int,
    val avgHeartRate: Int,
    val wPointsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "claimed_rewards")
data class ClaimedReward(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rewardTitle: String,
    val pointsCost: Int,
    val couponCode: String,
    val claimTimestamp: Long = System.currentTimeMillis()
)
