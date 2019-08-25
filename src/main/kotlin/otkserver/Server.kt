package otkserver

import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.executor.ExecutorFilter

class Servidor(final val PORTA: Int):
	  org.apache.mina.core.service.IoHandlerAdapter() {

	companion object {
		val logger = org.apache.log4j.Logger.getLogger(Servidor::class.java)
		@JvmStatic
		fun main(args: Array<String>) {
			val conexao = ConexaoBancoDados.criarConexao()
			Conta.criarTabelaSeNaoExistir(conexao)
			Personagem.criarTabelaSeNaoExistir(conexao)
			val conta = Conta(123, "abc", 7)
			if(Conta.contarTodos(conexao) < 1)
				conta.salvar(conexao)
			if(Personagem.contarTodos(conexao) < 1)
				Personagem(nome = "Maia", conta = conta).salvar(conexao)
			conexao.close()
			logger.info("Iniciando OTServer...")
			Servidor(Mundo.INSTANCE.porta)
		}
	}

	init {
		val acceptor: org.apache.mina.core.service.IoAcceptor =
			org.apache.mina.transport.socket.nio.NioSocketAcceptor()
		acceptor.setHandler(this)
		acceptor.bind(java.net.InetSocketAddress(PORTA))
		logger.info("OTServer iniciado na porta $PORTA")
	}

	override fun sessionCreated(sessao: IoSession) {
		logger.debug("Sessão criada!")
	}
	
	override fun sessionOpened(sessao: IoSession) {
		logger.debug("Sessão aberta!")
	}
	
	override fun sessionClosed(sessao: IoSession) {
		AtributosSessao.deslogar(sessao)
		logger.debug("Sessão fechada!")
	}
	
	override fun sessionIdle(sessao: IoSession,
	    status: org.apache.mina.core.session.IdleStatus) {
		logger.debug("Sessão ociosa: $status")
	}
	
	override fun exceptionCaught(sessao: IoSession, causa: Throwable) {
		AtributosSessao.deslogar(sessao)
		logger.error("Exception -> ${causa}")
		causa.printStackTrace()
	}
	
	override fun messageReceived(sessao: IoSession, mensagem: Any) {
		val buffer = mensagem as IoBuffer
		val tamanhoPacket = Packet.lerInt16(buffer.asInputStream())
		logger.trace("Packet recebido - Tamanho: $tamanhoPacket")
		var desconectar = false
		val packet: Packet = when(
			TipoRequestLogin.getTipoRequestByCodigo(
				(Packet.lerByte(buffer.asInputStream()) and 0xff).toByte()
			)) {
			TipoRequestLogin.LOGIN_LISTA_PERSONAGENS ->
				try {
					desconectar = true
					ProtocoloLogin(buffer.asInputStream()).criarPacketLogin()
				}
    		catch(ex: OTServerLoginException) {
    			Packet.criarPacketErroLogin(
    			  ex.message?.let { it } ?: run {
    				  PropriedadeConfiguracoes("mensagem.login.erro.generico") })
    		}
			TipoRequestLogin.PROCESSAR_LOGIN ->
				try {
				  ProtocoloLogin(buffer.asInputStream(), true).processarLogin(sessao)
			  }
			  catch(ex: OTServerLoginException) {
				  Packet.criarPacketProcessarLoginErro(
					  ex.message?.let { it } ?: run {
    				  PropriedadeConfiguracoes("mensagem.login.erro.generico") })
			  }
			else -> Packet()
		}
		packet.enviar(sessao, desconectar)
	}
	
	override fun messageSent(sessao: IoSession, mensagem: Any) {
		logger.debug("Mensagem enviada: $mensagem")
	}
	
	override fun inputClosed(sessao: IoSession) {
		logger.debug("Input fechado!")
		AtributosSessao.deslogar(sessao)
		super.inputClosed(sessao)
	}

}
