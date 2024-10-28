package com.vhennus.trivia.presentation

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.trivia.data.TriviaViewModel
import com.vhennus.trivia.domain.TriviaGame
import com.vhennus.trivia.domain.TriviaGameReq
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray
import com.vhennus.ui.theme.Purple
import com.vhennus.ui.theme.White
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.Wallet
import java.util.Calendar
import kotlin.coroutines.coroutineContext


@Composable
fun triviaPage(
    navController: NavController,
    triviaViewModel: TriviaViewModel,
    walletViewModel: WalletViewModel
){
    GeneralScaffold(
        topBar = { BackTopBar("Trivia", navController) },
        floatingActionButton ={}
    ) {
        //
        val triviaUIState = triviaViewModel.triviaUIState.collectAsState()
        val triviaGame = triviaViewModel.trivaGame.collectAsState()
        val lifecycleOwner = LocalLifecycleOwner.current
        val wallets = walletViewModel.allWallets.collectAsState()

        DisposableEffect(true) {

            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    // get game
                    triviaViewModel.resetUiState()
                    triviaViewModel.getTriviaGame()
                    walletViewModel.getAllWallets()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                triviaViewModel.resetUiState()
            }
        }


        if(triviaUIState.value.isGetGameError){
            Toast.makeText(LocalContext.current, triviaUIState.value.getGameError, Toast.LENGTH_SHORT).show()
            triviaViewModel.resetUiState()
        }


//        if (triviaUIState.value.isGetGameSuccess){
//
//
//        }else{
//            nonGame()
//        }

        if(!triviaUIState.value.isGetGameSuccess){
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().height( 100.dp).shimmerEffect())
                Spacer(modifier = Modifier.height(height = 50.dp).fillMaxWidth())
                Box(modifier = Modifier.size(200.dp,300.dp).shimmerEffect()){}


            }
        }else{
            game(triviaGame.value, wallets.value, triviaViewModel, navController)
        }
    }
}

@Composable
fun nonGame(){
    val context = LocalContext.current
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Trivia Starts 6:00 pm", style = MaterialTheme.typography.titleLarge)
        Text("Players get asked fun questions and riddles," +
                " the fastest person to get the correct answer wins 33,333 vhenncoins." +
                " You can exchange your vhenncoins for local currency. ",
            modifier = Modifier.padding(all = 20.dp)
        )
        Button(onClick = {
            val today = getToday6PM()
            setAlarm(context, today.timeInMillis)
            Toast.makeText(context, "Alarm set for ${today.time}", Toast.LENGTH_SHORT).show()
        }) {
            Text("Set Reminder")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun game(
    game: TriviaGame,
    wallets: List<Wallet>,
    triviaViewModel: TriviaViewModel,
    navController: NavController
){
    val context = LocalContext.current
    val isWalletSelected = remember { mutableStateOf(false) }
    val isAnswerSelected = remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var selectedItemWallet by remember { mutableStateOf("Select an item") }

    val triviaUIState = triviaViewModel.triviaUIState.collectAsState()


    LaunchedEffect(triviaUIState.value.isPlayGameSuccess) {
        if (triviaUIState.value.isPlayGameSuccess){
            navController.navigate(NavScreen.PostTriviaPage.route)
        }
    }


    LaunchedEffect(triviaUIState.value.isPlayGameError) {
        if(triviaUIState.value.isPlayGameError){
            Toast.makeText(context, triviaUIState.value.playGameErrorMessage, Toast.LENGTH_SHORT).show()
            triviaViewModel.resetUIE(triviaUIState.value.copy(isPlayGameError = false))
        }
    }

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp),

  ) {
      Text(game.trivia_question.question,
          modifier = Modifier.padding(all = 5.dp)
      )

      //answers

      Box (modifier = Modifier.fillMaxWidth()){

          Column {
              val options = game.trivia_question.options

              options.forEach{option->
                  Row (
                      modifier = Modifier.selectable(
                          selected = (option == selectedOption),
                          onClick = {
                              selectedOption = option
                              isAnswerSelected.value = true
                          }
                      ),
                      verticalAlignment = Alignment.CenterVertically,

                  ) {
                      RadioButton(
                          selected =(option == selectedOption),
                          onClick = {
                              selectedOption = option
                              isAnswerSelected.value = true
                          }
                      )
                      Text( text = option,
                          modifier = Modifier.padding(start = 8.dp)
                      )
                  }
              }
          }
      }

      // play button

      Button(onClick = {
          val req = TriviaGameReq(selectedOption,selectedItemWallet)
          //validation
          if (!isWalletSelected.value){
              Toast.makeText(context, "Please select a wallet", Toast.LENGTH_SHORT).show()
              return@Button
          }
          if (!isAnswerSelected.value){
              Toast.makeText(context, "Please select an answer", Toast.LENGTH_SHORT).show()
              return@Button
          }
          triviaViewModel.playTriviaGame(req)

      },
          modifier = Modifier.size(width = 250.dp, height = 40.dp)
      ) {
          if(triviaUIState.value.isPlayGameLoading){
              AnimatedPreloader(modifier = Modifier.size(size = 100.dp), MaterialTheme.colorScheme.surface)
          }else{
              Text("Play")
          }

      }



      Box(
          modifier = Modifier.fillMaxWidth()
      ) {
          var expanded by remember { mutableStateOf(false) }


          val items = wallets

          Text("Select Wallet", style = MaterialTheme.typography.titleLarge)

          ExposedDropdownMenuBox(
              expanded = expanded,
              onExpandedChange = { expanded = !expanded }
          ) {
              TextField(
                  value = selectedItemWallet,
                  onValueChange = { },
                  readOnly = true,  // Prevent typing
                  label = { Text("Dropdown") },
                  trailingIcon = {
                      Icon(
                          imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                          contentDescription = null
                      )
                  },
                  modifier = Modifier
                      .menuAnchor() // Required for proper positioning of the dropdown
                      .fillMaxWidth()
              )


              DropdownMenu(
                  expanded = expanded,
                  onDismissRequest = { expanded = false },  // Close when clicking outside
                  modifier = Modifier.fillMaxWidth()
              ) {
                  items.forEach { item ->
                      DropdownMenuItem(
                          text =  {Text(text = item.walletAddress)},
                          onClick = {

                              selectedItemWallet = item.walletAddress
                              expanded = false
                              isWalletSelected.value = true
                          }
                      )
                  }
              }

          }
//          OutlinedTextField(
//              value = selectedItem,
//              onValueChange = { },
//              readOnly = true,  // Prevent typing in the field
//              label = { Text("Dropdown") },
//              modifier = Modifier
//                  .fillMaxWidth()
//                  .clickable { expanded = true },  // Click to show dropdown
//              trailingIcon = {
//                  Icon(
//                      imageVector = Icons.Default.ArrowDropDown,
//                      contentDescription = null
//                  )
//              }
//          )


      }


  }
}


