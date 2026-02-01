import play.api.libs.json._
import scala.util.{Try, Success, Failure, Properties}

object WikipediaFrequency {
  // Difficulty tiers based on Wikipedia totalhits:
  // 1 = Easy (very common words): >= 50,000 hits
  // 2 = Medium-Easy: 10,000 - 49,999 hits
  // 3 = Medium: 2,000 - 9,999 hits
  // 4 = Medium-Hard: 500 - 1,999 hits
  // 5 = Hard (rare words): < 500 hits

  private val TIER_1_MIN = 50000   // Easy
  private val TIER_2_MIN = 10000   // Medium-Easy
  private val TIER_3_MIN = 2000    // Medium
  private val TIER_4_MIN = 500     // Medium-Hard
  // Below 500 = Hard (tier 5)

  // Optional Wikimedia API token for authenticated requests (higher rate limits)
  // If not set, falls back to unauthenticated requests (for local dev/testing)
  private val wikimediaToken: Option[String] =
    Properties.envOrNone("WIKIMEDIA_TOKEN").filter(_.nonEmpty)

  def getTotalHits(word: String, lang: String): Either[String, Int] = {
    val url = s"https://$lang.wikipedia.org/w/api.php?action=query&list=search&srsearch=${java.net.URLEncoder.encode(word, "UTF-8")}&format=json&srlimit=1"

    // Build headers - add Authorization if token is available
    val baseHeaders = Map("User-Agent" -> "RandomWordAPI/1.0 (https://github.com/RazorSh4rk/random-word-api)")
    val headers = wikimediaToken match {
      case Some(token) => baseHeaders + ("Authorization" -> s"Bearer $token")
      case None => baseHeaders
    }

    Try {
      val response = requests.get(url, headers = headers)
      val json = Json.parse(response.text())

      // Check for rate limit error
      if ((json \ "error").isDefined) {
        Left("rate_limited")
      } else {
        val totalHits = (json \ "query" \ "searchinfo" \ "totalhits").asOpt[Int].getOrElse(0)
        Right(totalHits)
      }
    } match {
      case Success(result) => result
      case Failure(_) => Left("request_failed")
    }
  }

  def getDifficultyTier(totalHits: Int): Int = {
    if (totalHits >= TIER_1_MIN) 1
    else if (totalHits >= TIER_2_MIN) 2
    else if (totalHits >= TIER_3_MIN) 3
    else if (totalHits >= TIER_4_MIN) 4
    else 5
  }

  def wordMatchesDifficulty(word: String, lang: String, targetDifficulty: Int): Either[String, Boolean] = {
    getTotalHits(word, lang) match {
      case Right(hits) => Right(getDifficultyTier(hits) == targetDifficulty)
      case Left(error) => Left(error)
    }
  }

  // Filter words by difficulty, making API calls one by one
  // Returns up to `count` words matching the target difficulty
  // If rate limited, returns None to signal fallback to unfiltered results
  def filterWordsByDifficulty(
    words: List[String],
    lang: String,
    targetDifficulty: Int,
    count: Int
  ): Option[List[String]] = {
    var result = List.empty[String]
    val iterator = words.iterator

    while (result.length < count && iterator.hasNext) {
      val word = iterator.next()
      wordMatchesDifficulty(word, lang, targetDifficulty) match {
        case Right(true) => result = result :+ word
        case Right(false) => // Skip this word
        case Left("rate_limited") => return None // Signal to use fallback
        case Left(_) => // Other errors, skip this word
      }
    }

    Some(result)
  }
}
