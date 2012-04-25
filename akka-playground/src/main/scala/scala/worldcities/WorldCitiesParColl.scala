package scala.worldcities

object WorldCitiesParColl extends App {
  
  val lat = 50.88
  val lng = 4.7
  println("Reading world cities")
  val cities = io.Source.fromFile("worldcitiespop.txt", "ISO-8859-1").getLines.toList
  
  val startTime = System.currentTimeMillis
  val nearestCities = 
    cities.par
      .map(City(_))
      .filter(city => city.isNear(lat, lng) && city.population.isDefined)
  val end = System.currentTimeMillis
 
  nearestCities.toList.sortBy(_.population).map(println)
  println("Number of cities: " + nearestCities.size)
  println("Elapsed (ParColl): " + (end-startTime))
}
