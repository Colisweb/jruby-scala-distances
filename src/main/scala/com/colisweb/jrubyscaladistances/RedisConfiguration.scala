package com.colisweb.jrubyscaladistances

import eu.timepit.refined.types.net.PortNumber
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration.Duration

final case class RedisConfiguration(host: NonEmptyString, port: PortNumber, expirationTimeout: Duration)
