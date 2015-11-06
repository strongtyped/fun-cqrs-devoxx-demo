import sbt.Keys._
import sbt._


object Dependencies {  

  //------------------------------------------------------------------------------------------------------------
  val scalaLogging          =  "com.typesafe.scala-logging" %%  "scala-logging"     % "3.1.0"
  
  val macwireVersion        =  "1.0.7"
  val macwireMacros         =  "com.softwaremill.macwire"   %%  "macros"            % macwireVersion
  val macwireRuntime        =  "com.softwaremill.macwire"   %%  "runtime"           % macwireVersion

  val scalaTest             =  "org.scalatest"              %% "scalatest"          % "3.0.0-M10" % "test"

  val mainDeps = Seq(
    libraryDependencies ++= Seq(
      scalaLogging, 
      macwireRuntime,
      macwireMacros,
      scalaTest
    )
  )
  //------------------------------------------------------------------------------------------------------------

}