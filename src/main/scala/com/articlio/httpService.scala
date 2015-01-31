package com.articlio
import com.articlio.input.JATS
import com.articlio.util.runID

//
// Spray imports
//
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent.duration._

class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    get {
	  path("") { 
		complete {
          <html>
            <body>
              <h1>service is up</h1>
            </body>
          </html>
        }
      } ~
      path("semantic") {
        parameter('all) { all =>
          val bulk = new Bulk((new runID).id)
          bulk.all
          complete("Done processing all files... but you probably timed out by now")
        } ~
        parameter('allp) { all =>
          val bulk = new Bulk((new runID).id)
          bulk.allPDF
          complete("Done processing all pdf files... but you probably timed out by now")
        } ~
        parameter('alle) { all =>
          val bulk = new Bulk((new runID).id)
          bulk.alleLife
          complete("Done processing all eLife files... but you probably timed out by now")
        } ~       
        parameter('export) { all =>
          com.articlio.storage.createAnalyticSummary.go()
          com.articlio.storage.createCSV.go()
          complete("Done producing result CSVs")
        } ~    
          parameter('purgeAll) { purgeAll => 
          AppActorSystem.outDB ! "dropCreate"
          complete("purging all semantic data...")
        }
      }
    }
}

//import com.articlio.AppActorSystem
object HttpService {

  // ActorSystem for spray
  implicit val sprayActorSystem = ActorSystem("spray-actor-system")

  // create and start our service actor
  val service = sprayActorSystem.actorOf(Props[MyServiceActor], "http-service")

  implicit val timeout = Timeout(6000.seconds)
  
  val serviceManager = IO(Http) 
 
  class ShutDowner extends Actor {
    def receive = {
      case "Start" => println("got start messageeeeeeeeeeeeeeeeee"); serviceManager ! Http.Bind(service, interface = "localhost", port = 3001)
      case "Stop" => println("got stop messageeeeeeeeeeeeeeeeee"); serviceManager ! Http.Unbind(1.seconds)
      case Http.Unbound => println("Spray unboundddddddddddddddddddd"); sender() ! "done"
      case other => println("received unknown message: " + other)
    }
  }
}

