package com.example.ticknow.presentation.login

import android.annotation.SuppressLint
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.ticknow.FanViewModel
import com.example.ticknow.R
import com.example.ticknow.ui.theme.Black
import com.example.ticknow.ui.theme.NegroFuerte
import com.example.ticknow.ui.theme.SelectedField
import com.example.ticknow.ui.theme.ShapeButton
import com.example.ticknow.ui.theme.UnselectedField
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit,
    fanViewModel: FanViewModel
) {


    var isLoginEnable by rememberSaveable { mutableStateOf(false) }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Declarar FocusRequesters para cada campo de texto
    val passwordFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val emailVerificationStatus = fanViewModel.emailVerificationStatus

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .background(NegroFuerte)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(250.dp)
        )


        Spacer(modifier = Modifier.height(10.dp))

        // Si el correo fue enviado correctamente
        emailVerificationStatus.value?.let {
            Text(text = it, color = Color.Green)
        }
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Ingresa tu email:",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = fanViewModel.email,
            onValueChange = { fanViewModel.email = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocusRequester.requestFocus()
                }
            )

        )


        Spacer(Modifier.height(45.dp))
        Text(
            text = "Ingresa tu contraseña:",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = fanViewModel.password,
            onValueChange = { fanViewModel.password = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField
            ),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                val imagen = if (passwordVisibility) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = imagen, contentDescription = "show password", tint = White)
                }
            },
            visualTransformation = if (passwordVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
        Spacer(Modifier.height(50.dp))

        if (fanViewModel.isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 32.dp),
                color = Color.White
            )
        }

        // Muestra el mensaje de error si existe
        fanViewModel.errorMessage.value?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color(0xFFBF9253),
                containerColor = Color(0xFFE5921D)
            ),
            onClick = {
                if (fanViewModel.email.isEmpty() || fanViewModel.password.isEmpty()) {
                    Log.e("LoginScreen", "Email or password is empty")
                    fanViewModel.errorMessage.value = "Por favor, ingresa tu email y contraseña."
                    return@Button
                }
                fanViewModel.login(fanViewModel.email, fanViewModel.password, navigateToHome)
            },
        ) {
            Text(text = "Inicar sesión", color = Black)
        }

    }
}


@Composable
fun alertDialogDoc() {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Error Inicio de sesión")
            },
            text = {
                Text(
                    "Correo electrónico o contraseña incorrectas, vuelve a intertarlo"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
        )
    }
}

fun enableLogin(email: String, password: String) =
    Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 7


//
//fun LoginScreen(/*navController: NavController, auth: FirebaseAuth,*/ navigateToHome:()->Unit) {
//    var email by rememberSaveable { mutableStateOf("") }
//    var password by rememberSaveable { mutableStateOf("") }
//
//    var isLoginEnable by rememberSaveable { mutableStateOf(false) }
//    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(NegroFuerte)
//            .padding(horizontal = 25.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Spacer(modifier = Modifier.height(80.dp))
////        Text(
////            "INICIO DE SESIÓN",
////            color = White,
////            fontSize = 30.sp,
////            fontWeight = FontWeight.SemiBold
////        )
////        Spacer(modifier = Modifier.height(20.dp))
//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = "",
//            modifier = Modifier
//                .clip(CircleShape)
//                .size(250.dp)
//        )
//        Spacer(modifier = Modifier.height(60.dp))
//        Text(
//            text = "Ingresa tu email:",
//            color = White,
//            fontWeight = FontWeight.Bold,
//            fontSize = 20.sp,
//            modifier = Modifier.align(alignment = Alignment.Start)
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        TextField(
//            value = email,
//            onValueChange = {
//                email = it
//                isLoginEnable = enableLogin(email, password)
//            },
//            modifier = Modifier.fillMaxWidth(),
//            colors = TextFieldDefaults.colors(
//                unfocusedContainerColor = UnselectedField,
//                focusedContainerColor = SelectedField
//            ),
//            maxLines = 1,
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
//        )
//        Spacer(Modifier.height(45.dp))
//        Text(
//            text = "Ingresa tu contraseña:",
//            color = White,
//            fontWeight = FontWeight.Bold,
//            fontSize = 20.sp,
//            modifier = Modifier.align(alignment = Alignment.Start)
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        TextField(
//            value = password,
//            onValueChange = {
//                password = it
//                isLoginEnable = enableLogin(email, password)
//            },
//            modifier = Modifier.fillMaxWidth(),
//            colors = TextFieldDefaults.colors(
//                unfocusedContainerColor = UnselectedField,
//                focusedContainerColor = SelectedField
//            ),
//            maxLines = 1,
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            trailingIcon = {
//                val imagen = if (passwordVisibility) {
//                    Icons.Filled.VisibilityOff
//                } else {
//                    Icons.Filled.Visibility
//                }
//                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
//                    Icon(imageVector = imagen, contentDescription = "show password", tint = White)
//                }
//            },
//            visualTransformation = if (passwordVisibility) {
//                VisualTransformation.None
//            } else {
//                PasswordVisualTransformation()
//            }
//        )
//        Spacer(Modifier.height(50.dp))
//        Button(
//            colors = ButtonDefaults.buttonColors(
//                disabledContainerColor = Color(0xFFBF9253),
//                containerColor = Color(0xFFE5921D)
//            ),
//            onClick = {
////                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
////                    if (task.isSuccessful) {
////                        navController.navigate(AppScreens.HomeScreen.route) {
////                            popUpTo(AppScreens.InitialScreen.route) { inclusive = true }
////                        }
////                        navigateToHome()
////                        Log.i("aris", "LOGIN OK")
////                    } else {
////                        //Error
////                        Log.i("aris", "LOGIN KO")
////                    }
////                }
//            }, enabled = isLoginEnable
//        ) {
//            Text(text = "Inicar sesión", color = Black)
//        }
//
////        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
////            Divider(
////                Modifier
////                    .background(White)
////                    .height(1.dp)
////                    .weight(1f)
////            )
////            Text(
////                text = "O",
////                modifier = Modifier.padding(horizontal = 18.dp),
////                fontSize = 14.sp,
////                fontWeight = FontWeight.Bold,
////                color = Color.White
////            )
////            Divider(
////                Modifier
////                    .background(White)
////                    .height(1.dp)
////                    .weight(1f)
////            )
////
////        }
//    }
//}