package com.example.musify.data.utils

/**
 * A sealed class hierarchy that is used to encapsulate a fetched
 * resource of type [ResourceType]. If any error occurs, the
 * [FailureType] will be used.
 */
sealed class FetchedResource<ResourceType, FailureType> {
    /**
     * A class that encapsulates a successful fetch of a resource of
     * type [ResourceType]. The [data] property  holds the resource.
     */
    data class Success<ResourceType, FailureType>(val data: ResourceType) :
        FetchedResource<ResourceType, FailureType>()

    /**
     * A class that encapsulates an un-successful fetch operation
     * of a resource of type [ResourceType]. The [error] property
     * contains the [FailureType]. The [data] property contains an
     * optional resource that can be used to speify the data to be
     * used in the case of failure.
     */
    data class Failure<ResourceType, FailureType>(
        val error: FailureType,
        val data: ResourceType? = null
    ) : FetchedResource<ResourceType, FailureType>()
}