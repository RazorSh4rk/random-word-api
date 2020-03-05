scalaVersion := "2.13.1"

name := "random-word-api"
organization := "io.github.razorsh4rk"
version := "1.0"

enablePlugins(JavaAppPackaging)

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
libraryDependencies += "com.lihaoyi" %% "cask" % "0.5.6"