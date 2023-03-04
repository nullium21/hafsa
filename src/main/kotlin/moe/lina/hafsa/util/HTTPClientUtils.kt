package moe.lina.hafsa.util

import arrow.core.Either
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

suspend inline fun <reified T> HttpClient.fetch(url: String): Either<HttpStatusCode, T> =
    get(url).let {
        if (it.status == HttpStatusCode.OK) Either.Right(it.body())
        else Either.Left(it.status)
    }
