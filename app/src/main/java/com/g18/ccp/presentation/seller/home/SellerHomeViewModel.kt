package com.g18.ccp.presentation.seller.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SellerHomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val name = userRepository.getUserName()
            _userName.value = name
        }
    }

}
