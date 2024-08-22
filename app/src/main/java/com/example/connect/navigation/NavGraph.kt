package com.example.connect.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.connect.screens.AddThreads
import com.example.connect.screens.BottomNav
import com.example.connect.screens.Home
import com.example.connect.screens.Login
import com.example.connect.screens.Notification
import com.example.connect.screens.OtherUsers
import com.example.connect.screens.Profile
import com.example.connect.screens.Register
import com.example.connect.screens.Search
import com.example.connect.screens.Splash

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.routes
    ){

        composable(Routes.Splash.routes){
            Splash(navController)
        }

        composable(Routes.Home.routes){
            Home(navController)
        }

        composable(Routes.Notification.routes){
            Notification()
        }

        composable(Routes.Search.routes){
            Search(navController)
        }

        composable(Routes.AddThread.routes){
            AddThreads(navController)
        }

        composable(Routes.Profile.routes){
            Profile(navController)
        }

        composable(Routes.BottomNav.routes){
            BottomNav(navController)
        }

        composable(Routes.Login.routes){
            Login(navController)
        }

        composable(Routes.Register.routes){
            Register(navController)
        }

        composable(Routes.OtherUsers.routes) { backStackEntry ->
            val userId = backStackEntry.arguments!!.getString("userId")
            OtherUsers(navController, userId!!)
//            if (userId != null) {
//                OtherUsers(navController, userId)
//            } else {
//                Log.e("NavGraph", "User ID is null")
//                navController.popBackStack()
//            }
        }
    }
}