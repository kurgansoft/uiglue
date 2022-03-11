ThisBuild / scalaVersion     := "2.13.8"

val zioVersion = "1.0.13"

lazy val core = project.settings(
    name := "uiglue",
    baseDirectory := file("core"),
    sourceDirectory := baseDirectory.value / "src",
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % zioVersion,
      "org.scala-js" %%% "scala-js-macrotask-executor"  % "1.0.0",
      "io.github.cquiroz" %%% "scala-java-time"      % "2.3.0"
    )
  ).enablePlugins(ScalaJSPlugin)

lazy val examples = project.settings(
  name := "Examples",
  baseDirectory := file("examples/ui"),
  sourceDirectory := baseDirectory.value / "src",
  skip in publish := true,
  Compile / scalaJSUseMainModuleInitializer := true,
//  Compile / npmDependencies ++= Seq(
//    "react" -> "17.0.2",
//    "react-dom" -> "17.0.2"
//  ),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.1.0",
    "com.github.japgolly.scalajs-react"  %%% "core" % "2.0.1"
  )
).dependsOn(core).enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

val ZHTTPVersion = "1.0.0.0-RC25"

lazy val webServer = project.settings(
  name := "Webserver for the examples",
  baseDirectory := file("examples/backend"),
  sourceDirectory := baseDirectory.value / "src",
  skip in publish := true,
  libraryDependencies ++= Seq(
    "io.d11" %% "zhttp" % ZHTTPVersion,
    "com.lihaoyi" %% "os-lib" % "0.8.0"
  )
)
