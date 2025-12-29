ThisBuild / scalaVersion := "3.3.5"

val zioVersion = "2.1.24"

lazy val core = project.settings(
    name := "uiglue",
    baseDirectory := file("core"),
    sourceDirectory := baseDirectory.value / "src",
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % zioVersion,
      "dev.zio" %%% "zio-streams" % zioVersion,
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
      "io.github.cquiroz" %%% "scala-java-time" % "2.6.0"
    )
  ).enablePlugins(ScalaJSPlugin)

lazy val examples = project.settings(
  name := "Examples",
  baseDirectory := file("examples"),
  sourceDirectory := baseDirectory.value / "src",
  skip / publish := true,
  Compile / scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.8.1",
    "com.github.japgolly.scalajs-react"  %%% "core" % "2.1.3"
  )
).dependsOn(core).enablePlugins(ScalaJSPlugin)
