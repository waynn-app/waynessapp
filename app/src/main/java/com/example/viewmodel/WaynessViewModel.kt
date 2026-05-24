package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.*
import com.example.repository.WaynessRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed interface AuthUiState {
    object SignedOut : AuthUiState
    object Loading : AuthUiState
    data class SignedIn(val email: String, val name: String) : AuthUiState
}

data class PushNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class WaynessViewModel(
    application: Application,
    private val repository: WaynessRepository
) : AndroidViewModel(application) {

    // --- Authentication ---
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.SignedIn("Nico.coronel@hotmail.es", "Nicolás Coronel"))
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    // --- Push Notifications simulated inbox ---
    private val _notifications = MutableStateFlow<List<PushNotification>>(
        listOf(
            PushNotification(
                title = "¡Bienvenido a Wayness!",
                text = "Tu cuenta se ha configurado con éxito. Conecta tu sensor cardíaco para empezar a ganar WPoints."
            )
        )
    )
    val notifications: StateFlow<List<PushNotification>> = _notifications.asStateFlow()

    // --- Active Workout State ---
    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _currentHeartRate = MutableStateFlow(85)
    val currentHeartRate: StateFlow<Int> = _currentHeartRate.asStateFlow()

    private val _workoutMinutes = MutableStateFlow(0)
    val workoutMinutes: StateFlow<Int> = _workoutMinutes.asStateFlow()

    private val _workoutPointsEarned = MutableStateFlow(0)
    val workoutPointsEarned: StateFlow<Int> = _workoutPointsEarned.asStateFlow()

    private val _workoutCaloriesBurned = MutableStateFlow(0)
    val workoutCaloriesBurned: StateFlow<Int> = _workoutCaloriesBurned.asStateFlow()

    // --- Claims Dialog State ---
    private val _lastClaimedCoupon = MutableStateFlow<String?>(null)
    val lastClaimedCoupon: StateFlow<String?> = _lastClaimedCoupon.asStateFlow()

    private val _claimToastMessage = MutableStateFlow<String?>(null)
    val claimToastMessage: StateFlow<String?> = _claimToastMessage.asStateFlow()

    // --- Exposed DB flows ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val posts: StateFlow<List<CommunityPost>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardItem>> = repository.allRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.allActivityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
        }
    }

    // --- Save User Profile ---
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    // --- Manual Exercise Simulator additions ---
    fun addManualExercisePoints(type: String, amount: Int, points: Int) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: return@launch
            val updated = current.copy(
                pushupsCount = current.pushupsCount + if (type == "Pushups") amount else 0,
                repsCount = current.repsCount + if (type == "Repetitions") amount else 0,
                wPoints = current.wPoints + points
            )
            repository.saveProfile(updated)
        }
    }

    // --- Authenticators ---
    fun loginWithGoogle(email: String, name: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            delay(1200) // beautiful auth mock load
            _authUiState.value = AuthUiState.SignedIn(email, name)
            
            // Set User profile in DB
            val existing = repository.userProfile.firstOrNull()
            if (existing == null) {
                repository.saveProfile(
                    UserProfile(
                        name = name,
                        email = email,
                        wPoints = 55334,
                        totalCalories = 495,
                        hasConnectedHealth = true
                    )
                )
            } else {
                repository.saveProfile(existing.copy(name = name, email = email))
            }

            pushAppNotification(
                "Inicio de sesión exitoso",
                "Has ingresado como $name de manera completamente segura."
            )
        }
    }

    fun logout() {
        _authUiState.value = AuthUiState.SignedOut
    }

    // --- Add Notifications ---
    fun pushAppNotification(title: String, text: String) {
        val list = _notifications.value.toMutableList()
        list.add(0, PushNotification(title = title, text = text))
        _notifications.value = list
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    // --- Community Interations ---
    fun submitPost(content: String, imageUrl: String?) {
        viewModelScope.launch {
            val user = repository.userProfile.firstOrNull() ?: return@launch
            val newPost = CommunityPost(
                authorName = user.name,
                authorAvatarUrl = "avatar_nico", // Uses Nicolás avatar or dynamic
                timestamp = "Ahora mismo",
                content = content,
                imageUrl = imageUrl,
                likesCount = 0,
                commentsCount = 0,
                isLikedByUser = false
            )
            repository.addPost(newPost)
            pushAppNotification(
                "Publicación enviada",
                "Tu post sobre entrenamiento se ha compartido con la comunidad de Wayness."
            )
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }

    fun commentOnPost(postId: Int, text: String) {
        viewModelScope.launch {
            repository.commentOnPost(postId)
            pushAppNotification(
                "Nuevo comentario",
                "Comentaste: \"$text\""
            )
        }
    }

    // --- Heart Rate & Workout Simulation Tracking ---
    fun setHeartRate(bpm: Int) {
        _currentHeartRate.value = bpm
        
        // If workout is active, dynamically update live metrics if applicable
        if (_isWorkoutActive.value) {
            // Recalculate based on currently set heart rate
            val pMin = getPointsPerMinuteForBpm(bpm)
            viewModelScope.launch {
                val existing = repository.userProfile.firstOrNull()
                if (existing != null) {
                    repository.saveProfile(existing.copy(currentHeartRate = bpm))
                }
            }
        }
    }

    fun toggleWorkoutSession() {
        if (_isWorkoutActive.value) {
            // Stop session and save logs
            val earned = _workoutPointsEarned.value
            val calories = _workoutCaloriesBurned.value
            val mins = _workoutMinutes.value
            val avgBpm = _currentHeartRate.value
            
            viewModelScope.launch {
                _isWorkoutActive.value = false
                
                if (earned > 0) {
                    val log = ActivityLog(
                        activityType = "Cardio Run / Health Workout",
                        amount = calories,
                        durationMinutes = mins,
                        avgHeartRate = avgBpm,
                        wPointsEarned = earned
                    )
                    repository.addActivityLog(log)
                    
                    pushAppNotification(
                        "¡Esfuerzo recompensado!",
                        "Entrenamiento finalizado. Ganaste $earned WPoints y quemaste $calories kcal."
                    )
                }
                
                // Reset stats
                _workoutMinutes.value = 0
                _workoutPointsEarned.value = 0
                _workoutCaloriesBurned.value = 0
            }
        } else {
            // Start session
            _isWorkoutActive.value = true
            _workoutMinutes.value = 0
            _workoutPointsEarned.value = 0
            _workoutCaloriesBurned.value = 0
            
            // Launch periodic coroutine that adds minutes and points every few seconds to simulate workout flow
            viewModelScope.launch {
                while (_isWorkoutActive.value) {
                    delay(3000) // 1 minute in-app is simulated every 3 seconds
                    if (!_isWorkoutActive.value) break
                    
                    _workoutMinutes.value = _workoutMinutes.value + 1
                    
                    val bpm = _currentHeartRate.value
                    val pts = getPointsPerMinuteForBpm(bpm)
                    _workoutPointsEarned.value = _workoutPointsEarned.value + pts
                    
                    // calories burned depends slightly on heart rate
                    val calAdd = if (bpm >= 130) 12 else if (bpm >= 90) 8 else 4
                    _workoutCaloriesBurned.value = _workoutCaloriesBurned.value + calAdd
                }
            }
        }
    }

    // Calculations from startup's system rules (PDF)
    fun getPointsPerMinuteForBpm(bpm: Int): Int {
        return when {
            bpm < 90 -> 0
            bpm in 90..109 -> 4 // Zona verde
            bpm in 110..129 -> 5 // Zona naranja
            bpm in 130..149 -> 6 // Zona roja
            else -> 7 // Zona viola (+150 bpm)
        }
    }

    // --- Marketplace Actions ---
    fun claimMarketReward(rewardItem: RewardItem) {
        viewModelScope.launch {
            val user = repository.userProfile.firstOrNull() ?: return@launch
            if (user.wPoints < rewardItem.pointsCost) {
                _claimToastMessage.value = "Puntos insuficientes para canjear este producto."
                return@launch
            }
            
            val code = repository.claimReward(rewardItem, user.email)
            if (code != null) {
                _lastClaimedCoupon.value = code
                pushAppNotification(
                    "Cupón generado con éxito",
                    "Canjeaste ${rewardItem.title}. Cupón: $code. Enviado a ${user.email}"
                )
            }
        }
    }

    fun dismissClaimCoupon() {
        _lastClaimedCoupon.value = null
    }

    fun dismissToast() {
        _claimToastMessage.value = null
    }

    // --- Entrepreneur Registration ---
    fun submitEntrepreneurRegistration(businessName: String, category: String, logoSelection: String) {
        viewModelScope.launch {
            repository.registerAsEntrepreneur(businessName, category)
            pushAppNotification(
                "¡Bienvenido Socio Emprendedor!",
                "Has sido aprobado. Ahora puedes listar tus propios cupones y ofertas para canjes."
            )
        }
    }

    fun addNewPartnerReward(title: String, cost: Int, desc: String) {
        viewModelScope.launch {
            val user = repository.userProfile.firstOrNull() ?: return@launch
            val newItem = RewardItem(
                title = title,
                pointsCost = cost,
                description = desc,
                isBuiltIn = false,
                providerName = user.name
            )
            repository.addReward(newItem)
            pushAppNotification(
                "Cupón de emprendedor creado",
                "Tu oferta \"$title\" se ha publicado en el mercado y está disponible para todos."
            )
        }
    }

    // Force simulate quick points addition
    fun triggerSimulatedCaloriesAndPoints() {
        viewModelScope.launch {
            val profile = repository.userProfile.firstOrNull() ?: return@launch
            repository.saveProfile(
                profile.copy(
                    wPoints = profile.wPoints + 500,
                    totalCalories = profile.totalCalories + 120
                )
            )
            pushAppNotification(
                "Datos de Google Fit recibidos",
                "Se han procesado 500 WPoints de entrenamiento."
            )
        }
    }

    fun connectOrDisconnectHealthConnection() {
        viewModelScope.launch {
            val profile = repository.userProfile.firstOrNull() ?: return@launch
            val toggled = !profile.hasConnectedHealth
            repository.saveProfile(
                profile.copy(hasConnectedHealth = toggled)
            )
            val text = if (toggled) "Conectado de manera segura con Google Connect/Fit." else "Conexión con Google Connect desactivada."
            pushAppNotification("Conectividad de Salud", text)
        }
    }
}
