package lottery

import io.strongtyped.funcqrs._
import lottery.domain.model.LotteryProtocol.{ AddParticipant, CreateLottery, Run }
import lottery.domain.model.{ Lottery, LotteryId }
import lottery.domain.service.{ LotteryViewProjection, LotteryViewRepo }
import org.scalatest.concurrent.{ Futures, ScalaFutures }
import org.scalatest.time.{ Seconds, Span }
import org.scalatest.{ FunSuite, Matchers, OptionValues }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class LotteryTest extends FunSuite with Matchers with Futures
    with ScalaFutures with FailedFutures with OptionValues {

  override implicit val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val id = LotteryId("test-lottery")
  val lotteryBehavior = Lottery.behavior(LotteryId("test-lottery"))

  test("Run a Lottery") {

    new WriteModelSupportTest with ReadModelSupportTest {

      // Write Model
      val (events, lottery) =
        lotteryBehavior.applyCommands(
          CreateLottery("TestLottery"),
          AddParticipant("John"),
          AddParticipant("Paul"),
          Run
        ).futureValue

      lottery.hasWinner shouldBe true
      lottery.participants should have size 2

      // Read Model
      val repo = new LotteryViewRepo
      val projection = new LotteryViewProjection(repo)

      val view =
        projection.applyEvents(events).flatMap { _ =>
          repo.find(id)
        }.futureValue

      view.participants should have size 2
      view.winner shouldBe defined
    }

  }

  test("Run a Lottery twice") {

    new WriteModelSupportTest {

      val lotteryFut = lotteryBehavior.applyCommands(
        CreateLottery("TestLottery"),
        AddParticipant("John"),
        AddParticipant("Paul"),
        Run,
        Run
      )

      whenFailed(lotteryFut) {
        case e => e.getMessage shouldBe "Lottery has already a winner"
      }
    }
  }

  test("Run a Lottery without participants") {

    new WriteModelSupportTest {
      val lotteryFut = lotteryBehavior.applyCommands(
        CreateLottery("TestLottery"),
        Run
      )

      whenFailed(lotteryFut) {
        case e => e.getMessage shouldBe "Lottery has no participants"
      }
    }
  }

  test("Add twice the same participant") {

    new WriteModelSupportTest {
      val lotteryFut = lotteryBehavior.applyCommands(
        CreateLottery("TestLottery"),
        AddParticipant("John"),
        AddParticipant("John")
      )

      whenFailed(lotteryFut) {
        case e => e.getMessage shouldBe "Participant John already added!"
      }
    }
  }

}
