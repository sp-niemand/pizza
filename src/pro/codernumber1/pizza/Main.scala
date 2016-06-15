package pro.codernumber1.pizza

import pro.codernumber1.pizza.io.Input
import pro.codernumber1.pizza.schedule.PSWScheduler

import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]): Unit = {
    import pro.codernumber1.pizza.schedule.ImplicitConversions.Schedule
    (new Input).jobs() match {
      case Success(jobs) => println(PSWScheduler.schedule(jobs).averageFinishTime)
      case Failure(t) => println(s"Input failure: $t")
    }
  }
}