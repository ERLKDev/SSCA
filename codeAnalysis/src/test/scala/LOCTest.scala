import TestSpecs.MetricSpec
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics._

/**
  * Created by erikl on 5/4/2017.
  */
class LOCTest extends MetricSpec {
  var analyser: Analyser = _
  var metrics: List[Metric] = _

  before {
    metrics = List(new Loc)
    analyser = new Analyser(metrics, "C:\\tmp\\gitAkkaAkka1", 1)
  }

/*  test("new pizza has zero toppings") {
    assert(pizza.getToppings.size == 0)
  }

  test("adding one topping") {
    pizza.addTopping(Topping("green olives"))
    assert(pizza.getToppings.size === 1)
  }*/

  // mark that you want a test here in the future
  test ("test pizza pricing") (pending)
}
