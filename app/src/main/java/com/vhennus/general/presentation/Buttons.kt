package com.vhennus.general.presentation

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.profile.presentation.updateBioValidation
import com.vhennus.ui.AnimatedPreloader


@Composable
fun AppButtonLarge(
    text:String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isIcon: Boolean = false,
    icon: ImageVector? = null,
    onclick: ()-> Unit
){
    Button(onClick = {
        onclick()
    },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(30.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if(isLoading){
            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
        }else {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isIcon && icon != null) Icon(icon, "") else null
                Text(text = text, modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

        }

    }
}