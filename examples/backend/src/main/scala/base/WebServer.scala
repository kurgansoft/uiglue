package base

import zio._
import zio.http._
import zio.http.model.{Method, Status}

import java.io.File
import java.nio.file.Paths

object WebServer extends ZIOAppDefault {

  val examplesUIDirectory = os.pwd / "examples" / "ui"

  val resourcesFolder = examplesUIDirectory / "src" / "main" / "resources"

  val generatedJSDirectory = examplesUIDirectory / "target" / "scala-2.13" / "scalajs-bundler" / "main"

  val notFound = Response.status(Status.NotFound)

  val mix: App[Any] = Http.collectRoute[Request] {
    case Method.GET -> !! / "favicon.ico" => Handler.ok.toHttp
    case Method.GET -> !! / "js" / rest =>
      Http.fromFile(new File(generatedJSDirectory + "/" + rest)).mapError(_ => notFound)
    case Method.GET -> path =>
      val subPath = path.segments.size match {
        case 1 => "/index.html"
        case _ => path.toString()
      }
      val filePath = resourcesFolder.toString() + subPath
      Http.fromFile(new File(Paths.get(filePath).toString)).mapError(_ => notFound)
  }

  override val run = Server.serve(mix).provide(Server.default)

}
