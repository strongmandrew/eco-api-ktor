package com.example.domain.usecase.recyclePoint

import com.example.domain.ErrorResponse
import com.example.domain.Response
import com.example.domain.dao.RecyclePointDao
import com.example.entity.RecyclePoint
import com.example.utils.ServiceResult

class GetRecyclePointById(
    private val recyclePointDao: RecyclePointDao
) {

    suspend operator fun invoke(idPoint: Int): Response<RecyclePoint> {

        return when (val result = recyclePointDao.getPointById(idPoint)) {
            is ServiceResult.Success -> {
                Response(
                    data = result.data,
                    statusCode = 200
                )

            }
            is ServiceResult.Error -> {
                Response(
                    statusCode = result.error.statusCode,
                    error = ErrorResponse(result.error.name, result.error.message)
                )
            }
        }
    }
}