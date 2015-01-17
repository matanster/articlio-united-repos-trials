import play.api._

object Global extends GlobalSettings {

  Logger.info("Application has started")
  println("Application has started")
  
  override def onStart(app: Application) {
    println("Application has started")
    Logger.info("Application has started")
    val a = Boot
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
}