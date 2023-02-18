package com.example.plugins

import com.example.data.NewsDaoImpl
import com.example.data.RecyclePointDaoImpl
import com.example.data.ReviewDaoImpl
import com.example.data.UserDaoImpl
import com.example.domain.dao.NewsDao
import com.example.domain.dao.RecyclePointDao
import com.example.domain.dao.ReviewDao
import com.example.domain.dao.UserDao
import com.example.domain.usecase.email.CodeGenerator
import com.example.domain.usecase.email.EmailAuthenticator
import com.example.domain.usecase.email.EmailCredentials
import com.example.domain.usecase.email.EmailService
import com.example.domain.usecase.news.GetNews
import com.example.domain.usecase.recyclePoint.*
import com.example.domain.usecase.review.DeleteReviewById
import com.example.domain.usecase.review.GetReviewById
import com.example.domain.usecase.review.GetReviewsByPointId
import com.example.domain.usecase.review.InsertReview
import com.example.domain.usecase.user.ApproveValidation
import com.example.domain.usecase.user.SendValidation
import com.example.domain.usecase.user.RegisterUser
import com.example.domain.usecase.user.SetOrUpdateValidationCode
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import javax.mail.Session

fun Application.configureKoin() {

    install(Koin) {
        slf4jLogger()
        modules(listOf(recyclePointModule, reviewModule,
            newsModule, userModule))
    }
}

val recyclePointModule = module {
    single<RecyclePointDao> { RecyclePointDaoImpl() }

    factory { RecyclePointFileNameGenerator() }

    single { ChangeRecyclePointApproval(get()) }
    single { GetRecyclePointById(get()) }
    single { GetRecyclePoints(get()) }
    single { InsertRecyclePoint(get()) }
    single { SetRecyclePointPhoto(get(), get()) }
    single { DownloadPointPhoto(get()) }
    single { DeleteRecyclePoint(get()) }
}

val reviewModule = module {

    single<ReviewDao> {ReviewDaoImpl()}

    single { GetReviewsByPointId(get()) }
    single { GetReviewById(get()) }
    single { InsertReview(get()) }
    single { DeleteReviewById(get()) }
}

val newsModule = module {
    single<NewsDao> { NewsDaoImpl() }

    single { GetNews(get()) }
}

val userModule = module {



    single<UserDao> { UserDaoImpl() }

    val session = Session.getInstance(EmailCredentials.props,
        EmailAuthenticator().invoke())

    factory { EmailService(session) }
    factory { CodeGenerator() }
    single { RegisterUser(get()) }
    single { SetOrUpdateValidationCode(get()) }
    single { SendValidation(get(), get(), get()) }
    single { ApproveValidation(get()) }
}