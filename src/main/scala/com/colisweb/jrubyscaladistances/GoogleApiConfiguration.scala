package com.colisweb.jrubyscaladistances

import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration.FiniteDuration

final case class GoogleApiConfiguration(
    apiKey: NonEmptyString,
    connectTimeout: FiniteDuration,
    readTimeout: FiniteDuration,
    queryRateLimit: PosInt
)
