package com.example.musify.domain

import androidx.compose.ui.graphics.Color
import com.example.musify.domain.PremiumPlanInformation.PricingInformation

/**
 * A class that models a premium plan that is available for purchase.
 * The [colorInformation] property will be used for generating
 * a gradient.
 *
 * @param id the id of the plan.
 * @param name the name of the plan.
 * @param highlights a list of highlights of the plan.
 * @param termsAndConditions the terms and conditions of the plan.
 * @param pricingInformation an instance of [PricingInformation] that is
 * related to the plan.
 */
data class PremiumPlanInformation(
    val id: String,
    val name: String,
    val highlights: List<String>,
    val termsAndConditions: String,
    val pricingInformation: PricingInformation,
    val colorInformation: ColorInformation
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

    /**
     * A data class that contains the [gradientEndColor] and [gradientStartColor]
     * that are associated with a particular [PremiumPlanInformation] instance.
     */
    data class ColorInformation(
        val gradientStartColor: Color,
        val gradientEndColor: Color
    )
}

/**
 * A list of premium plans that are available by default.
 */
val defaultPremiumPlans = listOf(
    PremiumPlanInformation(
        id = "premium_mini",
        name = "Mini",
        highlights = listOf(
            "1 day and 1 week plans",
            "Ad-free music on mobile",
            "Download 30 songs on 1 mobile device",
            "Mobile only plan"
        ),
        termsAndConditions = "Prices vary according to duration of plan. Terms and conditions apply.",
        pricingInformation = PricingInformation(
            associatedCardId = "premium_mini",
            cost = "From $7",
            term = "For 1 day"
        ),
        colorInformation = PremiumPlanInformation.ColorInformation(
            gradientStartColor = Color(0xFF4F99F4),
            gradientEndColor = Color(0xFF2F4ABC)
        )
    ),
    PremiumPlanInformation(
        id = "premium_individual",
        name = "Premium Individual",
        highlights = listOf(
            "Ad-free music listening",
            "Download to listen offline",
            "Debit and credit cards accepted"
        ),
        termsAndConditions = "Offer only for users who are new to Premium. Terms and conditions apply.",
        pricingInformation = PricingInformation(
            associatedCardId = "premium_individual",
            cost = "Free",
            term = "For 1 month"
        ),
        colorInformation = PremiumPlanInformation.ColorInformation(
            gradientStartColor = Color(0xFF045746),
            gradientEndColor = Color(0xFF16A96A)

        )
    ),
    PremiumPlanInformation(
        id = "premium_duo",
        name = "Premium Duo",
        highlights = listOf(
            "2 Premium accounts",
            "For couples who live together",
            "Ad-free music listening",
            "Download 10,000 songs/device, on up to 5 devices per account",
            "Choose 1, 3, 6 or 12 months of Premium",
            "Debit and credit cards accepted"
        ),
        termsAndConditions = "Offer only for users who are new to Premium. Terms and conditions apply.",
        pricingInformation = PricingInformation(
            associatedCardId = "premium_duo",
            cost = "Free",
            term = "For 1 month"
        ),
        colorInformation = PremiumPlanInformation.ColorInformation(
            gradientStartColor = Color(0xff5992C2),
            gradientEndColor = Color(0xff3F3F76)
        )
    ),
    PremiumPlanInformation(
        id = "premium_family",
        name = "Premium Family",
        highlights = listOf(
            "Add-free music listening",
            "Choose 1, 3, 6 or 12 months of Premium",
            "Ad-free music listening",
            "Debit and credit cards accepted"
        ),
        termsAndConditions = "Offer only for users who are new to Premium. Terms and conditions apply.",
        pricingInformation = PricingInformation(
            associatedCardId = "premium_family",
            cost = "Free",
            term = "For 1 month"
        ),
        colorInformation = PremiumPlanInformation.ColorInformation(
            gradientStartColor = Color(0xFF213265),
            gradientEndColor = Color(0xFF972A8E)
        )
    ),
    PremiumPlanInformation(
        id = "premium_student",
        name = "Premium Student",
        highlights = listOf(
            "Add-free music listening",
            "Download to listen offline",
        ),
        termsAndConditions = "Offer available only to students at an accredited higher education institution. Terms and conditions apply.",
        pricingInformation = PricingInformation(
            associatedCardId = "premium_family",
            cost = "Free",
            term = "For 1 month"
        ),
        colorInformation = PremiumPlanInformation.ColorInformation(
            gradientStartColor = Color(0xFFF49A24),
            gradientEndColor = Color(0xFFB27049)
        )
    )
)