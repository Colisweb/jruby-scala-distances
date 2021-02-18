organization := "com.colisweb"

scalaVersion := "2.12.10"

scalafmtOnCompile := true
scalafmtCheck := true
scalafmtSbtCheck := true

lazy val root = Project(id = "JRubyScalaDistances", base = file("."))
  .settings(libraryDependencies ++= scalaDistancesLibraries)

resolvers += Resolver.bintrayRepo("colisweb", "maven")

lazy val scalaDistancesVersion = "6.0.0"

lazy val scalaDistancesLibraries = Seq(
  "com.colisweb" %% "scala-distances-core"            % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-provider-google" % scalaDistancesVersion,
  "com.colisweb" %% "scala-distances-cache-redis"     % scalaDistancesVersion
)

inThisBuild(
  List(
    licenses := Seq(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    homepage := Some(
      url("https://gitlab.com/colisweb-open-source/scala/jruby-scala-distances")
    ),
    bintrayOrganization := Some("colisweb"),
    scalacOptions += "-Yresolve-term-conflict:object",
    publishMavenStyle := true,
    isSnapshot := true,
    pushRemoteCacheTo := Some(
      MavenCache("local-cache", baseDirectory.value / sys.env.getOrElse("CACHE_PATH", "sbt-cache"))
    )
  )
)
