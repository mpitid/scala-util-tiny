
name := "scala-util-tiny"

organization in ThisBuild := "throwaway.util"

scalacOptions in ThisBuild := Seq("-deprecation", "-feature", "-unchecked", "-optimise")

crossScalaVersions in ThisBuild := Seq("2.10.6", "2.11.7")

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
, "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)

