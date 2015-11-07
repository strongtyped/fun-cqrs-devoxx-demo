package io.strongtyped.funcqrs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ReadModelSupportTest {
  implicit class ProjectionOps(projection: Projection) {
    def applyEvents(events: Seq[DomainEvent]): Future[Unit] = {
      events.foldLeft(Future.successful(())) { (fut, evt) =>
        fut.flatMap { _ => projection.onEvent(evt) }
      }
    }
  }
}
