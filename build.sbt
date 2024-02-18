ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """passwordKeeper""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "com.opencsv" % "opencsv" % "5.7.1",
      "org.flywaydb" %% "flyway-play" % "7.37.0",
      "org.postgresql" % "postgresql" % "42.5.4",
      "io.getquill" %% "quill-jdbc-zio" % "4.8.0",
    )
  )