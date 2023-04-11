package uiglue

import zio.internal.stacktracer.Tracer
import zio.{Queue, Ref, Unsafe, UIO, ZIO}

import scala.concurrent.Future

object EventLoop {

  type EventHandler[E <: Event] = E => Unit

  implicit val tracer: Tracer = Tracer.instance

  implicit val unsafe: Unsafe = Unsafe.unsafe(x => x)

  private def createEventHandler[E <: Event](queue: Queue[E]): EventHandler[E] = event => {
    Future {
      zio.Runtime.default.unsafe.run(queue.offer(event).as())
    }(org.scalajs.macrotaskexecutor.MacrotaskExecutor)
  }

  def createLoop[E <: Event](
                            initalState: UIState[E],
                            renderFunction: (UIState[E], EventHandler[E]) => Unit,
                            bootStrapEvents: List[E] = List.empty
                            ): UIO[Unit] = {
    for {
      state <- Ref.make(initalState)
      queue <- Queue.unbounded[E]
      eventHandler = createEventHandler(queue)
      _ = renderFunction(initalState, eventHandler)
      _ <- queue.offerAll(bootStrapEvents)
      _ <- queue.take.flatMap(event =>
        for {
          currentState <- state.get
          (newState, effect) = currentState.processEvent(event)
          _ <- state.set(newState)
          _ <- ZIO.succeed(renderFunction(newState, eventHandler)).fork
          _ <- effect(eventHandler).flatMap(queue.offerAll).fork
        } yield ()
      ).forever
    } yield ()
  }
}
