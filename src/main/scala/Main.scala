
import play.api.libs.json._
import scala.io.Source
import scala.util.Random
import scala.util.Properties

object Main extends cask.MainRoutes {
  override def port: Int = 
    Properties.envOrElse("PORT", "9001").toInt

  override def host: String = 
    Properties.envOrElse("HOST", "0.0.0.0")
  
  println("Starting on " + port)

  val words = Json.parse(Source.fromFile("words.json").getLines().mkString)
  val swearWords = Json.parse(Source.fromFile("swear.json").getLines().mkString)
  val headers = Seq(
    ("content-type", "application/json"),
    ("Access-Control-Allow-Origin", "*")
  )

  @cask.get("/")
  def redir() = {
    cask.Redirect("/home")
  }


  @cask.staticFiles("/home")
  def index() = {
    "/templates/index.html"
  }

  @cask.get("/all")
  def getAll(swear: Int = 0) = {
    if(swear == 0) 
      cask.Response(Json.stringify(words), headers = headers)
    else 
      cask.Response(Json.stringify(Json.toJson(words.as[List[String]])), headers = headers)
  }

  @cask.get("/word")
  def getWord(number: Int = 1, swear: Int = 0) = {
    val n = if(number < 1001) number else 1000
    val r = new Random
    var w = words.as[List[String]]
    if(swear == 1)
      w = w:::swearWords.as[List[String]]
    val ret = List
      .tabulate(n)( el=> w(r.between(0, w.length - 1)) )
    
    cask.Response(Json.stringify(Json.toJson(ret)), headers = headers)
  }

  initialize()
}

