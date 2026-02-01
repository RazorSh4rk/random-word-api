import utest._

object WordServiceTest extends TestSuite {
  val tests = Tests {
    test("filterByLength") {
      test("returns all words when length is -1") {
        val words = List("a", "bb", "ccc", "dddd")
        val result = WordService.filterByLength(words, -1)
        assert(result == words)
      }

      test("filters words by exact length") {
        val words = List("a", "bb", "ccc", "dddd", "ee")
        val result = WordService.filterByLength(words, 2)
        assert(result == List("bb", "ee"))
      }

      test("returns empty list when no words match length") {
        val words = List("a", "bb", "ccc")
        val result = WordService.filterByLength(words, 5)
        assert(result.isEmpty)
      }
    }

    test("takeWords") {
      test("takes specified number of words") {
        val words = List("a", "b", "c", "d", "e")
        val result = WordService.takeWords(words, 3)
        assert(result == List("a", "b", "c"))
      }

      test("returns all words when count exceeds list size") {
        val words = List("a", "b")
        val result = WordService.takeWords(words, 5)
        assert(result == List("a", "b"))
      }

      test("returns empty list when count is 0") {
        val words = List("a", "b", "c")
        val result = WordService.takeWords(words, 0)
        assert(result.isEmpty)
      }
    }

    test("shouldApplyDifficultyFilter") {
      test("returns true for valid diff and number <= 5") {
        assert(WordService.shouldApplyDifficultyFilter(1, 5) == true)
        assert(WordService.shouldApplyDifficultyFilter(3, 3) == true)
        assert(WordService.shouldApplyDifficultyFilter(5, 1) == true)
      }

      test("returns false when diff is out of range") {
        assert(WordService.shouldApplyDifficultyFilter(0, 3) == false)
        assert(WordService.shouldApplyDifficultyFilter(6, 3) == false)
        assert(WordService.shouldApplyDifficultyFilter(-1, 3) == false)
      }

      test("returns false when number > 5") {
        assert(WordService.shouldApplyDifficultyFilter(3, 6) == false)
        assert(WordService.shouldApplyDifficultyFilter(1, 10) == false)
      }
    }

    test("padWithFallback") {
      test("returns filtered when it has enough words") {
        val filtered = List("a", "b", "c")
        val fallback = List("a", "b", "c", "d", "e")
        val result = WordService.padWithFallback(filtered, fallback, 3)
        assert(result == List("a", "b", "c"))
      }

      test("pads with fallback when filtered is short") {
        val filtered = List("a")
        val fallback = List("a", "b", "c", "d", "e")
        val result = WordService.padWithFallback(filtered, fallback, 3)
        assert(result == List("a", "b", "c"))
      }

      test("excludes already filtered words from padding") {
        val filtered = List("b")
        val fallback = List("a", "b", "c", "d")
        val result = WordService.padWithFallback(filtered, fallback, 3)
        assert(result == List("b", "a", "c"))
      }

      test("truncates if filtered has more than target") {
        val filtered = List("a", "b", "c", "d", "e")
        val fallback = List("a", "b", "c", "d", "e")
        val result = WordService.padWithFallback(filtered, fallback, 2)
        assert(result == List("a", "b"))
      }
    }

    test("shuffleWords") {
      test("returns same size list") {
        val words = List("a", "b", "c", "d", "e")
        val result = WordService.shuffleWords(words)
        assert(result.size == words.size)
      }

      test("contains all original words") {
        val words = List("a", "b", "c", "d", "e")
        val result = WordService.shuffleWords(words)
        assert(result.sorted == words.sorted)
      }
    }
  }
}
