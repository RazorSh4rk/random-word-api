import scala.util.Random

object WordService {
  def filterByLength(words: List[String], length: Int): List[String] = {
    if (length == -1) words
    else words.filter(_.length == length)
  }

  def shuffleWords(words: List[String]): List[String] = {
    Random.shuffle(words)
  }

  def takeWords(words: List[String], count: Int): List[String] = {
    words.take(count)
  }

  def shouldApplyDifficultyFilter(diff: Int, number: Int): Boolean = {
    diff >= 1 && diff <= 5 && number <= 5
  }

  def padWithFallback(
    filtered: List[String],
    fallback: List[String],
    targetCount: Int
  ): List[String] = {
    if (filtered.length >= targetCount) {
      filtered.take(targetCount)
    } else {
      val needed = targetCount - filtered.length
      val padding = fallback.filterNot(filtered.contains).take(needed)
      filtered ++ padding
    }
  }

  def applyDifficultyFilter(
    shuffledWords: List[String],
    lang: String,
    diff: Int,
    number: Int
  ): List[String] = {
    if (!shouldApplyDifficultyFilter(diff, number)) {
      takeWords(shuffledWords, number)
    } else {
      val wikiLang = if (lang == "en") "en" else lang
      WikipediaFrequency.filterWordsByDifficulty(shuffledWords, wikiLang, diff, number) match {
        case Some(filtered) => padWithFallback(filtered, shuffledWords, number)
        case None => takeWords(shuffledWords, number)
      }
    }
  }

  def getRandomWords(
    words: List[String],
    number: Int,
    length: Int,
    lang: String,
    diff: Int
  ): List[String] = {
    val filtered = filterByLength(words, length)
    val shuffled = shuffleWords(filtered)
    applyDifficultyFilter(shuffled, lang, diff, number)
  }
}
