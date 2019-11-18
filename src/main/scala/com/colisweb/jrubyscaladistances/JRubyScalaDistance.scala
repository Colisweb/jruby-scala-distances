package com.colisweb.jrubyscaladistances

import cats.effect._
import com.colisweb.distances.Types.LatLong
import com.colisweb.distances.caches.RedisCache
import com.colisweb.distances.providers.google.{
  GoogleDistanceProvider,
  GoogleDistanceProviderError,
  GoogleGeoApiContext
}
import com.colisweb.distances.{DistanceApi, TravelMode, Types, _}
import com.google.maps.OkHttpRequestHandler
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

final class JRubyScalaDistance(googleApiConfig: GoogleApiConfiguration, redisConfig: RedisConfiguration) {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val distanceApi: DistanceApi[IO, GoogleDistanceProviderError] = {
    val logger   = LoggerFactory.getLogger(classOf[OkHttpRequestHandler])
    val loggingF = (message: String) => logger.debug(message.replaceAll("key=([^&]*)&", "key=REDACTED&"))

    val distanceProvider = GoogleDistanceProvider[IO](
      GoogleGeoApiContext(
        googleApiConfig.apiKey,
        googleApiConfig.connectTimeout,
        googleApiConfig.readTimeout,
        googleApiConfig.queryRateLimit,
        loggingF
      )
    )

    val cache = RedisCache[IO](
      caches.RedisConfiguration(redisConfig.host, redisConfig.port),
      Some(redisConfig.expirationTimeout)
    )
    DistanceApi[IO, GoogleDistanceProviderError](
      distanceProvider.distance,
      distanceProvider.batchDistances,
      cache.caching,
      cache.get
    )
  }

  def getDrivingDistance(
      origin: LatLong,
      destination: LatLong,
      travelMode: TravelMode = TravelMode.Driving
  ): Either[GoogleDistanceProviderError, Types.Distance] = {

    distanceApi
      .distance(origin, destination, List(travelMode))
      .unsafeRunSync()
      .getOrElse(travelMode, throw new RuntimeException("Unknown travelMode exception happened"))
  }
}

object JRubyScalaDistance {

  def redisConfiguration(host: String, port: Int, expirationTimeout: Duration): RedisConfiguration =
    RedisConfiguration(
      host = Refined.unsafeApply(host),
      port = Refined.unsafeApply(port),
      expirationTimeout = expirationTimeout
    )

  def googleApiConfiguration(
      apiKey: String,
      connectTimeout: FiniteDuration,
      readTimeout: FiniteDuration,
      queryRateLimit: Int
  ): GoogleApiConfiguration = GoogleApiConfiguration(
    apiKey = Refined.unsafeApply(apiKey),
    connectTimeout = connectTimeout,
    readTimeout = readTimeout,
    queryRateLimit = Refined.unsafeApply(queryRateLimit)
  )

}
