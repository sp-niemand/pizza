package pro.codernumber1.pizza.schedule

object ImplicitConversions {
  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Long = if (s.nonEmpty) s.view.map(_.waitTime.toLong).sum / s.length else 0
    def finish: Int = s.lastOption.map(_.finishTime).getOrElse(0)
  }
}
