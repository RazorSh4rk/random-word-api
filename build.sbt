scalaVersion := "2.13.1"

name := "random-word-api"
organization := "io.github.razorsh4rk"
version := "1.0"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.8.1",
"com.lihaoyi" %% "cask" % "0.5.6",
"com.lihaoyi" %% "requests" % "0.6.5",
"net.debasishg" %% "redisclient" % "3.30",
"com.lihaoyi" %% "utest" % "0.7.5" % Test)

testFrameworks += new TestFramework("utest.runner.Framework")
