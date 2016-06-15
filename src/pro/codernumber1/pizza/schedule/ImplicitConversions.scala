package pro.codernumber1.pizza.schedule

object ImplicitConversions {
  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Long = s.view.map(_.waitTime.toLong).sum / s.length
    def finish: Int = s.lastOption.map(_.finishTime).getOrElse(0)
  }
}
