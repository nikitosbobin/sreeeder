package ru.debajo.reader.rss.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Material3TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeholder: @Composable (() -> Unit)? = null,
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        TextField(
            modifier = modifier,
            value = value,
            placeholder = placeholder,
            interactionSource = interactionSource,
            colors = material3Colors(),
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun material3Colors(): TextFieldColors {
    val textColor = LocalContentColor.current.copy(LocalContentAlpha.current)
    val unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
    val leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
    val trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
    val unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
    val placeholderColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)

    return TextFieldDefaults.textFieldColors(
        textColor = textColor,
        disabledTextColor = textColor.copy(ContentAlpha.disabled),
        backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
        cursorColor = MaterialTheme.colorScheme.primary,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = unfocusedIndicatorColor.copy(alpha = ContentAlpha.disabled),
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        leadingIconColor = leadingIconColor,
        disabledLeadingIconColor = leadingIconColor.copy(alpha = ContentAlpha.disabled),
        errorLeadingIconColor = leadingIconColor,
        trailingIconColor = trailingIconColor,
        disabledTrailingIconColor = trailingIconColor.copy(alpha = ContentAlpha.disabled),
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = unfocusedLabelColor.copy(ContentAlpha.disabled),
        errorLabelColor = MaterialTheme.colorScheme.error,
        placeholderColor = placeholderColor,
        disabledPlaceholderColor = placeholderColor.copy(ContentAlpha.disabled)
    )
}