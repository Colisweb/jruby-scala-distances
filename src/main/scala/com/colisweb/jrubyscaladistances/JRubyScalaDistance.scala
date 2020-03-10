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
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.Try

final class JRubyScalaDistance(googleApiConfig: GoogleApiConfiguration, redisConfig: RedisConfiguration) {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val logger   = LoggerFactory.getLogger(classOf[OkHttpRequestHandler])
  val loggingF = (message: String) => logger.debug(message.replaceAll("key=([^&]*)&", "key=REDACTED&"))

  val googleGeoApiContext = GoogleGeoApiContext(
    googleApiConfig.apiKey,
    googleApiConfig.connectTimeout,
    googleApiConfig.readTimeout,
    googleApiConfig.queryRateLimit,
    loggingF
  )

  val distanceApi: DistanceApi[IO, GoogleDistanceProviderError] = {

    val distanceProvider = GoogleDistanceProvider[IO](googleGeoApiContext)

    val cache = RedisCache[IO](
      caches.RedisConfiguration(redisConfig.host, redisConfig.port),
      Some(redisConfig.expirationTimeout)
    )
    DistanceApi[IO, GoogleDistanceProviderError](
      distanceProvider.distance,
      distanceProvider.batchDistances,
      cache.caching,
      cache.get,
      // reuse the same key as in scala-distance 3.x
      directedPath => Seq(directedPath.travelMode, directedPath.origin, directedPath.destination, None)
    )
  }

  def getDistance(
      origin: LatLong,
      destination: LatLong,
      travelMode: TravelMode
  ): Try[Types.Distance] =
    distanceApi
      .distance(origin, destination, List(travelMode))
      .unsafeRunSync()
      .getOrElse(travelMode, Left(new RuntimeException("Unknown travelMode exception happened")))
      .toTry

  def getDrivingDistance(
      origin: LatLong,
      destination: LatLong
  ): Try[Types.Distance] = getDistance(origin, destination, TravelMode.Driving)

  def shutdown(): Unit = googleGeoApiContext.geoApiContext.shutdown()

}
