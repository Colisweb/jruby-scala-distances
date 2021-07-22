package com.colisweb.jrubyscaladistances.here

import com.colisweb.distances.caches.{RedisCache, RedisConfiguration}
import com.colisweb.distances.model.path.DirectedPathWithModeAt
import com.colisweb.distances.model.{DistanceAndDuration, Point, TravelMode}
import com.colisweb.distances.providers.here.{HereRoutingApi, HereRoutingContext, RoutingMode}
import com.colisweb.distances.{DistanceApi, Distances}
import com.colisweb.jrubyscaladistances.JRubyRedisConfiguration
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import scalacache.Flags

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

final class JRubyHereScalaDistance(hereApiConfig: HereApiConfiguration, redisConfig: JRubyRedisConfiguration) {

  val hereContext: HereRoutingContext = HereRoutingContext(
    NonEmptyString.unsafeFrom(hereApiConfig.apiKey),
    hereApiConfig.connectTimeout,
    hereApiConfig.readTimeout
  )

  val distanceApi: DistanceApi[Try, DirectedPathWithModeAt] = {

    import scalacache.modes.try_._
    import scalacache.serialization.circe._
    import io.circe.generic.extras.defaults._
    import io.circe.generic.extras.semiauto._

    implicit val distanceAndDurationCodec: Codec[DistanceAndDuration] = deriveConfiguredCodec

    val redisTtl = redisConfig.expirationTimeout match {
      case duration: FiniteDuration => Some(duration)
      case _                        => None
    }

    Distances
      .from[Try, DirectedPathWithModeAt](
        HereRoutingApi.sync(hereContext)(RoutingMode.MinimalDistanceMode)
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

  def getShortestDrivingDistance(
      origin: Point,
      destination: Point
  ): Try[DistanceAndDuration] = getShortestDistance(origin, destination, TravelMode.Car())

  def getShortestDistance(
      origin: Point,
      destination: Point,
      travelMode: TravelMode
  ): Try[DistanceAndDuration] =
    distanceApi.distance(DirectedPathWithModeAt(origin, destination, travelMode, None))

}
