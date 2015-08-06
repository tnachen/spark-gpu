import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object ParallelizeTest {
  def main(args: Array[String]) {
    val conf = new SparkConf
    conf.setAppName("ParallelizeTest")
    val sc = new SparkContext(conf)

    val cnt = sc.parallelize(1 to 100, 10).count()
    println(s"Count: $cnt")
    sc.stop()
  }
}
