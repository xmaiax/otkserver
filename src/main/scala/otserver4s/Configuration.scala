package otserver4s

import scala.util.{ Try, Success, Failure }
import org.apache.log4j.Logger

case class ConfiguracoesOTServer(properties: java.util.Properties)
object ConfiguracoesOTServer {
  private val logger: Logger = Logger.getLogger(classOf[ConfiguracoesOTServer])
  val INSTANCE = ConfiguracoesOTServer("otserver.properties")
  def apply(arquivoProperties: String): ConfiguracoesOTServer = 
    Try(ConfiguracoesOTServer.getClass.getClassLoader
        .getResourceAsStream(arquivoProperties)
        .asInstanceOf[java.io.InputStream]) match {
    case Success(input) => {
      val properties = new java.util.Properties
      properties.load(input)
      logger.debug("Configurações carregadas com sucesso!")
      logger.trace(properties)
      ConfiguracoesOTServer(properties)
    }
    case Failure(ex) => {
      logger.fatal(s"Ocorreu um erro ao carregar configurações: ${ex.getMessage}")
      ex.printStackTrace
      System.exit(-1)
      null
    }
  }
}
object PropriedadeConfiguracoes {
  def apply(chave: String): String = 
    ConfiguracoesOTServer.INSTANCE.properties.getProperty(chave)
}
