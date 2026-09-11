package com.kian.mahmoudi.vegang.data.config

sealed class FetchStage {
    data object Idle : FetchStage()
    data object Downloading : FetchStage()
    data object TcpTesting : FetchStage()
    data object RealPinging : FetchStage()
    data object GeoLocating : FetchStage()
    data object Done : FetchStage()
}

data class FetchStatus(
    val stage: FetchStage = FetchStage.Idle,
    val totalFetched: Int = 0,
    val tcpPassed: Int = 0,
    val tested: Int = 0,
    val healthy: Int = 0,
    val attempt: Int = 1,
    val maxAttempts: Int = 3
)