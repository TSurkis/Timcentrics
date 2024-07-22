package com.timcentrics.utils

import com.timcentrics.mockLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class ConsentCostCalculatorTest {

    @Test
    fun testIndividualConsentScores() {
        mockLogger()
        val consentCostCalculator = ExposedNoSpecialCasesConsentCalculator()
        for (consentEntryScore in consentCostCalculator.consentScores) {
            val score = consentCostCalculator.calculate(
                listOf(
                    ServicesConsentData(
                        name = consentEntryScore.key,
                        consentList = listOf(consentEntryScore.key)
                    )
                )
            )

            assertEquals(consentEntryScore.value, score.totalScore)
        }
    }

    @Test
    fun testSpecialCaseBankingSnoopy() {
        mockLogger()
        val consentCostCalculator = ExposedConsentCalculator()
        val consentList = listOf(
            ConsentCostCalculator.PURCHASE_ACTIVITY,
            ConsentCostCalculator.BANK_DETAILS,
            ConsentCostCalculator.CREDIT_AND_DEBIT_CARD_NUMBER,
            // Arbitrary values to avoid the "Good Citizen" Use case:
            ConsentCostCalculator.IP_ADDRESS,
            ConsentCostCalculator.APP_CRASHES
        )
        val actualScore = consentCostCalculator.calculate(
            listOf(
                ServicesConsentData(
                    name = "TestingBankingSnoopy",
                    consentList = consentList
                )
            )
        )

        var expectedScore = 0
        for (consentName in consentList) {
            expectedScore += consentCostCalculator.consentScores[consentName]!!
        }
        expectedScore += (expectedScore * 0.1f).toInt()

        assertEquals(expectedScore, actualScore.totalScore)
    }

    @Test
    fun testSpecialCaseWhyDoYouCare() {
        mockLogger()
        val consentCostCalculator = ExposedConsentCalculator()
        val consentList = listOf(
            ConsentCostCalculator.SEARCH_TERMS,
            ConsentCostCalculator.GEOGRAPHIC_LOCATION,
            ConsentCostCalculator.IP_ADDRESS,
            // Arbitrary values to avoid the "Good Citizen" Use case:
            ConsentCostCalculator.ADVERTISING_IDENTIFIER,
            ConsentCostCalculator.APP_CRASHES
        )
        val actualScore = consentCostCalculator.calculate(
            listOf(
                ServicesConsentData(
                    name = "TestingWhyDoYouCare",
                    consentList = consentList
                )
            )
        )

        var expectedScore = 0
        for (consentName in consentList) {
            expectedScore += consentCostCalculator.consentScores[consentName] ?: 0
        }
        expectedScore += (expectedScore * 0.27f).toInt()

        assertEquals(expectedScore, actualScore.totalScore)
    }


    @Test
    fun testSpecialCaseGoodCitizen() {
        mockLogger()
        val consentCostCalculator = ExposedConsentCalculator()
        val consentList = listOf(
            ConsentCostCalculator.IP_ADDRESS
        )
        val actualScore = consentCostCalculator.calculate(
            listOf(
                ServicesConsentData(
                    name = "TestingGoodCitizen",
                    consentList = consentList
                )
            )
        )

        var expectedScore = consentCostCalculator.consentScores[ConsentCostCalculator.IP_ADDRESS]!!
        expectedScore -= (expectedScore * 0.1f).toInt()

        assertEquals(expectedScore, actualScore.totalScore)
    }

    @Test
    fun testSpecialCaseGoodCitizenMaxValues() {
        mockLogger()
        val consentCostCalculator = ExposedConsentCalculator()
        val consentList = mutableListOf<String>()
        val consentCostList: List<String> = consentCostCalculator.consentScores.keys.toList()
        for (index in 0 until ConsentCostCalculator.GOOD_CITIZEN_MAXIMUM_CONSENTS_CRITERIA) {
            consentList.add(consentCostList[index])
        }
        val actualScore = consentCostCalculator.calculate(
            listOf(
                ServicesConsentData(
                    name = "TestingGoodCitizen",
                    consentList = consentList
                )
            )
        )

        var expectedScore = 0
        for (consentName in consentList) {
            expectedScore += consentCostCalculator.consentScores[consentName] ?: 0
        }
        expectedScore -= (expectedScore * 0.1f).toInt()

        assertEquals(expectedScore, actualScore.totalScore)
    }
}

private class ExposedNoSpecialCasesConsentCalculator : ConsentCostCalculator() {
    val consentScores get() = serviceCostMap

    override val specialCaseCostProcessorsList: List<(consentedServices: List<String>, currentScore: Int) -> Int> =
        listOf()
}

private class ExposedConsentCalculator : ConsentCostCalculator() {
    val consentScores get() = serviceCostMap
}