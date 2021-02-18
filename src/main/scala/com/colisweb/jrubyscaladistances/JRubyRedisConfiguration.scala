package com.colisweb.jrubyscaladistances

import scala.concurrent.duration.Duration

final case class JRubyRedisConfiguration(
    host: String,
    port: Int,
    expirationTimeout: Duration
)
