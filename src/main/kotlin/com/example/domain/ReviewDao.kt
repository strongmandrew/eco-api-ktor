package com.example.domain

import com.example.entity.Review
import com.example.utils.ServiceResult

interface ReviewDao {
    suspend fun getReviewsByPointId(id: Int): ServiceResult<List<Review>>
    suspend fun postReview(review: Review): ServiceResult<Boolean>
}