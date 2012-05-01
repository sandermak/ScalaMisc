package scalagreeter

import akka.actor._
import akka.dispatch.Future
import akka.util.Timeout
import akka.util.duration._
import akka.dispatch.Await

object Greeter extends App {
  
  case class Greeting(who: String)
  case class FutureGreeting(who: String)
 
  class GreetingActor extends Actor with ActorLogging {
    def receive = {
      case Greeting(who)  => log.info("Hello " + who)
      case FutureGreeting(who) => sender ! "Hello future " + who
    }
  }
 
  // Create an actor system with an actor
  val system = ActorSystem("MySystem")
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
 
  // Send message
  greeter ! Greeting("Sander")
  
  // The ask pattern models a reply by an actor with a Future
  import akka.pattern.ask
  
  val timeout = Timeout(2 seconds)
  val greeting = ask(greeter, FutureGreeting("Sander"))(timeout);
  println(Await.result(greeting, timeout.duration))
  
  system.shutdown
}