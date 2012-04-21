package scalagreeter

import akka.actor._

object Greeter extends App {
  case class Greeting(who: String)
 
  class GreetingActor extends Actor with ActorLogging {
    def receive = {
      case Greeting(who) ⇒ log.info("Hello " + who)
    }
  }
 
  val system = ActorSystem("MySystem")
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
  greeter ! Greeting("Sander")
  system.shutdown
}