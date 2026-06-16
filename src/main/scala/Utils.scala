import java.io.File
import scala.io.Source
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import scala.collection.mutable

object Utils {
  private lazy val languages: List[String] = {
    new File("languages/")
      .listFiles
      .filter(_.isFile)
      .toList
      .map(_.getName.replace(".json", ""))
  }

  private val languageCache: mutable.Map[String, JsValue] = mutable.Map.empty

  def getLanguages(): List[String] = languages

  def getLanguageFileContent(lang: String): JsValue = {
    languageCache.getOrElseUpdate(lang, {
      if (languages.contains(lang)) {
        Json.parse(Source.fromFile(s"languages/$lang.json").getLines.mkString)
      } else null
    })
  }
}
