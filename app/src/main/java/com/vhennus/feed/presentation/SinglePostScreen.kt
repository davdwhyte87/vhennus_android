package com.vhennus.feed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.R
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.Comment
import com.vhennus.general.utils.CLog
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


@Composable
fun singlePostScreen(id:String, feedViewModel: FeedViewModel, navController: NavController){
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
             feedViewModel.getSinglePosts(id)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            feedViewModel.clearModelData()
        }
    }
    val post = feedViewModel.singlePost.collectAsState()


    GeneralScaffold(
        {BackTopBar("Post", navController)}, {}
    ) {
        Column {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())


                var prettyPostDate = ""
                // Parse the string into a Date object
                try {
                    val parsedDate = inputFormat.parse(post.value.created_at)
                    val prettyTime = PrettyTime()
                    prettyPostDate = prettyTime.format(parsedDate)
                } catch (e: ParseException) {
                    CLog.error("PRETTY DATE ERROR", e.toString())
                }


                // Format the parsed date using PrettyTime

                // profile pic
                val images =listOf(
                    R.drawable.p1,
                    R.drawable.p2,
                    R.drawable.p3,
                    R.drawable.p4,
                    R.drawable.p5
                )
                var randomImage = images[0]
                LaunchedEffect(true) {
                    randomImage = images[Random.nextInt(images.size)]
                }

                val painter = painterResource(id = randomImage)
                Image(
                    painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                // actual post
                Column (
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    Text(text = post.value.user_name, style= MaterialTheme.typography.titleLarge)
                    Text(text = prettyPostDate, style= MaterialTheme.typography.bodySmall)
                    Text(text = post.value.text, style= MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))
                }
            }

            Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            Text(text = "Comments", style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )
            // llist of comments
            LazyColumn {
                items(post.value.comments){it->
                    comment(it)
                }
            }

        }
    }

}

@Composable
fun comment(comment:Comment){
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())
    var prettyPostDate = ""
    // Parse the string into a Date object
    try {
        val parsedDate = inputFormat.parse(comment.created_at)
        val prettyTime = PrettyTime()
        prettyPostDate = prettyTime.format(parsedDate)
    } catch (e: ParseException) {
        CLog.error("PRETTY DATE ERROR", e.toString())
    }

    // ui

    Row (
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // profile pic
        val images = listOf(
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5
        )
        var randomImage = images[0]
        LaunchedEffect(true) {
            randomImage = images[Random.nextInt(images.size)]
        }

        val painter = painterResource(id = randomImage)
        Image(
            painter,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Column (
                horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
        ){

        Text(text = comment.user_name, style= MaterialTheme.typography.titleLarge)
        Text(text = prettyPostDate, style= MaterialTheme.typography.bodySmall)
        Text(text = comment.text, style= MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))
    }
    }

}