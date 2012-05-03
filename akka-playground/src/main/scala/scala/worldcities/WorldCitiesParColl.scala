package scala.worldcities

object WorldCitiesParColl extends App {
  
  val lat = 50.88
  val lng = 4.7
  println("Reading world cities")
  val cities = io.Source.fromFile("worldcitiespop.txt", "ISO-8859-1").getLines.toList
  println("Number of cities: " + cities.size)
  
  val startTime = System.currentTimeMillis
  val nearestCities = 
    cities.par
      .map(City(_))
      .filter(city => city.isNear(lat, lng) && city.population.isDefined)
//  val nearestCities = 
//    cities.par
//      .flatMap(line => { val city = City(line); if(city.isNear(lat, lng) && city.population.isDefined) Some(city) else None})


  val end = System.currentTimeMillis
 
  nearestCities.toList.sortBy(_.population).map(println)
  println("Number of cities: " + nearestCities.size)
  println("Elapsed (ParColl): " + (end-startTime))
}