fun setAlarm(context:Context, time:Long){
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
//        if (ContextCompat.checkSelfPermission(context, notificationPermission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions( arrayOf(notificationPermission), YOUR_REQUEST_CODE)
//        }
//    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)

}

fun triviaAlarmNotificationChannel(context: Context){
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationChannel = NotificationChannel(
        "trivia_alarm",
        "trivia_alarm",
        NotificationManager.IMPORTANCE_HIGH
    )
    notificationManager.createNotificationChannel(notificationChannel)
}

fun getToday6PM(): Calendar {
    // Get an instance of Calendar set to today's date
    val calendar = Calendar.getInstance()

    // check if the time is past 6pm
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    if (currentHour>18){
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    // Set the hour of the day to 6 PM (18:00 in 24-hour format)
    calendar.set(Calendar.HOUR_OF_DAY, 8)
    calendar.set(Calendar.MINUTE, 15)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar
}

class AlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Alarm ringing!", Toast.LENGTH_LONG).show()
        Log.d("ALARMXXXXX", " hello ranger")
        val channelID = "trivia_alarm"


        context?.let {ctx->
            triviaAlarmNotificationChannel(ctx)
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(ctx, channelID )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Vhennus Trivia")
                .setContentText("It's time to play today's game")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(1, builder.build())
        }
    }
}


fun isAlarmSet(context: Context, requestCode: Int): Boolean {
    val intent = Intent(context, AlarmReceiver::class.java)

    // Check if the alarm is already set by using FLAG_NO_CREATE
    val pendingIntent = PendingIntent.getBroadcast(
        context, requestCode, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )

    // If pendingIntent is not null, the alarm is set
    return pendingIntent != null
}


fun Modifier.shimmerEffect():Modifier = composed{

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition()
    val shimmerOffset by transition.animateFloat(
        initialValue = -2*size.width.toFloat(),
        targetValue = 2*size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val shimmerColors = listOf(
        Gray,
        White,
        Gray
    )

    // Create a linear gradient brush for the shimmer effect
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerOffset, 0f),
        end = Offset(
            x =shimmerOffset+ size.width.toFloat() ,
            y = size.height.toFloat()
        )
    )

    background(
        brush
    ).onGloballyPositioned { size = it.size }

}



