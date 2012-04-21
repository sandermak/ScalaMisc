package scala.worldcities

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinRouter

object WorldCities extends App {

  lazy val cities = io.Source.fromFile("worldcitiespop.txt", "ISO-8859-1").getLines.toSeq
  val segmentSize = 15000
  
  println("Number of cities: " + cities.size)
  
  val actorSystem = ActorSystem("WorldCities")
  val nearestCityActor = actorSystem.actorOf(
      Props[NearestCityActor].withRouter(RoundRobinRouter(4)))
  
  for(citiesSegment <- cities.grouped(segmentSize))
    nearestCityActor ! CitiesInput(citiesSegment)
    
  actorSystem.shutdown()
}

case class CitiesInput(cities: Seq[String])
case class NearestCities(cities: Seq[String])

class NearestCityActor extends Actor {
  
  def receive = {
    case CitiesInput(cities) => println("Segment: " + cities.size)
    case _ => println("Bad input!")
  }
  
}