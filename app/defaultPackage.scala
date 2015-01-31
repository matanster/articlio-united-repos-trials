//
// Takes care of starting up non-Play code, when Play bootstraps.
//

import play.api._
import com.articlio.HttpService
import com.articlio.AppActorSystem

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import akka.pattern.ask
//import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.Await

object Global extends GlobalSettings {

  Logger.info("Global object started")
  println("Global object started")
  
  println("Starting non-Play stuff...")
  Logger.info("Starting non-Play stuff...")
  //val a = Boot
  val h = HttpService
  val s = AppActorSystem.system.actorOf(Props[h.ShutDowner], 
      name = "s")
  
  s ! "Start"
  
  override def onStart(app: Application) {
    implicit val timeout = Timeout(5.seconds)
    val future = s ? "Stop"
    Await.result(future, 5.seconds)
  }  

  override def onStop(app: Application) {

    implicit val timeout = Timeout(5.seconds)
    Logger.info("Stopping non-Play stuff...")
    val future = s ? "Stop"
    Await.result(future, 5.seconds)
  }  
}

