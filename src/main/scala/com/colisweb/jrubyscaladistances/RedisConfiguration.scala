package com.colisweb.jrubyscaladistances

import scala.concurrent.duration.Duration

final case class RedisConfiguration(
    host: String,
    port: Int,
    expirationTimeout: Duration
)
