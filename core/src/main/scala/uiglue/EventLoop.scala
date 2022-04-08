package uiglue

import zio.{Queue, Ref, Task, UIO}

import scala.concurrent.Future

object EventLoop {

  type EventHandler[E <: Event] = E => Unit

  private def createEventHandler[E <: Event](queue: Queue[E]): EventHandler[E] = event => {
    Future {
      zio.Runtime.default.unsafeRun(queue.offer(event).as())
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
          _ <- Task{renderFunction(newState, eventHandler)}.orDie.fork
          _ <- effect(eventHandler).flatMap(queue.offerAll).fork
        } yield ()
      ).forever
    } yield ()
  }
}
