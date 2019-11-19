package com.colisweb.jrubyscaladistances

import com.colisweb.distances.TravelMode

object DistanceTravelMode {

  def drivingMode: TravelMode   = TravelMode.Driving
  def bicyclingMode: TravelMode = TravelMode.Bicycling
  def walkingMode: TravelMode   = TravelMode.Walking
  def transitMode: TravelMode   = TravelMode.Transit
  def unknownMode: TravelMode   = TravelMode.Unknown

}
