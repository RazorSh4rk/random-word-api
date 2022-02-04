import java.io.File
import scala.io.Source
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object Utils {
  def getLanguages(): List[String] = {
        new File("languages/")
            .listFiles
            .filter(_.isFile)
            .toList
            .map(el => el.toString
                .split("/")
                .last
                .replace(".json", "")
            )
  }

  def getLanguageFileContent(lang: String): JsValue = {
      if(getLanguages.contains(lang)) {
        Json.parse(
            Source.fromFile(s"languages/$lang.json").getLines.mkString
        )
      } else null
  }
}
