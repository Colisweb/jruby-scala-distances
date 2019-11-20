
# JRuby Scala Distances  
  
JRuby Scala Distances is simple interface to use ScalaDistance from JRuby.  

## Why ?

JRuby Scala Distances  allow us to use the lib ScalaDistance directly from you jruby code. 
For example, it's not possible to get a TravelMode from directly from ScalaDistance, and it's easier to use.

Also, you don't have to pass the implicit required ContextShift to call it.

## How to use it ?

Simpy create a RedisConfiguration and a googleApiConfiguration object like this : 

    redisConfig = RedisConfiguration.new(  
      redis_host, # host  
      redis_port, # port  
      Duration.apply("1s") # expirationTimeout  
    )  
      
    googleApiConfig = GoogleApiConfiguration.new(  
      gmap_api_key, # apiKey  
      Duration.apply("1s"), # connectTimeout  
      Duration.apply("2s"), # readTimeout  
      50 # queryRateLimit  
    ) 
  
 and then create your `JRubyScalaDistance` :
 

    ::ScalaDistanceApi = JRubyScalaDistance.new(googleApiConfig, redisConfig)

  
  Now you can call the method `getDrivingDistance` like this : 
  

    origin = com.colisweb.distances.Types::LatLong.new(41.63253, -40.27242)
    destination = com.colisweb.distances.Types::LatLon≤g.new(-57.24373, -52.27306)
      
    ScalaDistanceApi  
      .getDrivingDistance(origin, destination)  
      .fold(  
        -> (failure) { failure },  
        -> (distance) { Success({ distance: distance.length.value, duration: distance.duration.toMinutes }) }  
      )

