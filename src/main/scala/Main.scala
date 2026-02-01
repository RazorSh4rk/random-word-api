import play.api.libs.json._
import scala.io.Source
import scala.util.Properties

object Main extends cask.MainRoutes {
  override val port: Int = Properties.envOrElse("PORT", "9001").toInt
  override val host: String = Properties.envOrElse("HOST", "0.0.0.0")

  println(s"Available languages: ${LanguageService.getAvailableLanguages()}")
  println(s"Running on $host:$port")

  private val englishWords: JsValue =
    Json.parse(Source.fromFile("words.json").getLines().mkString)

  private val jsonHeaders = Seq(
    ("content-type", "application/json"),
    ("Access-Control-Allow-Origin", "*")
  )

  private def jsonResponse(data: JsValue, status: Int = 200) =
    cask.Response(Json.stringify(data), statusCode = status, headers = jsonHeaders)

  private def errorResponse(message: String, status: Int = 403) =
    jsonResponse(Json.toJson(Map("Error" -> message)), status)

  private val rateLimitError = errorResponse("You hit the rate limit, try again in a few seconds")
  private val languageError = errorResponse("No translation for this language")

  @cask.get("/")
  def root() = cask.Redirect("/home")

  @cask.staticFiles("/home")
  def home() = "/templates/index.html"

  @cask.staticFiles("/js", headers = Seq("Content-Type" -> "text/javascript"))
  def js() = "/templates/script.js"

  @cask.staticFiles("/css", headers = Seq("Content-Type" -> "text/css"))
  def css() = "/templates/style.css"

  @cask.get("/languages")
  def languages() = jsonResponse(Json.toJson(LanguageService.getAvailableLanguages()))

  @cask.get("/all")
  def getAll(req: cask.Request, lang: String = "en") = {
    val ip = req.exchange.getSourceAddress.toString
    if (!RateLimiter.check(ip)) {
      rateLimitError
    } else {
      LanguageService.getAllWordsJson(lang, englishWords) match {
        case Right(words) => jsonResponse(words)
        case Left(_) => languageError
      }
    }
  }

  @cask.get("/word")
  def getWord(
    req: cask.Request,
    number: Int = 1,
    lang: String = "en",
    length: Int = -1,
    diff: Int = -1
  ) = {
    val ip = req.exchange.getSourceAddress.toString
    if (!RateLimiter.check(ip)) {
      rateLimitError
    } else {
      LanguageService.getWordsForLanguage(lang, englishWords) match {
        case Right(words) =>
          val result = WordService.getRandomWords(words, number, length, lang, diff)
          jsonResponse(Json.toJson(result))
        case Left(_) => languageError
      }
    }
  }

  @cask.get("/redis_dump")
  def redisDump(pass: String) = {
    RateLimiter.getKeys(pass) match {
      case Some(keys) => keys.mkString("\n")
      case None => ""
    }
  }

  initialize()
}
