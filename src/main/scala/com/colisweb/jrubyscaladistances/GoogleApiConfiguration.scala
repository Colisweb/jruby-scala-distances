package com.colisweb.jrubyscaladistances

import scala.concurrent.duration.Duration

final case class GoogleApiConfiguration(
    apiKey: String,
    connectTimeout: Duration,
    readTimeout: Duration,
    queryRateLimit: Int
)
