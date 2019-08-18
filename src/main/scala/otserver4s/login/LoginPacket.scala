package otserver4s.login

import java.net.Socket
import scala.util.{ Try, Success, Failure }
import otserver4s.Packet
import otserver4s.database.{ Conta, Personagem }
import otserver4s.utils.MD5Utils

case class LoginRequest(
  accountNumber: Option[Int],
  password: Option[String],
  nomePersonagem: Option[String] = None,
  sistemaOperacional: SistemaOperacional = SistemaOperacional.OUTRO,
  versao: Int = -1
) {
  def criarPacketLogin: Packet = {
    otserver4s.Client.logger.debug(this)
    val conexao = otserver4s.database.ConexaoBancoDados.criarConexao
    val loginPacket = Try(Conta(this, conexao)) match {
      case Success(loginOk) => {
        otserver4s.Client.logger.debug(loginOk)
        val packet = Packet()
        packet.escreverByte(LoginRequest.CODIGO_PACKET_LOGIN_SUCESSO)
        packet.escreverString(
          s"${Mundo.INSTANCE.motd.codigo}\n${Mundo.INSTANCE.motd.mensagem}")
        packet.escreverByte(LoginRequest.MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS)
        packet.escreverByte(loginOk.personagens.length.toByte)
        loginOk.personagens.foreach(personagem => {
          packet.escreverString(personagem.nome)
          packet.escreverString(Mundo.INSTANCE.nome)
          java.net.InetAddress.getByName(Mundo.INSTANCE.host)
            .getHostAddress.split("[.]")
            .map(Integer.parseInt(_) & 0xff).map(_.toByte)
            .foreach(packet.escreverByte(_))
          packet.escreverInt16(Mundo.INSTANCE.porta.toShort)
        })
        packet.escreverInt16(loginOk.diasPremiumRestantes.toShort)
        packet
      }
      case Failure(ex) => {
        val packet = Packet()
        packet.escreverByte(LoginRequest.CODIGO_PACKET_LOGIN_ERRO)
        packet.escreverString(ex.getMessage)
        packet
      }
    }
    conexao.close
    loginPacket    
  }
  def processarLogin = {
    otserver4s.Client.logger.debug(this)
    val conexao = otserver4s.database.ConexaoBancoDados.criarConexao
    val loginPacket = Try(Conta(this, conexao, false)) match {
      case Success(loginOk) => {
        otserver4s.Client.logger.debug(loginOk)
        val packet = Packet()
        Personagem.buscarPersonagemPorNomeEAccount(
            this.nomePersonagem.get, loginOk.number, conexao) match {
          case Some(personagem) => 
            if (otserver4s.Conectados.verificarSeConectado(personagem.nome)) {
              packet.escreverByte(LoginRequest.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO)
              packet.escreverString("Personagem ja conectado.")
            }
            else {
              otserver4s.Client.logger.debug(personagem)
              // TODO: Se personagem pode spawnar na própria posição e não está logado, então...
              // TODO: >  Colocar ele no mapa!
              // TODO: Se não: enviar mensagem com erro
              packet.escreverByte(LoginRequest.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO)
              packet.escreverString("In-game nao implementado...")
            }
          case None => {
            packet.escreverByte(LoginRequest.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO)
            packet.escreverString(s"Personagem ${this.nomePersonagem.get} nao pertence a conta.")
          }
        }
        packet
      }
      case Failure(ex) => {
        val packet = Packet()
        packet.escreverByte(LoginRequest.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO)
        packet.escreverString(ex.getMessage)
        packet
      }
    }
    conexao.close
    loginPacket
  }
}
object LoginRequest {
  val VERSAO = 760
  val MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS: Byte = 0x64
  val CODIGO_PACKET_LOGIN_SUCESSO: Byte = 0x14
  val CODIGO_PACKET_LOGIN_ERRO: Byte = 0x0a
  val CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO: Byte = 0x0a
  val CODIGO_PACKET_PROCESSAR_LOGIN_ERRO: Byte = 0x14
  def apply(socket: Socket, processarLogin: Boolean): LoginRequest = {
    val sistemaOperacional = SistemaOperacional.lerSistemaOperacional(
      Packet.lerInt16(socket.getInputStream)) 
    val versao = Packet.lerInt16(socket.getInputStream)
    if(!processarLogin) {
      Packet.pularLeitura(socket.getInputStream, 12)
      LoginRequest(sistemaOperacional = sistemaOperacional, versao = versao,
        accountNumber = Option(Packet.lerInt32(socket.getInputStream)).filter(_ > 0),
        password = Packet.lerString(socket.getInputStream)
          .map(otserver4s.utils.MD5Utils.str2md5(_)))
    }
    else {
      if(Packet.lerByte(socket.getInputStream) == 0x01) 
        otserver4s.Client.logger.debug(s"Client é GM (?!?)")
      val accountNumber = Option(Packet.lerInt32(socket.getInputStream)).filter(_ > 0)
      val nomePersonagem = Packet.lerString(socket.getInputStream)
      LoginRequest(sistemaOperacional = sistemaOperacional, versao = versao,
      accountNumber = accountNumber, nomePersonagem = nomePersonagem,
      password = Packet.lerString(socket.getInputStream).map(MD5Utils.str2md5(_)))
    }
  }
}
