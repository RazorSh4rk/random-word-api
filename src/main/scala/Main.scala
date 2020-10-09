import play.api.libs.json._
import scala.io.Source
import scala.util.Random
import scala.util.Properties
import java.net.URI
import com.redis._

object Main extends cask.MainRoutes {
  override val port: Int = 
    Properties.envOrElse("PORT", "9001").toInt

  override val host: String = 
    Properties.envOrElse("HOST", "0.0.0.0")

  val redisUrl = 
    URI.create(Properties.envOrElse("REDIS_URL", "http://localhost:6379"))
 
  val words = Json.parse(Source.fromFile("words.json").getLines().mkString)
  val swearWords = Json.parse(Source.fromFile("swear.json").getLines().mkString)
  val headers = Seq(
    ("content-type", "application/json"),
    ("Access-Control-Allow-Origin", "*")
  )

  val checkRateLimit = (IP: String) => {
    val redisConnection = new RedisClient(redisUrl)
  	redisConnection.get(IP) match {
  		case None => {
  				redisConnection.set(IP, "limited")
  				redisConnection.expire(IP, 5)
  				true
  			}
  		case Some(x) => false
  	}
  }

  @cask.get("/")
  def redir() = {
    cask.Redirect("/home")
  }

  @cask.staticFiles("/home")
  def index() = {
    "/templates/index.html"
  }

  @cask.get("/all")
  def getAll(req: cask.Request, swear: Int = 0) = {
  	val IP = req.exchange.getSourceAddress.toString
  	if(checkRateLimit(IP)){
	    if(swear == 0) 
	      cask.Response(Json.stringify(words), headers = headers)
	    else 
	      cask.Response(Json.stringify( Json.toJson( words.as[List[String]]:::(swearWords.as[List[String]]) ) ) , headers = headers)
    } else cask.Response(
       		Json.stringify(Json.toJson(Map("Error" -> "You hit the rate limit, try again in a few seconds"))),
        	statusCode = 403,
       		headers = headers
    	)
  }

  @cask.get("/word")
  def getWord(req: cask.Request, number: Int = 1, swear: Int = 0) = {
  	val IP = req.exchange.getSourceAddress.toString
  	if(checkRateLimit(IP) && number != 500 && swear != 0){
	    val r = new Random
	    var w = words.as[List[String]]
	    if(swear == 1)
	      w = w:::swearWords.as[List[String]]
	    val ret = Random.shuffle(w).take(number)
	
	    cask.Response(Json.stringify(Json.toJson(ret)), headers = headers)
    } else cask.Response(
    		Json.stringify(Json.toJson(Map("Error" -> "You hit the rate limit, try again in a few seconds"))),
    		statusCode = 403,
    		headers = headers
    	)
  }

  // endpoint to check redis entries because i cant do that in heroku
  @cask.get("/redis_dump")
  def redisDump(pass: String) = {
  	val _pass = scala.util.Properties.envOrElse(pass, "")
  	if(pass != "") {
  		val keys: List[String] = redisConnection.keys("*") match {
  			case None => List()
  			case Some(x) => {
  				x.map(el => el match {
  					case None => ""
  					case Some(x) => x.toString
  				})
  			}
  		}
  		keys.mkString("\n")
  	} else ""
  }

  initialize()
}
