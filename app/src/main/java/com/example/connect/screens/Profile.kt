package com.example.connect.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.connect.AuthViewModel
import com.example.connect.R
import com.example.connect.item_view.ThreadItem
import com.example.connect.model.UserModel
import com.example.connect.navigation.Routes
import com.example.connect.utils.SharePref
import com.example.connect.viewmodel.AddThreadViewModel
import com.example.connect.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(navController: NavHostController){
    val context= LocalContext.current
    val authViewModel: AuthViewModel= viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val userViewModel: UserViewModel= viewModel()
    val threads by userViewModel.threads.observeAsState(null)

    val user= UserModel(
        name=SharePref.getName(context),
        username = SharePref.getUserName(context),
        imageUrl = SharePref.getImage(context)
    )

    userViewModel.fetchThreads(FirebaseAuth.getInstance().currentUser!!.uid)


    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navController.navigate(Routes.Login.routes) {
                popUpTo(navController.graph.startDestinationId) {inclusive=true}
                launchSingleTop = true
            }
        }
    }

    LazyColumn(){
        item{
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val context = LocalContext.current

                val (text, logo, username, followers, following,bio,button) = createRefs()

                val threadViewModel: AddThreadViewModel = viewModel()

                Text(
                    text = SharePref.getName(context),
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier.constrainAs(text) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                )

                Image(
                    painter = rememberAsyncImagePainter(model = SharePref.getImage(context)),
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .constrainAs(logo) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }, contentScale = ContentScale.Crop
                )

                Text(
                    text = SharePref.getUserName(context),
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.constrainAs(username) {
                        top.linkTo(text.bottom)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = SharePref.getBio(context),
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.constrainAs(bio) {
                        top.linkTo(username.bottom,margin = 3.dp)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text ="0 Followers",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.constrainAs(followers) {
                        top.linkTo(bio.bottom,margin = 5.dp)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = "0 Following",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.constrainAs(following) {
                        top.linkTo(followers.bottom,margin = 2.dp)
                        start.linkTo(parent.start)
                    }
                )

                ElevatedButton(onClick = {
                    authViewModel.logout()
                }, modifier = Modifier
                    .constrainAs(button){
                        top.linkTo(following.bottom,margin = 5.dp)
                        start.linkTo(parent.start)
                    }){
                    Text(text = "Logout")
                }
            }
        }

        items(threads?: emptyList()){pair ->
            ThreadItem(thread = pair, users = user, navHostController = navController
                , userId =FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

}