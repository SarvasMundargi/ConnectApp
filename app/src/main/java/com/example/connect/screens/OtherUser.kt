package com.example.connect.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.connect.AuthViewModel
import com.example.connect.item_view.ThreadItem
import com.example.connect.model.UserModel
import com.example.connect.navigation.Routes
import com.example.connect.utils.SharePref
import com.example.connect.viewmodel.AddThreadViewModel
import com.example.connect.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OtherUsers(navController: NavHostController,uid: String){
    val context= LocalContext.current
    Log.d("Otherusers id","$uid")
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val userViewModel: UserViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)
    val users by userViewModel.users.observeAsState(null)


    userViewModel.fetchThreads(uid)
    userViewModel.fetchUser(uid)

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
                    text = users!!.name,
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
                    painter = rememberAsyncImagePainter(model = users!!.imageUrl),
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
                    text = users!!.username,
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.constrainAs(username) {
                        top.linkTo(text.bottom)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = users!!.bio,
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
            }
        }

        if(threads!=null && users!=null){
            items(threads?: emptyList()){pair ->
                ThreadItem(thread = pair, users = users!!, navHostController = navController
                    , userId = FirebaseAuth.getInstance().currentUser!!.uid)
            }
        }
    }
}