
# JRuby Scala Distances  
  
JRuby Scala Distances is a simple API to use ScalaDistance from JRuby.  

## Why ?

In JRuby, it's impossible to use the library [ScalaDistance](https://gitlab.com/colisweb-open-source/scala-distances) directly from your code. For example you cant get a TravelMode
directly from JRuby.
Also, it's difficult to pass the required implicit ContextShift to call ScalaDistance.

So with JRuby Scala Distances you don't have to deal with this kind of problem.

## How to use it ?

Simpy create a RedisConfiguration and a GoogleApiConfiguration objects : 
```ruby
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

```
     
  
 and then create your `JRubyScalaDistance` :
 
```ruby
    scalaDistanceApi = JRubyScalaDistance.new(googleApiConfig, redisConfig)
```

  
Now you can call the method `getDrivingDistance` like this : 
  
```ruby
    origin = com.colisweb.distances.Types::LatLong.new(41.63253, -40.27242)
    destination = com.colisweb.distances.Types::LatLong.new(-57.24373, -52.27306)
      
    scalaDistanceApi  
      .getDrivingDistance(origin, destination)  
      .fold(  
        -> (failure) { failure },  
        -> (distance) { Success({ distance: distance.length.value, duration: distance.duration.toMinutes }) }  
      )
```


Then don't forget to add an `at_exit` in your ruby code to cleanup all threads at exits :

```ruby
at_exit do
  scalaDistanceApi.shutdown if scalaDistanceApi.present?
end
```
