package com.example.myapplication.compose.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.R

@Composable
fun DialogText(text: String, modifier: Modifier, selected: Boolean = false) {
    ConstraintLayout(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxSize()
    ) {
        val (textRef, iconRef) = createRefs()

        Text(text = text, modifier = Modifier.constrainAs(textRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            if (selected) {
                end.linkTo(iconRef.start)
            } else {
                end.linkTo(parent.end)
            }
            width = Dimension.fillToConstraints
        })
        if (selected) {
            Icon(
                Icons.Rounded.Check,
                stringResource(id = R.string.check_icon_description),
                modifier = Modifier
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(iconRef.end)
                        width = Dimension.wrapContent
                    }
                    .padding(start = 4.dp)
            )
        }
    }
}