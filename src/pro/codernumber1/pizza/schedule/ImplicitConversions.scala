package pro.codernumber1.pizza.schedule

object ImplicitConversions {
  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Double = s.map(_.waitTime).sum / s.length
    def finish: Int = s.lastOption.map(_.finishTime).getOrElse(0)
  }
}
