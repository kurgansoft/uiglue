package base

import zio.{App, ExitCode, URIO}
import zhttp.http._

import java.io.File
import java.nio.file.Paths

object WebServer extends App {

  val examplesUIDirectory = os.pwd / "examples" / "ui"

  val resourcesFolder = examplesUIDirectory / "src" / "main" / "resources"

  val generatedJSDirectory = examplesUIDirectory / "target" / "scala-2.13" / "scalajs-bundler" / "main"

  val notFound: HttpApp[Any, Nothing] = Http.error("").setStatus(Status.NOT_FOUND)

  val mix: HttpApp[Any, Nothing] = Http.collectHttp[Request] {
    case Method.GET -> !! / "favicon.ico" => Http.ok
    case Method.GET -> !! / "js" / rest =>
      Http.fromFile(new File(generatedJSDirectory + "/" + rest)).orElse(notFound)
    case Method.GET -> path =>
      val pathSegments = path.toList
      val subPath = if (pathSegments.nonEmpty) pathSegments.reduce(_ + "/" + _) else "index.html"
      val filePath = resourcesFolder.toString() + "/" + subPath
      Http.fromFile(new File(Paths.get(filePath).toString)).orElse(notFound)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    zhttp.service.Server.start(8080, mix).exitCode
}
