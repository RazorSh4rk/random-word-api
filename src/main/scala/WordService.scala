import scala.util.Random

object WordService {
  def filterByLength(words: List[String], length: Int): List[String] = {
    if (length == -1) words
    else words.filter(_.length == length)
  }

  /**
   * Pick `count` random elements from `words` without copying the full list.
   * Uses a partial Fisher-Yates shuffle on an array copy.
   */
  def pickRandom(words: List[String], count: Int): List[String] = {
    val n = count.min(words.length)
    if (n == 0) List.empty
    else if (n >= words.length) words
    else {
      val arr = words.toArray
      val rng = new Random()
      for (i <- 0 until n) {
        val j = i + rng.nextInt(arr.length - i)
        val tmp = arr(i)
        arr(i) = arr(j)
        arr(j) = tmp
      }
      arr.take(n).toList
    }
  }

  /**
   * Full shuffle of the word list (used when difficulty filter needs
   * the full shuffled list as fallback).
   */
  def shuffleWords(words: List[String]): List[String] = {
    val arr = words.toArray
    val rng = new Random()
    for (i <- (arr.length - 1) to 0 by -1) {
      val j = rng.nextInt(i + 1)
      val tmp = arr(i)
      arr(i) = arr(j)
      arr(j) = tmp
    }
    arr.toList
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
    if (shouldApplyDifficultyFilter(diff, number)) {
      // difficulty filter needs the full shuffled list for fallback
      val shuffled = shuffleWords(filtered)
      applyDifficultyFilter(shuffled, lang, diff, number)
    } else {
      // fast path: pick random elements without full shuffle
      pickRandom(filtered, number)
    }
  }
}
