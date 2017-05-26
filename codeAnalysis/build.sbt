import sbt.Keys.javaOptions

name := "codeAnalysis"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

fork in Test := true
envVars in Test := Map("testing" -> "y")
baseDirectory in Test := file("..\\")