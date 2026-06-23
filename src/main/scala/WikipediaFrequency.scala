import play.api.libs.json._
import scala.util.{Try, Success, Failure, Properties}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

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

  // --- Performance improvements: caching, timeouts, parallelism ---

  // In-memory cache for Wikipedia lookup results: key = "lang:word", value = totalhits
  // Thread-safe concurrent map — survives across requests within the same JVM
  private val totalHitsCache =
    scala.collection.concurrent.TrieMap.empty[String, Either[String, Int]]

  // Aggressive HTTP timeouts: better to fall back to random words than hang
  private val HTTP_CONNECT_TIMEOUT_MS = 5000   // 5 seconds to connect
  private val HTTP_READ_TIMEOUT_MS    = 8000   // 8 seconds to get a response

  def getTotalHits(word: String, lang: String): Either[String, Int] = {
    val cacheKey = s"$lang:$word"

    // Check cache first — avoids redundant API calls
    totalHitsCache.get(cacheKey) match {
      case Some(result) => return result
      case None => // proceed to make the API call
    }

    val url = s"https://$lang.wikipedia.org/w/api.php?action=query&list=search&srsearch=${java.net.URLEncoder.encode(word, "UTF-8")}&format=json&srlimit=1"

    val baseHeaders = Map("User-Agent" -> "RandomWordAPI/1.0 (https://github.com/RazorSh4rk/random-word-api)")
    val headers = wikimediaToken match {
      case Some(token) => baseHeaders + ("Authorization" -> s"Bearer $token")
      case None => baseHeaders
    }

    val result = Try {
      val response = requests.get(url, headers = headers,
        connectTimeout = HTTP_CONNECT_TIMEOUT_MS, readTimeout = HTTP_READ_TIMEOUT_MS)
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

    // Cache the result so subsequent lookups for the same word are instant
    totalHitsCache(cacheKey) = result
    result
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

  // How many words to look up in parallel per batch.
  // 20 concurrent requests per batch gives us the speed of parallelism
  // without hammering the Wikipedia API.
  private val BATCH_SIZE = 20

  // How long to wait for a single batch before giving up and falling through.
  private val BATCH_TIMEOUT = 30.seconds

  // Filter words by difficulty using parallel batch processing.
  // Returns up to `count` words matching the target difficulty.
  // If rate limited, returns None to signal fallback to unfiltered results.
  def filterWordsByDifficulty(
    words: List[String],
    lang: String,
    targetDifficulty: Int,
    count: Int
  ): Option[List[String]] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val result = scala.collection.mutable.ListBuffer.empty[String]
    val iterator = words.iterator

    while (result.length < count && iterator.hasNext) {
      val batch = iterator.take(BATCH_SIZE).toList

      // Fire all lookups in this batch concurrently
      val futures = batch.map { word =>
        Future {
          (word, wordMatchesDifficulty(word, lang, targetDifficulty))
        }
      }

      // Wait for all parallel requests, with a safety net timeout
      val batchResults: List[(String, Either[String, Boolean])] = Try {
        Await.result(Future.sequence(futures), BATCH_TIMEOUT)
      } getOrElse {
        // Entire batch timed out — treat remaining words as failed
        // so we fall back to random selection
        return None
      }

      for ((word, matchResult) <- batchResults) {
        if (result.length < count) {
          matchResult match {
            case Right(true)   => result += word
            case Left("rate_limited") => return None
            case _             => // Skip words that don't match or had errors
          }
        }
      }
    }

    Some(result.toList)
  }
}
