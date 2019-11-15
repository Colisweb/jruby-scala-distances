organization := "com.colisweb"

scalaVersion := "2.12.8"

scalafmtOnCompile := true
scalafmtCheck := true
scalafmtSbtCheck := true

lazy val root = Project(id = "JRubyScalaDistances", base = file("."))
  .settings(libraryDependencies ++= scalaDistancesLibraries)

lazy val scalaDistancesVersion = "3.0.1"

lazy val scalaDistancesLibraries = Seq(
  "com.colisweb" %% "scala-distances-core"            % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-provider-google" % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-cache-redis"     % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-cache-noCache"   % scalaDistancesVersion
)

inThisBuild(
  List(
    credentials += Credentials(Path.userHome / ".bintray" / ".credentials"),
    licenses := Seq(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    homepage := Some(
      url("https://gitlab.com/colisweb-open-source/jruby-scala-distances")
    ),
    bintrayOrganization := Some("colisweb"),
    bintrayReleaseOnPublish := true,
    scalacOptions += "-Yresolve-term-conflict:object",
    publishMavenStyle := true
  )
)
