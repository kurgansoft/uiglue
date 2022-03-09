ThisBuild / scalaVersion     := "2.13.8"

val zioVersion = "1.0.4-2"

lazy val core = (project in file(".")).enablePlugins(ScalaJSPlugin)
  .settings(
    name := "uiglue",
    baseDirectory := file("core"),
    sourceDirectory := baseDirectory.value / "src",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "org.scala-js" %%% "scala-js-macrotask-executor"  % "1.0.0",      
    )
  )