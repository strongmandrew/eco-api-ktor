package com.example.utils

enum class Errors(val message: String, val statusCode: Int = 500) {
    DATABASE_ERROR(
        message ="An error occurred trying to reach database",
    ),
    EMPTY_DATA(
        message = "Empty result dataset",
        statusCode = 202
    ),
    INSERT_FAILED(
        message = "Unable to insert data",
    ),
    NOT_FOUND(
        message = "Entity you're looking for cannot be found",
        statusCode = 200
    ),
    UPDATE_FAILED(
        message = "Failed to update database entry",
    ),
    FILE_SYSTEM_ERROR(
        message = "Failed to upload a file"
    ),
    EMAIL_SERVICE_UNAVAILABLE(
        message = "Email service is temporally unavailable",
        statusCode = 500
    ),
    EMAIL_NOT_VERIFIED(
        message = "Verify your email first to authorize",
        statusCode = 202
    ),
    ACTION_NOT_ALLOWED(
        message = "Not enough permissions for the action",
        statusCode = 401
    ),
    BAD_INPUT(
        message = "Make sure your inputs and field names are correct",
        statusCode = 400
    ),
    EMAIL_ALREADY_EXISTS(
        message = "User with the same email already exists",
        statusCode = 200
    )

}