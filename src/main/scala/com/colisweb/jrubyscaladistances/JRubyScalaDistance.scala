package com.colisweb.jrubyscaladistances

import cats.effect._
import com.colisweb.distances.caches.{RedisCache, RedisConfiguration}
import com.colisweb.distances.model.path.DirectedPathWithModeAt
import com.colisweb.distances.model.{DistanceAndDuration, Point, TravelMode}
import com.colisweb.distances.providers.google.TrafficModel.BestGuess
import com.colisweb.distances.providers.google.{
  GoogleDistanceDirectionsApi,
  GoogleDistanceDirectionsProvider,
  GoogleGeoApiContext
}
import com.colisweb.distances.{DistanceApi, Distances}
import com.google.maps.OkHttpRequestHandler
import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import org.slf4j.LoggerFactory
import scalacache.Flags
import io.circe.generic.extras.defaults._
import io.circe.generic.extras.semiauto._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

final class JRubyScalaDistance(googleApiConfig: GoogleApiConfiguration, redisConfig: JRubyRedisConfiguration) {

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

  val distanceApi: DistanceApi[Try, DirectedPathWithModeAt] = {

    import scalacache.modes.try_._
    import scalacache.serialization.circe._

    implicit val distanceAndDurationCodec: Codec[DistanceAndDuration] = deriveConfiguredCodec

    val redisTtl = redisConfig.expirationTimeout match {
      case duration: FiniteDuration => Some(duration)
      case _                        => None
    }

    Distances
      .from[Try, DirectedPathWithModeAt](
        GoogleDistanceDirectionsApi.sync(googleGeoApiContext, BestGuess)(
          GoogleDistanceDirectionsProvider.chooseMinimalDistanceRoute
        )
      )
      .caching(
        RedisCache(
          RedisConfiguration(redisConfig.host, redisConfig.port),
          Flags.defaultFlags,
          redisTtl
        )
      )
      .api
  }

  def getShortestDistance(
      origin: Point,
      destination: Point,
      travelMode: TravelMode
  ): Try[DistanceAndDuration] =
    distanceApi.distance(DirectedPathWithModeAt(origin, destination, travelMode, None))

  def getShortestDrivingDistance(
      origin: Point,
      destination: Point
  ): Try[DistanceAndDuration] = getShortestDistance(origin, destination, TravelMode.Driving)

  def shutdown(): Unit = googleGeoApiContext.geoApiContext.shutdown()

}
