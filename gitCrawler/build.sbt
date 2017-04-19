
name := "gitCrawler"

version := "1.0"

scalaVersion := "2.11.0"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-lift-json" % "0.11.3"
libraryDependencies += "com.bacoder.jgit" % "org.eclipse.jgit" % "3.1.0-201309071158-r"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"