package com.example.routes

import com.example.domain.usecase.recyclePoint.*
import com.example.domain.usecase.review.GetReviewsByPointId
import com.example.domain.usecase.review.InsertReview
import com.example.domain.usecase.userTakeOff.TakeOffRubbish
import com.example.entity.RecyclePoint
import com.example.entity.Review
import com.example.entity.UserTakeOff
import com.example.utils.Const
import com.example.utils.Const.DEFAULT_ID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.recyclePointRoute() {

    val getRecyclePoints: GetRecyclePoints by inject()
    val getRecyclePointById: GetRecyclePointById by inject()
    val setRecyclePointPhoto: SetRecyclePointPhoto by inject()
    val changeRecyclePointApproval: ChangeRecyclePointApproval by inject()
    val insertRecyclePoint: InsertRecyclePoint by inject()
    val deleteRecyclePoint: DeleteRecyclePoint by inject()
    val getRecyclePointByQuery: GetRecyclePointByQuery by inject()
    val getRecyclePointsFilteredByType: GetPointsFilteredByType by inject()
    val takeOffRubbish: TakeOffRubbish by inject()

    val getReviewsByPointId: GetReviewsByPointId by inject()
    val insertReview: InsertReview by inject()

    val addAcceptedRubbishType: AddAcceptedRubbishType by inject()

    route(Endpoint.RECYCLE_POINT.path) {

        authenticate("user-auth") {
            route(Endpoint.PHOTO.path) {

                static {
                    staticRootFolder = File(Const.PHOTO_PATH)
                    files(".")
                }
            }

            get {

                val query = call.request.queryParameters["query"]
                val filter = call.request.queryParameters["filter"]

                val queried = query?.let { queried ->
                    getRecyclePointByQuery(queried)
                        .data?.toSet()
                }

                val filtered = filter?.let { filtered ->
                    getRecyclePointsFilteredByType(filtered)
                        .data?.toSet()
                }


                return@get when {
                    queried == null && filtered == null -> {
                        val result = getRecyclePoints()
                        return@get call.respond(
                            message = result,
                            status = HttpStatusCode.fromValue(result.statusCode)
                        )
                    }

                    queried == null ->
                        call.respond(
                            message = filtered!!,
                            status = HttpStatusCode.OK
                        )

                    filtered == null -> call.respond(
                        message = queried,
                        status = HttpStatusCode.OK
                    )


                    else -> call.respond(
                        message = filtered.intersect(queried),
                        status = HttpStatusCode.OK
                    )
                }
            }

            post {

                val principal = call.principal<JWTPrincipal>()
                val uid =
                    principal!!.payload.getClaim("uid").asInt()
                val point = call.receive<RecyclePoint>()

                val result = insertRecyclePoint(point.copy(userIdProposed = uid))
                call.respond(
                    message = result,
                    status = HttpStatusCode.fromValue(result.statusCode)
                )

            }
        }

        route("/{id}") {

            authenticate("admin-auth") {
                patch(Endpoint.APPROVE.path) {

                    val id = call.parameters["id"]
                    val result = changeRecyclePointApproval(
                        id?.toInt() ?: DEFAULT_ID
                    )
                    call.respond(
                        message = result,
                        status = HttpStatusCode.fromValue(result.statusCode)
                    )
                }

                delete {
                    val id = call.parameters["id"]
                    val result =
                        deleteRecyclePoint(id?.toInt() ?: DEFAULT_ID)
                    call.respond(
                        message = result,
                        status = HttpStatusCode.fromValue(result.statusCode)
                    )
                }
            }

            authenticate("user-auth") {
                route(Endpoint.ACCEPTED.path) {
                    post {
                        val id = call.parameters["id"]

                        val type = call.request
                            .queryParameters["type"] ?: return@post

                        val response = addAcceptedRubbishType(
                            id?.toInt() ?: DEFAULT_ID,
                            type
                        )
                        call.respond(
                            message = response,
                            status = HttpStatusCode.fromValue(response.statusCode)
                        )
                    }
                }
                
                route(Endpoint.TAKE_OFF.path) {
                    post {
                        val id = call.parameters["id"]?.toInt() ?: DEFAULT_ID
                        val takeOff = call.receive<UserTakeOff>()

                        val principal = call.principal<JWTPrincipal>()
                        val uid =
                            principal!!.payload.getClaim("uid").asInt()
                        val result = takeOffRubbish(takeOff.copy(
                            idUser = uid,
                            idRecyclePoint = id
                        ))
                        call.respond(
                            message = result,
                            status = HttpStatusCode.fromValue(result.statusCode)
                        )
                    }
                }

                get {

                    val id = call.parameters["id"]
                    val result =
                        getRecyclePointById(id?.toInt() ?: DEFAULT_ID)
                    call.respond(
                        message = result,
                        status = HttpStatusCode.fromValue(result.statusCode)
                    )
                }

                route(Endpoint.PHOTO.path) {

                    patch {

                        val id = call.parameters["id"]
                        val extension = call.request
                            .queryParameters["ext"]

                        val channel = call.receiveChannel()

                        val result = setRecyclePointPhoto(
                            channel,
                            extension
                                ?: throw IllegalStateException(),
                            id?.toInt() ?: DEFAULT_ID
                        )
                        call.respond(
                            message = result,
                            status = HttpStatusCode.fromValue(result.statusCode)
                        )
                    }

                }

                route(Endpoint.REVIEW.path) {

                    get {

                        val id = call.parameters["id"]

                        val result = getReviewsByPointId(
                            id?.toInt() ?: DEFAULT_ID
                        )
                        call.respond(
                            message = result,
                            status = HttpStatusCode.fromValue(result.statusCode)
                        )
                    }

                    post {

                        val review = call.receive<Review>()

                        val principal = call.principal<JWTPrincipal>()
                        val uid =
                            principal!!.payload.getClaim("uid").asInt()

                        val id = call.parameters["id"]

                        val result = insertReview(
                            review,
                            id?.toInt() ?: DEFAULT_ID,
                            uid
                        )
                        call.respond(
                            message = result,
                            status = HttpStatusCode.fromValue(result.statusCode)
                        )
                    }
                }
            }
        }
    }

}