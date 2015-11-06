//@formatter:off

import Dependencies._
import Settings._

name := "fun-cqrs-devoxx"
organization in ThisBuild := "io.strongtyped"
scalaVersion in ThisBuild := "2.11.7"


ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Xlint:-infer-any", "-Xfatal-warnings")

addCommandAlias("format", ";scalariformFormat;test:scalariformFormat")


lazy val funCqrs = ProjectRef(file("../../fun-cqrs"), "fun-cqrs-akka")

// dependencies

lazy val root = Project(
  
  id = "fun-cqrs-devoxx",
  base = file("."),
  
  settings = Seq(
    publishArtifact := false,
    routesGenerator := InjectedRoutesGenerator
  ) ++ mainDeps ++ commonSettings

).enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(funCqrs)
  .aggregate(funCqrs) 

//@formatter:on