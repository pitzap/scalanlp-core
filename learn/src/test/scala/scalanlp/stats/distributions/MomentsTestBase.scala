package scalanlp.stats
package distributions

import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.scalatest.prop._
import org.scalacheck._
import java.util.zip.DeflaterInputStream

/**
 * @author dlwh
 */
trait MomentsTestBase[T] extends FunSuite with Checkers {
  implicit def arbDistr: Arbitrary[Measure[T] with Rand[T] with Moments[Double]];
  val numSamples = 10000;
  def asDouble(x: T):Double
  def fromDouble(x: Double):T

  test("mean") {
    check(Prop.forAll { (distr: Measure[T] with Rand[T] with Moments[Double])=>
       val sample = distr.sample(numSamples).map(asDouble _)
       val mean = DescriptiveStats.mean(sample)
       (mean - distr.mean).abs/(mean.abs max 1) < 1E-1
    })
  }

  test("variance") {
    check(Prop.forAll { (distr: Measure[T] with Rand[T] with Moments[Double])=>
       val sample = distr.sample(numSamples).map(asDouble _)
       val variance = DescriptiveStats.variance(sample)
       (variance - distr.variance).abs/(variance max 1) < 1E-1
    })
  }


  test("mode") {
    check(Prop.forAll { (distr: Rand[T] with Measure[T] with Moments[Double])=>
      val sample = distr.sample(40)
      val probMode = distr(fromDouble(distr.mode))
//      if(distr.isInstanceOf[Poisson])
//        println(distr,probMode,sample.map{ distr },sample)
      sample.forall(x => probMode >= distr(x) - 1E-4);
    })
  }

}