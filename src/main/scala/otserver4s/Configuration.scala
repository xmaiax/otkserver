package otserver4s

import scala.util.{ Try, Success, Failure }
import org.apache.log4j.Logger

case class ConfiguracoesOtServer(properties: java.util.Properties)
object ConfiguracoesOtServer {
  private val logger: Logger = Logger.getLogger(classOf[ConfiguracoesOtServer])
  val INSTANCE = ConfiguracoesOtServer("otserver.properties")
  def apply(arquivoProperties: String): ConfiguracoesOtServer = 
    Try(ConfiguracoesOtServer.getClass.getClassLoader
        .getResourceAsStream(arquivoProperties)
        .asInstanceOf[java.io.InputStream]) match {
    case Success(input) => {
      val properties = new java.util.Properties
      properties.load(input)
      logger.debug("Configurações carregadas com sucesso!")
      logger.trace(properties)
      ConfiguracoesOtServer(properties)
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
    ConfiguracoesOtServer.INSTANCE.properties.getProperty(chave)
}
