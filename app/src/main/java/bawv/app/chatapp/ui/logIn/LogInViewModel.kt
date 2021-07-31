package com.plcoding.streamchatapp.ui.logIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.streamchatapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LogInViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {


    private val _logInEvent = MutableSharedFlow<LogInEvent>()
    val logInEvent = _logInEvent.asSharedFlow()

    private fun isValidUsername(username: String) =
        username.length >= Constants.MIN_USERNAME_LENGTH


    fun connectUser(username: String) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {
            if (isValidUsername(trimmedUsername)) {
                val result = client.connectGuestUser(
                    userId = trimmedUsername,
                    username = trimmedUsername
                ).await()
                if (result.isError) {
                    _logInEvent.emit(
                        LogInEvent.ErrorLogIn(
                            result.error().message ?: "Unknown error"
                        )
                    )
                    return@launch
                }
                _logInEvent.emit(LogInEvent.Success)
            }else{
                _logInEvent.emit(LogInEvent.ErrorInputTooShort)
            }
        }
    }

    sealed class LogInEvent {
        object ErrorInputTooShort : LogInEvent()
        data class ErrorLogIn(val error: String) : LogInEvent()
        object Success : LogInEvent()
    }

}