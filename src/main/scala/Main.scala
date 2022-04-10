import play.api.libs.json._
import scala.io.Source
import scala.util.Random
import scala.util.Properties
import java.net.URI
import com.redis._

object Main extends cask.MainRoutes {
	println(Utils.getLanguages())
  override val port: Int = 
    Properties.envOrElse("PORT", "9001").toInt

  override val host: String = 
    Properties.envOrElse("HOST", "0.0.0.0")

  val redisUrl = 
    URI.create(Properties.envOrElse("REDIS_URL", "http://localhost:6379"))

  println(s"running on $host:$port with redis url $redisUrl")
 
  val words = Json.parse(Source.fromFile("words.json").getLines().mkString)
  val swearWords = Json.parse(Source.fromFile("swear.json").getLines().mkString)
  val headers = Seq(
    ("content-type", "application/json"),
    ("Access-Control-Allow-Origin", "*")
  )

  // put the ip into redis with a 5 sec expiration to prevent DOS
  // attacks like we had in the past
  val checkRateLimit = (IP: String) => {
	try {
		val redisConnection = new RedisClient(redisUrl)
		redisConnection.get(IP) match {
			case None => {
					redisConnection.set(IP, "limited")
					redisConnection.expire(IP, 5)
					true
				}
			case Some(x) => false
		}
	} catch {
		// don't make the app hang is redis is unavailable
		case e: RuntimeException => true
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

  @cask.get("/languages")
  def languages() = {
	  cask.Response(
		  Json.stringify(Json.toJson(Utils.getLanguages)),
		  statusCode = 200,
		  headers = headers
	  )
  }

  @cask.get("/all")
  def getAll(req: cask.Request, lang: String = "en", swear: Int = 0) = {
  	val IP = req.exchange.getSourceAddress.toString
  	if(checkRateLimit(IP)) {
		// have this separately, english is the most common request and we dont want
		// to open a file every time for it
		if(lang != "en") {
			val fWords = Utils.getLanguageFileContent(lang.toLowerCase)
			if(fWords != null) {
				cask.Response(Json.stringify(fWords), headers = headers)
			} else {
				cask.Response(
					Json.stringify(Json.toJson(Map("Error" -> "No translation for this language"))),
					statusCode = 403,
					headers = headers
				)
			}
		} else {
			if(swear == 0) 
				cask.Response(Json.stringify(words), headers = headers)
			else 
				cask.Response(Json.stringify( Json.toJson( words.as[List[String]]:::(swearWords.as[List[String]]) ) ) , headers = headers)
		}
    } else cask.Response(
       		Json.stringify(Json.toJson(Map("Error" -> "You hit the rate limit, try again in a few seconds"))),
        	statusCode = 403,
       		headers = headers
    	)
  }

  @cask.get("/word")
  def getWord(req: cask.Request, number: Int = 1, lang: String = "en", swear: Int = 0, length: Int = -1) = {
  	val IP = req.exchange.getSourceAddress.toString
  	if(checkRateLimit(IP)){
		if(lang != "en") {
			val fWords = Utils.getLanguageFileContent(lang.toLowerCase)
			if(fWords != null) {
				val r = new Random
				var w = fWords.as[List[String]]
				
				val ret = { 
					if(length == -1) Random.shuffle(w).take(number)
					else Random.shuffle(w.filter(_.length == length)).take(number) 
				}
			
				cask.Response(Json.stringify(Json.toJson(ret)), headers = headers)
			} else {
				cask.Response(
					Json.stringify(Json.toJson(Map("Error" -> "No translation for this language"))),
					statusCode = 403,
					headers = headers
				)
			}
		} else {
			val r = new Random
			var w = words.as[List[String]]
			if(swear == 1)
			w = w:::swearWords.as[List[String]]
			val ret = { 
				if(length == -1) Random.shuffle(w).take(number)
				else Random.shuffle(w.filter(_.length == length)).take(number) 
			}
		
			cask.Response(Json.stringify(Json.toJson(ret)), headers = headers)
		}
    } else cask.Response(
    		Json.stringify(Json.toJson(Map("Error" -> "You hit the rate limit, try again in a few seconds"))),
    		statusCode = 403,
    		headers = headers
    	)
  }

  // endpoint to check redis entries because i cant do that in heroku
  @cask.get("/redis_dump")
  def redisDump(pass: String) = {
  	val _pass = scala.util.Properties.envOrElse("PASS", "")
  	if(pass != "" && pass == _pass) {
  		val redisConnection = new RedisClient(redisUrl)
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
