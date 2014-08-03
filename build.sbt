name := "SensorCoverage"

version := "1.0"

crossPaths := false

resolvers += "My bitbucket maven releases repo" at "https://bitbucket.org/eloipereira/maven-repo-releases/raw/master"

resolvers += DefaultMavenRepository

resolvers += "Java.net repository" at "http://download.java.net/maven/2"

resolvers += "Open Source Geospatial Foundation Repository" at "http://download.osgeo.org/webdav/geotools/"

resolvers += "OpenGeo Maven Repository" at "http://repo.opengeo.org"

libraryDependencies += "gov.nist.math" % "jama" % "1.0.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "de.micromata.jak" % "JavaAPIforKml" % "2.2.0-SNAPSHOT"

publishTo <<= version { (v: String) =>
  if (v.trim.endsWith("SNAPSHOT"))
    Some(Resolver.file("file",  new File( Path.userHome.absolutePath+"/.m2/repository/snapshots" )) )
  else
    Some(Resolver.file("file",  new File( Path.userHome.absolutePath+"/.m2/repository/releases" )) )
}