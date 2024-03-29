package com.example.domain.usecase.recyclePoint

import com.example.domain.ErrorResponse
import com.example.domain.Response
import com.example.domain.dao.RecyclePointDao
import com.example.utils.ServiceResult

class ChangeRecyclePointApproval (
    private val recyclePointDao: RecyclePointDao
) {

    suspend operator fun invoke(idPoint: Int): Response<Boolean> {

        return when (val approve = recyclePointDao.getPointApprovalById(idPoint)) {

            is ServiceResult.Success -> {

                when (val result = recyclePointDao.changePointApprovalById(idPoint, approve
                    .data.not())) {

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
            is ServiceResult.Error -> {
                Response(
                    statusCode = approve.error.statusCode,
                    error = ErrorResponse(approve.error.name, approve.error.message)
                )
            }
        }

    }

}