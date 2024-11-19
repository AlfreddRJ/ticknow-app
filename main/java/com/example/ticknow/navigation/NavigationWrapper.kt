package com.example.ticknow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ticknow.FanViewModel
import com.example.ticknow.presentation.home.HomeScreen
import com.example.ticknow.presentation.initial.InitialScreen
import com.example.ticknow.presentation.login.LoginScreen
import com.example.ticknow.presentation.signup.SignUpScreen
import com.example.ticknow.presentation.splash.SplashScreen

@Composable
fun NavigationWrapper(fanViewModel: FanViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            /*SplashScreen { navController.navigate(Login) }*/
            SplashScreen(
                navigateToHome = { navController.navigate(Home) { navController.popBackStack() } },
                navigateToInitialScreen = { navController.navigate(Initial) { navController.popBackStack() } },
                fanViewModel = fanViewModel
            )
        }

        composable<Initial> {
            InitialScreen(
                navigateToSignUpScreen = { navController.navigate(SignUp) },
                navigateToLoginScreen = { navController.navigate(Login) },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0) // Esto elimina todas las pantallas anteriores en el back stack
                        launchSingleTop = true // Evita duplicados de "home" en el stack
                    }
                },
                fanViewModel = fanViewModel
            )
        }

        composable<Login> {
            LoginScreen(
                navigateToSignUp = { navController.navigate(SignUp) },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0) // Esto elimina todas las pantallas anteriores en el back stack
                        launchSingleTop = true // Evita duplicados de "home" en el stack
                    }
                },
                fanViewModel = fanViewModel
            )
        }

        composable<SignUp> {
            SignUpScreen(
                navigateToLoginScreen = { navController.navigate(Login) },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                fanViewModel = fanViewModel
            )
        }

        composable<Home> {
            HomeScreen(
                navigateToInitialScreen = {
                    navController.navigate(Initial) {
                        popUpTo(0) // Esto elimina todas las pantallas anteriores en el back stack
                        launchSingleTop = true // Evita duplicados de "home" en el stack
                    }
                },
                fanViewModel = fanViewModel,
            )
        }


    }

}


