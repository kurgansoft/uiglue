ThisBuild / scalaVersion     := "2.13.10"

val zioVersion = "2.0.12"

lazy val core = project.settings(
    name := "uiglue",
    baseDirectory := file("core"),
    sourceDirectory := baseDirectory.value / "src",
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % zioVersion,
      "dev.zio" %%% "zio-streams" % zioVersion,
      "org.scala-js" %%% "scala-js-macrotask-executor"  % "1.1.1",
      "io.github.cquiroz" %%% "scala-java-time"      % "2.5.0"
    )
  ).enablePlugins(ScalaJSPlugin)

lazy val examples = project.settings(
  name := "Examples",
  baseDirectory := file("examples/ui"),
  sourceDirectory := baseDirectory.value / "src",
  skip / publish := true,
  Compile / scalaJSUseMainModuleInitializer := true,
  Compile / npmDependencies ++= Seq(
    "react" -> "17.0.2",
    "react-dom" -> "17.0.2"
  ),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.4.0",
    "com.github.japgolly.scalajs-react"  %%% "core" % "2.1.1"
  )
).dependsOn(core).enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

lazy val webServer = project.settings(
  name := "Webserver for the examples",
  baseDirectory := file("examples/backend"),
  sourceDirectory := baseDirectory.value / "src",
  skip / publish := true,
  libraryDependencies ++= Seq(
    "dev.zio" %% "zio-http" % "0.0.5",
    "com.lihaoyi" %% "os-lib" % "0.9.1"
  )
)
