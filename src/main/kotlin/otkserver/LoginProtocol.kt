package otkserver

import java.io.InputStream
import org.apache.mina.core.session.IoSession

enum class SistemaOperacional(private val tipo: String) {
  UNIX_LIKE("Unix-like"), WINDOWS("Windows"), OUTRO("Outro");
  override fun toString() = this.tipo
  companion object {
    fun lerSistemaOperacional(codigoPacket: Int) =
      when (codigoPacket) {
        0x01 -> UNIX_LIKE
        0x02 -> WINDOWS
        else -> OUTRO
      }
    }
}

data class MOTD(
	val mensagem: String,
	val codigo: Byte = 0x01
) {
	override fun toString() = "${codigo}\n${mensagem}"
}
data class Mundo(
  val nome: String = PropriedadeConfiguracoes("mundo"),
  val porta: Int = PropriedadeConfiguracoes("porta").toInt(),
  val host: String = PropriedadeConfiguracoes("host"),
  val motd: MOTD = MOTD(PropriedadeConfiguracoes("motd"))) {
  companion object { val INSTANCE = Mundo() }
}

fun Account(buffer: InputStream): Account {
	val numero = Packet.lerInt32(buffer)
	val senha = Packet.lerString(buffer)
	if(numero < 1) throw OTServerLoginException("mensagem.login.conta.vazia")
	senha?.let { return Account(numero, MD5Utils.str2md5(it)) }
	throw OTServerLoginException("mensagem.login.senha.vazia")
}
data class Account(val number: Int, val password: String)

fun ProtocoloLogin(
	buffer: InputStream,
	selecionouPersonagem: Boolean = false): ProtocoloLogin {
	val sistemaOperacional = SistemaOperacional
		.lerSistemaOperacional(Packet.lerInt16(buffer))
  val versao = Packet.lerInt16(buffer)
	if(ProtocoloLogin.VERSAO != versao)
		throw OTServerLoginException("mensagem.login.versao.errada")
	if(selecionouPersonagem) {
		val isClientGM = Packet.lerByte(buffer) == ProtocoloLogin.FLAG_GM_CLIENT
		val numeroConta = Packet.lerInt32(buffer)
		val personagem = Packet.lerString(buffer)
		Packet.lerString(buffer)?.let { senha ->
		  return ProtocoloLogin(
  			sistemaOperacional, versao,
  			Account(numeroConta, MD5Utils.str2md5(senha)),
  			isClientGM, personagem
  		)
		}
		throw OTServerLoginException()
	}
	else {
    Packet.pularLeitura(buffer, 12)
    return ProtocoloLogin(sistemaOperacional, versao,
		  Account(buffer))
	}
}
data class ProtocoloLogin(
  val sistemaOperacional: SistemaOperacional,
  val versao: Int,
  val account: Account,
  val isClientGM: Boolean? = null,
  val personagem: String? = null
) {
	companion object {
		val VERSAO = PropriedadeConfiguracoes("versao").toInt()
		val FLAG_GM_CLIENT = 0x01
		val logger = org.apache.log4j.Logger.getLogger(ProtocoloLogin::class.java)
	}
	fun criarPacketLogin(): Packet {
		logger.debug(this)
		val conexao = ConexaoBancoDados.criarConexao()
		val conta = Conta.buscarPorProtocoloLogin(this, conexao)
		val packet = Packet()
    packet.escreverByte(Packet.CODIGO_PACKET_LOGIN_SUCESSO)
    packet.escreverString(Mundo.INSTANCE.motd.toString())
    packet.escreverByte(Packet.MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS)
		conta.personagens?.let {
			packet.escreverByte(it.size and 0xff)
			it.forEach { personagem ->
			  packet.escreverString(personagem.nome)
        packet.escreverString(Mundo.INSTANCE.nome)
        java.net.InetAddress.getByName(Mundo.INSTANCE.host)
			    .getHostAddress().split(".").forEach {
					  blocoIp -> packet.escreverByte(blocoIp.toInt()) }
        packet.escreverInt16(Mundo.INSTANCE.porta)
			}
		} ?: run { packet.escreverByte(0x00) }
    packet.escreverInt16(conta.diasPremiumRestantes)
		conexao.close()
		return packet
	}
	fun processarLogin(sessao: IoSession): Packet {
		logger.debug(this)
		personagem?.let { nomePersonagemLogando ->
			val conexao = ConexaoBancoDados.criarConexao()
			val p = Personagem.buscarPorNome(nomePersonagemLogando, conexao)
			conexao.close()
			if(account.number.equals(p.conta.codigo) and
				 account.password.equals(p.conta.hashSenha)) {
				AtributosSessao.CONTA_LOGADA.setAtributo(sessao, p)
				val packet = Packet.criarPacketProcessarLoginErro("In-game nao implementado...")
				return packet
			}
		}
		throw OTServerLoginException()
	}
}

enum class TipoRequestLogin(private val codigo: Byte) {
	LOGIN_LISTA_PERSONAGENS(0x01),
	PROCESSAR_LOGIN(0x0a);
  override fun toString() = this.name
  companion object {
    fun getTipoRequestByCodigo(codigo: Byte): TipoRequestLogin? =
      TipoRequestLogin.values().filter { it.codigo == codigo }.first()
    }
}

enum class AtributosSessao {
	CONTA_LOGADA;

	override fun toString() = this.name
	fun getAtributo(sessao: IoSession): Any = sessao.getAttribute(this)
	fun setAtributo(sessao: IoSession, objeto: Any) = sessao.setAttribute(this, objeto)
	fun apagarAtributo(sessao: IoSession) = this.setAtributo(sessao, false)

	companion object {
		fun deslogar(sessao: IoSession) =
			AtributosSessao.CONTA_LOGADA.apagarAtributo(sessao)
	}
}
