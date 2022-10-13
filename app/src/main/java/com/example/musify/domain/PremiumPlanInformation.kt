package com.example.musify.domain

import androidx.compose.ui.graphics.Color
import com.example.musify.domain.PremiumPlanInformation.PricingInformation

/**
 * A class that models a premium plan that is available for purchase.
 * The [darkColor] & [lightColor] properties will be used for generating
 * a gradient.
 *
 * @param id the id of the plan.
 * @param name the name of the plan.
 * @param highlights a list of highlights of the plan.
 * @param termsAndConditions the terms and conditions of the plan.
 * @param pricingInformation an instance of [PricingInformation] that is
 * related to the plan.
 * @param darkColor the dark color that is to be used in the gradient.
 * @param lightColor the light color that is to be used in the gradient.
 */
data class PremiumPlanInformation(
    val id: String,
    val name: String,
    val highlights: List<String>,
    val termsAndConditions: String,
    val pricingInformation: PricingInformation,
    val darkColor:Color,
    val lightColor:Color
) {
    /**
     * A class that contains pricing information related to an instance of
     * [PremiumPlanInformation].
     * @param associatedCardId the id of the [PremiumPlanInformation] instance
     * that this information is associated with.
     * @param cost the cost of the plan.
     * @param term the subscription term of the plan.
     */
    data class PricingInformation(
        val associatedCardId: String,
        val cost: String,
        val term: String
    )
}