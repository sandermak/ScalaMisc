package javagreeter;

import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Greeter {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("MySystem");
		ActorRef greeter = system.actorOf(new Props(GreetingActor.class),
				"greeter");
		greeter.tell(new Greeting("Sander"));
		system.shutdown();
	}
}

class Greeting implements Serializable {
	public final String who;

	public Greeting(String who) {
		this.who = who;
	}
}
