package com.colisweb.jrubyscaladistances.here

import scala.concurrent.duration.Duration

final case class HereApiConfiguration(
    apiKey: String,
    connectTimeout: Duration,
    readTimeout: Duration
)
