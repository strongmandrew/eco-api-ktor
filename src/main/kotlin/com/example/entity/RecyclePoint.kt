package com.example.entity

import kotlinx.serialization.Serializable

@Serializable
data class RecyclePoint(
    val id: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val streetName: String,
    val streetHouseNum: String,
    val locationDescription: String? = null,
    val photoPath: String? = null,
    val totalRating: Int? = null,
    val totalReviews: Int? = null,
    val approved: Boolean? = null,
    val type: String,
    val rubbishTypes: List<RubbishType>
)
