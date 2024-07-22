package com.timcentrics.utils

import android.util.Log

data class ConsentCostModel(
    val consentServicesCost: List<ConsentServiceCost>,
    val totalScore: Int
)

data class ConsentServiceCost(
    val name: String,
    val cost: Int
) {
    override fun toString(): String = "$name = $cost"
}

data class ServicesConsentData(
    val name: String,
    val consentList: List<String>
)

open class ConsentCostCalculator {
    companion object {
        const val CONFIGURATION_OF_APP_SETTINGS = "Configuration of app settings"
        const val IP_ADDRESS = "IP address"
        const val USER_BEHAVIOUR = "User behaviour"
        const val USER_AGENT = "User agent"
        const val APP_CRASHES = "App crashes"
        const val BROWSER_INFORMATION = "Browser information"
        const val CREDIT_AND_DEBIT_CARD_NUMBER = "Credit and debit card number"
        const val FIRST_NAME = "First name"
        const val GEOGRAPHIC_LOCATION = "Geographic location"
        const val DATE_AND_TIME_OF_VISIT = "Date and time of visit"
        const val ADVERTISING_IDENTIFIER = "Advertising identifier"
        const val BANK_DETAILS = "Bank details"
        const val PURCHASE_ACTIVITY = "Purchase activity"
        const val INTERNET_SERVICE_PROVIDER = "Internet service provider"
        const val JAVASCRIPT_SUPPORT = "JavaScript support"
        const val SEARCH_TERMS = "Search terms"
        const val GOOD_CITIZEN_MAXIMUM_CONSENTS_CRITERIA = 4
    }

    protected val serviceCostMap: Map<String, Int> = mapOf(
        CONFIGURATION_OF_APP_SETTINGS to 1,
        IP_ADDRESS to 2,
        USER_BEHAVIOUR to 2,
        USER_AGENT to 3,
        APP_CRASHES to -2,
        BROWSER_INFORMATION to 3,
        CREDIT_AND_DEBIT_CARD_NUMBER to 4,
        FIRST_NAME to 6,
        GEOGRAPHIC_LOCATION to 7,
        DATE_AND_TIME_OF_VISIT to 1,
        ADVERTISING_IDENTIFIER to 2,
        BANK_DETAILS to 5,
        PURCHASE_ACTIVITY to 6,
        INTERNET_SERVICE_PROVIDER to 4,
        JAVASCRIPT_SUPPORT to -1,
    )

    protected open val specialCaseCostProcessorsList: List<(consentedServices: List<String>, currentScore: Int) -> Int> =
        listOf(
            ::addCostForBankSnoopy,
            ::addCostForWhyDoYouCare,
            ::addCostForGoodCitizen
        )

    private val loggedClassName: String get() = "ConsentCostCalculator"

    fun calculate(servicesConsentData: List<ServicesConsentData>): ConsentCostModel {
        var totalConsentScore = 0
        val consentedServicesList: MutableList<ConsentServiceCost> = mutableListOf()
        for (service in servicesConsentData) {
            var serviceConsentCostScore = 0

            Log.d(
                loggedClassName,
                "Analyzing consent cost for ${service.name}"
            )

            // Check consent cost according to the mapped logic
            for (dataConsented in service.consentList) {
                serviceCostMap[dataConsented]?.let { consentScore ->
                    serviceConsentCostScore += consentScore
                }
            }

            // Check consent cost according to special logic
            for (dataProcessor in specialCaseCostProcessorsList) {
                serviceConsentCostScore += dataProcessor(
                    service.consentList,
                    serviceConsentCostScore
                )
            }

            val consentServiceSummary =
                ConsentServiceCost(
                    name = service.name,
                    cost = serviceConsentCostScore
                )
            consentedServicesList.add(consentServiceSummary)
            Log.d(
                loggedClassName,
                "Analyzing consent cost complete: $consentServiceSummary\n\n"
            )
            totalConsentScore += serviceConsentCostScore
        }
        Log.d(
            loggedClassName,
            "Analyzing total consent cost complete: $totalConsentScore\n\n"
        )
        return ConsentCostModel(consentedServicesList, totalConsentScore)
    }

    private fun addCostForBankSnoopy(consentedServices: List<String>, currentScore: Int): Int =
        addScorePercentageIfConsentedServicesContainData(
            processingName = "'Bank Snoopy'",
            containedConsentedServices = listOf(
                PURCHASE_ACTIVITY,
                BANK_DETAILS,
                CREDIT_AND_DEBIT_CARD_NUMBER
            ),
            percentageAdded = 10,
            consentedServices = consentedServices,
            currentScore = currentScore
        )

    private fun addCostForWhyDoYouCare(consentedServices: List<String>, currentScore: Int) =
        addScorePercentageIfConsentedServicesContainData(
            processingName = "'Why do you care?'",
            containedConsentedServices = listOf(
                SEARCH_TERMS,
                GEOGRAPHIC_LOCATION,
                IP_ADDRESS
            ),
            percentageAdded = 27,
            consentedServices = consentedServices,
            currentScore = currentScore
        )


    private fun addCostForGoodCitizen(consentedServices: List<String>, currentScore: Int): Int =
        if (consentedServices.size <= GOOD_CITIZEN_MAXIMUM_CONSENTS_CRITERIA) {
            Log.d(
                loggedClassName,
                "'Good Citizen' special case detect. Removing 10% from the current score"
            )
            (-currentScore * 0.1).toInt()
        } else {
            0
        }

    private fun addScorePercentageIfConsentedServicesContainData(
        processingName: String,
        containedConsentedServices: List<String>,
        percentageAdded: Int,
        consentedServices: List<String>,
        currentScore: Int
    ): Int =
        if (consentedServices.containsAll(containedConsentedServices)) {
            Log.d(
                loggedClassName,
                "$processingName special case detect. Adding $percentageAdded% to the current score"
            )
            (currentScore * (percentageAdded / 100f)).toInt()
        } else {
            0
        }
}
