package com.example.ticknow.presentation.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ticknow.FanViewModel
import com.example.ticknow.R
import com.example.ticknow.ui.theme.Morado
import com.example.ticknow.ui.theme.MoradoClaro
import com.example.ticknow.ui.theme.MoradoMedio
import com.example.ticknow.ui.theme.NegroClaro
import com.example.ticknow.ui.theme.NegroFuerte
import com.example.ticknow.ui.theme.NegroMedio
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToInitialScreen: () -> Unit,
    fanViewModel: FanViewModel
) {


    val navController= rememberNavController()
    val scale = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(durationMillis = 700,
                easing = {
                    OvershootInterpolator(7f).getInterpolation(it)
                }
            )
        )

        navController.popBackStack()
        delay(1200)
        fanViewModel.checkDestination(
            navigateToHome = navigateToHome,
            navigateToInitial = navigateToInitialScreen
        )

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NegroClaro, NegroMedio, NegroFuerte))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "TickNow ",
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(15.dp))
        Image(
            modifier = Modifier
                .scale(scale.value)
                .size(300.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Splash",

            )

        Text(
            text = "Eficiencia y logros al instante",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(90.dp))
        CircularProgressIndicator(color = Color(0xFFFB4844))
    }

}
