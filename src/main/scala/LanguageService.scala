import play.api.libs.json._

object LanguageService {
  def getAvailableLanguages(): List[String] = {
    Utils.getLanguages()
  }

  def isLanguageSupported(lang: String): Boolean = {
    lang == "en" || Utils.getLanguages().contains(lang.toLowerCase)
  }

  def getWordsForLanguage(lang: String, englishWords: JsValue): Either[String, List[String]] = {
    if (lang == "en") {
      Right(englishWords.as[List[String]])
    } else {
      val content = Utils.getLanguageFileContent(lang.toLowerCase)
      if (content != null) {
        Right(content.as[List[String]])
      } else {
        Left("No translation for this language")
      }
    }
  }

  def getAllWordsJson(lang: String, englishWords: JsValue): Either[String, JsValue] = {
    if (lang == "en") {
      Right(englishWords)
    } else {
      val content = Utils.getLanguageFileContent(lang.toLowerCase)
      if (content != null) {
        Right(content)
      } else {
        Left("No translation for this language")
      }
    }
  }
}
