package com.example.musify.data.repository.tokenrepository

import com.example.musify.data.remote.token.BearerToken
import com.example.musify.data.utils.FetchedResource
import com.example.musify.domain.MusifyErrorType
import com.example.musify.domain.getAssociatedMusifyErrorType
import retrofit2.HttpException
import java.io.IOException

/**
 * A utility function used to run the [block] with a token retrieved
 * from the [TokenRepository] instance. It returns an instance of
 * [FetchedResource.Success] if the block did not throw an exception.
 * If the block throws either - a [HttpException] or an [IOException],
 * then [FetchedResource.Failure] containing the corresponding exception
 * will be returned. Any other exception thrown by the [block]
 * **will not be caught**.
 */
suspend fun <R> TokenRepository.runCatchingWithToken(block: suspend (BearerToken) -> R): FetchedResource<R, MusifyErrorType> =
    try {
        FetchedResource.Success(block(getValidBearerToken()))
    } catch (httpException: HttpException) {
        FetchedResource.Failure(httpException.getAssociatedMusifyErrorType())
    } catch (ioException: IOException) {
        FetchedResource.Failure(MusifyErrorType.NETWORK_CONNECTION_FAILURE)
    }