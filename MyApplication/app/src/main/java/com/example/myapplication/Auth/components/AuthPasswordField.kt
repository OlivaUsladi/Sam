package com.example.myapplication.Auth.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.myapplication.Auth.components.AuthOutlinedField
import com.example.myapplication.Auth.theme.AuthColors

@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    isVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null
) {
    AuthOutlinedField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        isError = isError,
        supportingText = supportingText,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            if (onVisibilityToggle != null) {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isVisible) "Скрыть пароль" else "Показать пароль",
                        tint = AuthColors.PlaceholderGrey
                    )
                }
            }
        }
    )
}