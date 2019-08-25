package otkserver

import org.apache.log4j.Logger

fun ConfiguracoesOTServer(arquivoProperties: String): ConfiguracoesOTServer {
	val properties = java.util.Properties()
	try {
		ConfiguracoesOTServer::class.java.classLoader
			.getResourceAsStream(arquivoProperties).use {
		  if(it == null)
			  throw java.lang.IllegalStateException("InputStream vazio!")
			properties.load(it)
		  ConfiguracoesOTServer.logger.debug(
			  "Configurações carregadas com sucesso!")
      ConfiguracoesOTServer.logger.trace(properties)
		}
	}
	catch(ex: Exception) {
		ConfiguracoesOTServer.logger.fatal(
			"Ocorreu um erro ao carregar configurações: ${ex.message}")
		ex.printStackTrace()
		System.exit(-1)
	}
	return ConfiguracoesOTServer(properties)
}
data class ConfiguracoesOTServer(
	private val properties: java.util.Properties) {
	companion object {
		val logger = Logger.getLogger(ConfiguracoesOTServer::class.java)
		val INSTANCE = ConfiguracoesOTServer("otserver.properties")
	}
	fun getParametro(nome: String) = properties.getProperty(nome)
}
 
fun PropriedadeConfiguracoes(nome: String) =
  ConfiguracoesOTServer.INSTANCE.getParametro(nome)

class OTServerLoginException(
	val mensagemErro: String = "mensagem.login.erro.generico"):
	Exception(PropriedadeConfiguracoes(mensagemErro))
