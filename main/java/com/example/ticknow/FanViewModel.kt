package com.example.ticknow

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.ticknow.data.AuthService
import com.example.ticknow.presentation.home.Task

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class FanViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authService: AuthService,
) : ViewModel() {

    //-------------------------------VARIABLES-----------------------------------------
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var emailExiste by mutableStateOf(false)


    // Estado de errores de validación
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    val emailVerificationStatus = mutableStateOf<String?>(null)
    val isVerifying = mutableStateOf(false)


    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    // State para almacenar el nombre del usuario actual
    private val _currentUserName = mutableStateOf<String?>(null)
    val currentUserName: State<String?> = _currentUserName


    var errorMessage = mutableStateOf<String?>(null) // Estado para el mensaje de error


    //OBTENER DATOS DEL USUARIO
    val userName = mutableStateOf<String?>(null)
    val userPhotoUrl = mutableStateOf<Uri?>(null)

//    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
//    val tasks: StateFlow<List<Task>>  get() = _tasks

    val currentUser: FirebaseUser?
        get() = authService.getCurrentUser()


    fun loadUserProfile() {
        userName.value = authService.getUserName()
        userPhotoUrl.value = authService.getUserPhotoUrl()
    }


    //--------------------------FUNCIÓN PARA EL INICIO DE SESÍON-------------------------------------------
    fun login(email: String, password: String, navigateToDetail: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    authService.login(email, password)
                }
                if (result != null) {
                    loadTasks() // Cargar tareas del usuario
                    navigateToDetail()
                    Log.d("FanViewModel", "¡Logueado con éxito!: $result")
                } else {
                    Log.e("FanViewModel", "Error logging in: $result")
                    errorMessage.value = "Usuario o contraseña incorrecta"
                }
            } catch (e: Exception) {
                errorMessage.value = "Usuario o contraseña incorrecta" // Muestra mensaje de error
                Log.e("FanViewModel", "Error logging in: $e")
            } finally {
                _isLoading.value = false
            }

        }
    }


    //--------------------------FUNCIONES QUE INCLUYE EL REGISTRO DE USUARIS-------------------------------------------
    fun register(email: String, password: String, onEmailVerificationSent: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    authService.register(email, password)
                }
                if (result != null) {
                    onEmailVerificationSent()
                    Log.d("FanViewModel", "¡Registrado con éxito!: $result")
                } else {
                    emailVerificationStatus.value = "Error al registrar usuario"
                    Log.e("FanViewModel", "Error registering: $result")
                }
            } catch (e: Exception) {
                Log.e("FanViewModel", "Error registering: ${e.message.orEmpty()}")
                emailExiste = true
            }
            _isLoading.value = false
        }
    }

    fun sendEmailVerification() {
        isVerifying.value = true  // Establece el estado de verificación a 'true'

        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            isVerifying.value = false  // Cambia el estado cuando se haya completado
            if (task.isSuccessful) {
                emailVerificationStatus.value =
                    "Correo de verificación enviado. Revisa tu bandeja de entrada verifica e inicia sesión"

            } else {
                emailVerificationStatus.value = "Error al enviar correo de verificación."
            }
        }
    }


    // Métodos para actualizar y validar el correo
    fun onEmailChange(newEmail: String) {
        email = newEmail
        emailError = if (!isValidEmail(newEmail)) "Correo no válido" else null
    }

    // Métodos para actualizar y validar la contraseña
    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = validatePassword(newPassword)
    }

    // Validación de correo
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validación de contraseña
    private fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !password.any { it.isUpperCase() } -> "La contraseña debe tener una letra mayúscula"
            !password.any { it.isLowerCase() } -> "La contraseña debe tener una letra minúscula"
            !password.any { it.isDigit() } -> "La contraseña debe tener un número"
            else -> null // Contraseña válida
        }
    }

    // Función para verificar si se cumplen todas las validaciones
    fun isFormValid(): Boolean {
        return emailError == null && passwordError == null
    }


    // ----------------------FUNCIÓN PARA SABER SI EL SUAURIO ESTÁ LOGUEADO-----------------------------------------
    private fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn()
    }


    // ----------------------FINCIÓN PARA SABER A DÓNDE SE DIRIGE SI EL USUARIOE ESTÁ LOGUEADO---------------------
    fun checkDestination(
        navigateToHome: () -> Unit,
        navigateToInitial: () -> Unit
    ) {
        val isUserLoggedIn = isUserLoggedIn()
        if (isUserLoggedIn) {
            navigateToHome()

        } else {
            navigateToInitial()
        }
    }

    //-------------------FUNCIÓN PARA INICIO DE SESIÓN CON GOOGLE-----------------
    fun onGoogleLoginSelected(googleLauncherLogin: (GoogleSignInClient) -> Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherLogin(gsc)
    }

    fun loginWithGoogle(idToken: String, navigateToHome: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.loginWithGoogle(idToken)
            }

            if (result != null) {
                navigateToHome()
            }
        }
    }


    //---------------LÓGICA PARA CARGAR TAREA-----------

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks


    init {
        observeAuthState()
    }

    // Cargar las tareas asociadas al usuario autenticado
    private fun loadTasks() {
//        val userId = currentUser?.uid ?: return
//        firestore.collection("tasks")
//            .whereEqualTo("userId", userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null || snapshot == null) return@addSnapshotListener
//                _tasks.value = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
//                Log.d("FanViewModel", "Tareas cargadas: ${_tasks.value.size}")
//            }


//        val userId = currentUser?.uid ?: return
//        firestore.collection("tasks")
//            .whereEqualTo("userId", userId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null || snapshot == null) {
//                    if (error != null) {
//                        Log.e("FanViewModel", "Error al cargar tareas: ${error.message}")
//                    }
//                    return@addSnapshotListener
//                }
//
//                // Verifica qué datos están llegando desde Firestore
//                snapshot.documents.forEach { doc ->
//                    Log.d("FanViewModel", "Tarea cargada: ${doc.data}")
//                }
//
//                // Actualiza la lista de tareas
//                _tasks.value = snapshot.documents.mapNotNull { doc ->
//                    doc.toObject(Task::class.java)?.copy(id = doc.id)
//                }
//            }

        //FUNCIONA

        val userId = currentUser?.uid ?: return
        firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val newTasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }

                // Combina el estado actual de las tareas con las nuevas tareas de Firestore
                _tasks.value = newTasks.map { newTask ->
                    val localTask = _tasks.value.find { it.id == newTask.id }
                    localTask
                        ?: newTask // Si existe en local, usa la tarea local, si no, usa la nueva
                }
                Log.d("FanViewModel", "Tareas cargadas: ${_tasks.value.size}")
            }


    }


    // Agregar una nueva tarea
    fun addTask(title: String, description: String?) {
        viewModelScope.launch {
            val userId = currentUser?.uid ?: return@launch
            val newTask = Task(
                id = firestore.collection("tasks").document().id,
                title = title,
                description = description,
                userId = userId,
                isCompleted = false // Inicializa con false
            )
            try {
                // Agrega la tarea a Firestore
                firestore.collection("tasks")
                    .document(newTask.id)
                    .set(newTask)
                    .await()

                // Opcionalmente, actualiza el estado local
                loadTasks()
            } catch (e: Exception) {
                // Manejo de errores
                Log.e("AddTask", "Error al agregar tarea: ${e.message}")
            }
        }
    }

    // Eliminar una tarea
    fun deleteTask(taskId: String) {
        firestore.collection("tasks").document(taskId).delete()
    }

    // Marcar tarea como completada/incompleta
    fun toggleTaskCompletion(taskId: String) {
//        viewModelScope.launch {
//            try {
//                val updatedTasks = _tasks.value.map { task ->
//                    if (task.id == taskId) {
//                        // Cambiar el estado local
//                        val newTask = task.copy(isCompleted = !task.isCompleted)
//
//                        // Actualizar en Firestore
//                        firestore.collection("tasks").document(taskId)
//                            .update("isCompleted", newTask.isCompleted)
//                            .await()
//                        newTask
//                    } else {
//
//                        task
//                    }
//                }
//                // Actualizar la lista local
//                _tasks.value = updatedTasks
//            } catch (e: Exception) {
//                Log.e("FanViewModel", "Error al cambiar el estado de la tarea: ${e.message}")
//            }
//        }

        viewModelScope.launch {
            try {
                // Busca la tarea que quieres actualizar
                val taskToUpdate = _tasks.value.find { it.id == taskId } ?: return@launch

                // Actualiza el estado localmente
                val updatedTask = taskToUpdate.copy(isCompleted = !taskToUpdate.isCompleted)



                // Actualiza en Firestore
                firestore.collection("tasks").document(taskId)
                    .update("isCompleted", updatedTask.isCompleted)
                    .await()

                // Actualiza la lista local
                _tasks.value = _tasks.value.map { task ->
                    if (task.id == taskId) updatedTask else task
                }

            } catch (e: Exception) {
                Log.e("FanViewModel", "Error al cambiar el estado de la tarea: ${e.message}")
            }
        }

    }


    fun updateTask(updatedTask: Task) {
////        viewModelScope.launch {
////            try {
////                firestore.collection("tasks").document(task.id).set(task).await()
////                Log.e("FanViewModel", "TareaActualizada")
////
////            } catch (e: Exception) {
////                Log.e("FanViewModel", "Error actualizando la tarea: ${e.message}")
////            }
////        }
//
//
//        viewModelScope.launch {
//            try {
//                // Aquí puedes actualizar la tarea en Firestore.
//                val user = currentUser
//                if (user != null) {
//                    val tasksCollection = firestore.collection("tasks").document(user.uid).collection("userTasks")
//                    tasksCollection.document(updatedTask.id).set(updatedTask).await()
//
//                    // Actualizar la lista local de tareas si es necesario.
//                    _tasks.value = _tasks.value.map { task ->
//                        if (task.id == updatedTask.id) updatedTask else task
//                    }
//                }
//            } catch (e: Exception) {
//                // Maneja errores de actualización
//                Log.e("UpdateTask", "Error al actualizar la tarea: ${e.message}")
//            }
//        }

        //FUNCONA
        viewModelScope.launch {
            try {
                // Actualizar Firestore
                firestore.collection("tasks")
                    .document(updatedTask.id)
                    .set(updatedTask)
                    .await()

                // Actualizar la lista local
                _tasks.value = _tasks.value.map { task ->
                    if (task.id == updatedTask.id) updatedTask else task
                }

                Log.d("FanViewModel", "Tarea actualizada correctamente: ${updatedTask.id}")
            } catch (e: Exception) {
                Log.e("FanViewModel", "Error al actualizar la tarea: ${e.message}")
            }
        }
    }

    private fun clearTasks() {
        _tasks.value = emptyList() // Limpia la lista de tareas
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                authService.logout() // Cierra sesión en Firebase Authentication
                clearTasks() // Limpia la lista de tareas
                onLogoutComplete() // Navega a la pantalla inicial
            } catch (e: Exception) {
                Log.e("FanViewModel", "Error al cerrar sesión: ${e.message}")
            }
        }
    }


    fun observeAuthState() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val newUser = auth.currentUser
            if (newUser != null) {
                // Usuario ha iniciado sesión
                loadTasks()
            } else {
                // Usuario ha cerrado sesión
                clearTasks()
            }
        }
    }
}


