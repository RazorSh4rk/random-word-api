import java.net.URI
import com.redis._
import scala.util.Properties

object RateLimiter {
  private val redisUrl: URI =
    URI.create(Properties.envOrElse("REDIS_URL", "http://localhost:6379"))

  private val expirationSeconds = 5

  def check(ip: String): Boolean = {
    try {
      val redisConnection = new RedisClient(redisUrl)
      redisConnection.get(ip) match {
        case None =>
          redisConnection.set(ip, "limited")
          redisConnection.expire(ip, expirationSeconds)
          true
        case Some(_) => false
      }
    } catch {
      case _: RuntimeException => true
    }
  }

  def getKeys(password: String): Option[List[String]] = {
    val expectedPass = Properties.envOrElse("PASS", "")
    if (password.isEmpty || password != expectedPass) {
      None
    } else {
      try {
        val redisConnection = new RedisClient(redisUrl)
        val keys = redisConnection.keys("*") match {
          case None => List.empty[String]
          case Some(keyList) =>
            keyList.flatMap {
              case Some(k) => Some(k.toString)
              case None => None
            }
        }
        Some(keys)
      } catch {
        case _: RuntimeException => Some(List.empty[String])
      }
    }
  }
}
