package com.example.ticknow.presentation.signup

import android.app.Activity
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavHostController
import com.example.ticknow.FanViewModel
import com.example.ticknow.R
import com.example.ticknow.presentation.initial.CustomButton
import com.example.ticknow.presentation.login.enableLogin
import com.example.ticknow.ui.theme.BackgroundButton
import com.example.ticknow.ui.theme.Black
import com.example.ticknow.ui.theme.Naranja
import com.example.ticknow.ui.theme.NegroFuerte
import com.example.ticknow.ui.theme.SelectedField
import com.example.ticknow.ui.theme.ShapeButton
import com.example.ticknow.ui.theme.UnselectedField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    navigateToHome: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    fanViewModel: FanViewModel,
) {


    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()


    val emailVerificationStatus = fanViewModel.emailVerificationStatus
    val isVerifying = fanViewModel.isVerifying  // Estado que indica si se está verificando el correo

    // Declarar FocusRequesters para cada campo de texto
    val passwordFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }


    val context = LocalContext.current


    // Configura el launcher para el resultado de inicio de sesión
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Llama a la función del ViewModel para iniciar sesión con Google
                fanViewModel.loginWithGoogle(account.idToken!!) {
                    navigateToHome()
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(NegroFuerte)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(200.dp)
        )
        Text(
            "Regístrate para comenzar",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "con tu productividad",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))


        Text(
            text = "Correo electrónico:",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        //--------------------TextField CORREO-----------------------------
        TextField(
            value = fanViewModel.email,
            onValueChange = { fanViewModel.onEmailChange(it) },
            isError = fanViewModel.emailError != null,
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
            ),
        )

        //Mostrar mensaje de error en  el correo
        fanViewModel.emailError?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,


                )
        }
        Spacer(Modifier.height(30.dp))

        //--------------------TextField CONTRASEÑA-----------------------------
        Text(
            text = "Contraseña:",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = fanViewModel.password,
            onValueChange = { fanViewModel.onPasswordChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            isError = fanViewModel.passwordError != null,
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
//            keyboardActions = KeyboardActions(
//                onDone = { keyboardController?.hide() } // Oculta el teclado al terminar
//            ),
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

        //Mostrar mensaje de error en  la contraseña
        fanViewModel.passwordError?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(30.dp))

        //-------------------------------BOTÓN PARA EL REGISTRO----------------------
        Button(
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color(0xFFBF9253),
                containerColor = Color(0xFFE5921D)
            ),
            onClick = {
                fanViewModel.register(
                    email = fanViewModel.email,
                    password = fanViewModel.password,
                    onEmailVerificationSent = {
                        // Muestra el mensaje de verificación pendiente
                        fanViewModel.sendEmailVerification()
                        fanViewModel.email=""
                        fanViewModel.password=""
                        navigateToLoginScreen()


                    }

                )

            },
            enabled = fanViewModel.isFormValid(),
            ) {
            Text(text = "Registrarme", color = Black)
        }

//        // Si el correo fue enviado correctamente
//        emailVerificationStatus.value?.let {
//            Text(text = it, color = Color.Green)
//        }

        // Si el correo está en proceso de ser verificado
        if (isVerifying.value) {
            CircularProgressIndicator() // Muestra un indicador de carga
            Text(text = "Verificando tu correo, por favor revisa tu bandeja de entrada.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(25.dp))

        //------------------------------- FILA DE DIVISIÓN---------------------
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(
                Modifier
                    .background(White)
                    .height(2.dp)
                    .weight(1f)
            )
            Text(
                text = "O",
                modifier = Modifier.padding(horizontal = 18.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            HorizontalDivider(
                Modifier
                    .background(White)
                    .height(2.dp)
                    .weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(15.dp))

        //-------------------------------BOTONES GOOGLE Y FACEBOOK ---------------------

        CustomButton2(
            Modifier.clickable {
                fanViewModel.onGoogleLoginSelected {
                        googleSignInClient ->
                    // Lanza el intento de inicio de sesión con el cliente obtenido
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            painterResource(id = R.drawable.google),
            "Continúa con Google"
        )


        Spacer(modifier = Modifier.height(18.dp))

        //-------------------------------FILA PARA INCIIAR SESIÓN SI YA TIENES CUENTA ---------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Text(
                text = " Inicia sesión aquí",
                color = Color(0xFFD19A24),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navigateToLoginScreen() }
            )


        }
    }
}


@Composable
fun CustomButton2(modifier: Modifier, painter: Painter, title: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 32.dp)
            .background(BackgroundButton),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 30.dp)
                .size(20.dp)
        )
        Text(
            text = title,
            color = White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,

            )
    }
}


