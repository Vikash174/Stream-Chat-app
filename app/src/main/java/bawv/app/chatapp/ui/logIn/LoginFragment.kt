package com.plcoding.streamchatapp.ui.logIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.FragmentLoginBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.Constants
import com.plcoding.streamchatapp.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment:BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val viewModel:LogInViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnConfirm.setOnClickListener {
               setupConnectingUiState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }

        binding.etUsername.addTextChangedListener{
            binding.etUsername.error = null
        }
        subscribeToEvent()

    }


    private fun subscribeToEvent(){
        lifecycleScope.launchWhenCreated {
            viewModel.logInEvent.collect(){event ->
                when(event){
                    is LogInViewModel.LogInEvent.ErrorInputTooShort ->{
                        setupIdleUiState()
                           binding.etUsername.error = getString(R.string.error_username_too_short,Constants.MIN_USERNAME_LENGTH)
                    }
                    is LogInViewModel.LogInEvent.ErrorLogIn ->{
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            event.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is LogInViewModel.LogInEvent.Success ->{
                        setupIdleUiState()
                      findNavController().navigateSafely(
                          R.id.action_loginFragment_to_channelFragment
                      )
                    }
                }
            }
        }
    }

    private fun setupConnectingUiState(){
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }
    private fun setupIdleUiState(){
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }

}