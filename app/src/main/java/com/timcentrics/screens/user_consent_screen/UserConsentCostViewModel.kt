package com.timcentrics.screens.user_consent_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timcentrics.user_consent.IUserConsentManager
import com.timcentrics.utils.ConsentCostCalculator
import com.timcentrics.utils.ConsentCostModel
import com.timcentrics.utils.ServicesConsentData
import com.usercentrics.sdk.Usercentrics
import com.usercentrics.sdk.UsercentricsConsentUserResponse
import com.usercentrics.sdk.errors.UsercentricsError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class UserConsentCostStatus {
    IDLE,
    LOADING,
    GENERAL_ERROR,
}

class UserConsentCostState(
    val status: UserConsentCostStatus,
    val consentScore: Int? = 0
)

class UserConsentCostViewModel(
    private val userConsentManager: IUserConsentManager,
    private val userConsentCostCalculator: ConsentCostCalculator
) : ViewModel() {
    private val _userConsentScreenState: MutableLiveData<UserConsentCostState> =
        MutableLiveData(UserConsentCostState(status = UserConsentCostStatus.IDLE, consentScore = 0))
    val userConsentScreenState get() = _userConsentScreenState as LiveData<UserConsentCostState>

    fun showConsentPrompt() {
        _userConsentScreenState.value =
            UserConsentCostState(
                status = UserConsentCostStatus.LOADING,
                consentScore = _userConsentScreenState.value?.consentScore
            )
        userConsentManager.display(::onResponse, ::onError)
    }

    private fun onResponse(response: UsercentricsConsentUserResponse?) {
        viewModelScope.launch(Dispatchers.IO) {
            // Find only the services the user consented to from the SDK's response
            val consentedServices =
                response?.consents?.filter { consent -> consent.status }?.toList() ?: emptyList()

            val usercentricsData = Usercentrics.instance.getCMPData()
            val consentCost: ConsentCostModel = userConsentCostCalculator.calculate(
                usercentricsData.services
                    // From all the provided services filter only the services the user has consented to
                    .filter { service ->
                        consentedServices.find { consentedService -> consentedService.templateId == service.templateId } != null
                    }
                    // Map the consented services to the required list in the calculator to check cost
                    .map { service ->
                        ServicesConsentData(
                            name = service.nameOfProcessingCompany,
                            consentList = service.dataCollectedList
                        )
                    }
            )

            _userConsentScreenState.postValue(
                UserConsentCostState(
                    status = UserConsentCostStatus.IDLE,
                    consentScore = consentCost.totalScore
                )
            )
        }
    }

    private fun onError(error: UsercentricsError?) {
        _userConsentScreenState.value =
            UserConsentCostState(
                status = UserConsentCostStatus.GENERAL_ERROR,
                consentScore = _userConsentScreenState.value?.consentScore
            )
    }
}