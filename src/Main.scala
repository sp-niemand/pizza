import scala.collection.mutable
import scala.util.Random
import scala.util.control.NonFatal

object Main {
  private def randomJobs = {
    val Nb = (20, 30)
    val N = Random.nextInt(Nb._2 - Nb._1) + Nb._1
    val L = mutable.ListBuffer.empty[Job]
    val size = 100
    for (i <- 1 to N) {
      L += new Job(Random.nextInt(size), Random.nextInt(size - 1) + 1)
    }
    L
  }

  def main(args: Array[String]): Unit = {


    val s = new Scheduler
    1 to 1 foreach { _ =>
      val L = randomJobs
      println(L)
      try {
        val r = s.schedule(L)
        assert(r.size == L.size)
        println(r)
      } catch {
        case NonFatal(t) =>
          println(s"lol fail: $t")
          println(L)
      }
    }
  }
}