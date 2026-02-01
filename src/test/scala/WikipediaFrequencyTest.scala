import utest._

object WikipediaFrequencyTest extends TestSuite {
  val tests = Tests {
    test("getDifficultyTier") {
      test("tier 1 for hits >= 50000") {
        assert(WikipediaFrequency.getDifficultyTier(50000) == 1)
        assert(WikipediaFrequency.getDifficultyTier(100000) == 1)
        assert(WikipediaFrequency.getDifficultyTier(1000000) == 1)
      }

      test("tier 2 for hits 10000-49999") {
        assert(WikipediaFrequency.getDifficultyTier(10000) == 2)
        assert(WikipediaFrequency.getDifficultyTier(25000) == 2)
        assert(WikipediaFrequency.getDifficultyTier(49999) == 2)
      }

      test("tier 3 for hits 2000-9999") {
        assert(WikipediaFrequency.getDifficultyTier(2000) == 3)
        assert(WikipediaFrequency.getDifficultyTier(5000) == 3)
        assert(WikipediaFrequency.getDifficultyTier(9999) == 3)
      }

      test("tier 4 for hits 500-1999") {
        assert(WikipediaFrequency.getDifficultyTier(500) == 4)
        assert(WikipediaFrequency.getDifficultyTier(1000) == 4)
        assert(WikipediaFrequency.getDifficultyTier(1999) == 4)
      }

      test("tier 5 for hits < 500") {
        assert(WikipediaFrequency.getDifficultyTier(0) == 5)
        assert(WikipediaFrequency.getDifficultyTier(100) == 5)
        assert(WikipediaFrequency.getDifficultyTier(499) == 5)
      }
    }
  }
}
