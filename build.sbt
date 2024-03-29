organization := "com.colisweb"

scalaVersion := "2.13.8"

scalafmtOnCompile := true
scalafmtCheck := true
scalafmtSbtCheck := true
ThisBuild / pushRemoteCacheTo := Some(
  MavenCache("local-cache", baseDirectory.value / sys.env.getOrElse("CACHE_PATH", "sbt-cache"))
)

lazy val root = Project(id = "JRubyScalaDistances", base = file("."))
  .settings(libraryDependencies ++= scalaDistancesLibraries)

lazy val scalaDistancesVersion = "7.5.2"

lazy val scalaDistancesLibraries = Seq(
  "com.colisweb" %% "scala-distances-core"            % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-provider-google" % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-provider-here"   % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-cache-redis"     % scalaDistancesVersion
)
