package com.example.ticknow.presentation.initial

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.ticknow.FanViewModel
import com.example.ticknow.R
import com.example.ticknow.ui.theme.BackgroundButton
import com.example.ticknow.ui.theme.Black
import com.example.ticknow.ui.theme.Morado
import com.example.ticknow.ui.theme.MoradoMedio
import com.example.ticknow.ui.theme.Naranja
import com.example.ticknow.ui.theme.NegroClaro
import com.example.ticknow.ui.theme.NegroFuerte
import com.example.ticknow.ui.theme.NegroMedio
import com.example.ticknow.ui.theme.ShapeButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


@Composable
fun InitialScreen(
    navigateToSignUpScreen: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    navigateToHome: () -> Unit,
    fanViewModel: FanViewModel
) {

//    val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        result: ActivityResult ->
//        if(result.resultCode == Activity.RESULT_OK){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                fanViewModel.loginWithGoogle(account.idToken!!,navigateToHome)
//
//            }catch (e:ApiException){
//                Toast.makeText(this, "Ha occurrido un error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


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
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NegroMedio, NegroFuerte),
                    startY = 0f,
                    endY = 800f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Te damos la bienvenida",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "a TickNow", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(250.dp)
        )
        Text(
            "Eficiencia y logros al instante",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navigateToSignUpScreen() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Text(text = "Regístrate con un correo", color = Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))

        //-------------------------------BOTONES GOOGLE Y FACEBOOK ---------------------
        CustomButton(
            Modifier.clickable {
                fanViewModel.onGoogleLoginSelected {
                        googleSignInClient ->
                    // Lanza el intento de inicio de sesión con el cliente obtenido
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            painterResource(id = R.drawable.google),
            "Continuar con Google"
        )
        Spacer(modifier = Modifier.height(18.dp))
//        CustomButton(
//            Modifier.clickable { },
//            painterResource(id = R.drawable.facebook),
//            "Continuar con Facebook"
//        )
        Text(
            text = "Iniciar sesión",
            color = Color.White,
            modifier = Modifier
                .padding(24.dp)
                .clickable { navigateToLoginScreen() },
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CustomButton(modifier: Modifier, painter: Painter, title: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 32.dp)
            .background(BackgroundButton)
            .border(2.dp, ShapeButton, CircleShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 14.dp)
                .size(25.dp)
        )
        Text(
            text = title,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}