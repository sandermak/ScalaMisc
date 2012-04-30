package scala.worldcities

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinRouter
import akka.actor.ActorRef
import akka.util.duration._ 

object WorldCities extends App {
  val lat = 50.88
  val lng = 4.7
  
  println("Reading world cities")
  val cities = io.Source.fromFile("worldcitiespop.txt", "ISO-8859-1").getLines
  val segmentSize = 15000
  val segmentedCities = cities.grouped(segmentSize).toList
  
  println("Number of cities: " + segmentedCities.map(_.size).sum)
  
  val actorSystem = ActorSystem("WorldCities")
  val startTime = System.currentTimeMillis
  val resultListener = actorSystem.actorOf(Props(new ResultListener(segmentedCities.size, startTime)), name = "results")
  val workers = Vector.fill(4) {
    actorSystem.actorOf(Props(new NearestCityActor(resultListener, lat, lng)))
  }
  val nearestCityActor = actorSystem.actorOf(
      Props[NearestCityActor].withRouter(RoundRobinRouter(routees = workers)))
  
  for(citiesSegment <- segmentedCities)
    nearestCityActor ! CitiesInput(citiesSegment)
 
}

case class CitiesInput(cities: Seq[String])
case class NearestCities(cities: Seq[City])

class NearestCityActor(resultListener: ActorRef, lat: Double, lng: Double)  extends Actor {
  
  
  def receive = {
    case CitiesInput(cities) => resultListener ! findTopNearestCities(cities)
    case _ => println("Bad input!")
  }

  def findTopNearestCities(cities: Seq[String]): NearestCities = {
      val nearest = cities
	      .map(City(_))
	      .filter(city => city.isNear(lat, lng) && city.population.isDefined)

	  NearestCities(nearest)
  }
  
}

case class City(name: String, population:Option[Int], lat: Double, lng: Double) {
  val searchBoundSquared = math.pow(0.4, 2)
  
  // (lat-targetLat)^2 + (lng-targetLng)^2 < 0.4^2
  def isNear(targetLat: Double, targetLng: Double) = {
     math.pow(lat-targetLat, 2) + math.pow(lng-targetLng, 2) < searchBoundSquared
  }
}

object City {
  def apply(input: String): City = {
    val fields = input.split(",")
    val name = fields(2) + " (" + fields(0) +")"
    val lat = fields(5).toDouble
    val lng = fields(6).toDouble
    val population = if(!"".equals(fields(4))) Some(fields(4).toInt) else None
    City(name, population, lat, lng)
  }
}

class ResultListener(nrOfResults: Int, startTime: Long) extends Actor {
  var toReceive = nrOfResults
  var cities: Seq[City] = scala.collection.mutable.ArrayBuffer()
  
  def receive = {
    case NearestCities(nearestCities) => cities ++= nearestCities; toReceive -= 1; if(toReceive == 0) printresults();
  }
  
  def printresults() {
    val end = System.currentTimeMillis
    cities.sortBy(_.population).map(println)
    println("Number of cities: " + cities.size)
    println("Elapsed (Actors): " + (end-startTime))
    context.system.shutdown()
  }
}
