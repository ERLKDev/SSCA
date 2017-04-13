name := "ssca"

version := "1.0"

scalaVersion := "2.11.0"

lazy val ssca =
  project.in( file(".") )
    .aggregate(codeAnalysis, gitCrawler).dependsOn(codeAnalysis, gitCrawler)

lazy val codeAnalysis = project

lazy val gitCrawler = project