package otserver4s.login

import java.net.Socket
import scala.util.{ Try, Success, Failure }
import otserver4s.Packet

case class LoginRequest(
  accountNumber: Option[Int],
  password: Option[String],
  sistemaOperacional: SistemaOperacional = SistemaOperacional.OUTRO,
  versao: Int = -1
) {
  def criarPacketLogin: Packet = {
    otserver4s.Client.logger.debug(this)
    val conexao = otserver4s.database.ConexaoBancoDados.criarConexao
    val loginPacket = Try(otserver4s.database.Conta(this, conexao)) match {
      case Success(loginOk) => {
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
}
object LoginRequest {
  val CODIGO_PACKET_LOGIN_SUCESSO: Byte = 0x14
  val CODIGO_PACKET_LOGIN_ERRO: Byte = 0x0a
  val MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS: Byte = 0x64
  def apply(socket: Socket): LoginRequest = {
    val sistemaOperacional = SistemaOperacional.lerSistemaOperacional(
      Packet.lerInt16(socket.getInputStream)) 
    val versao = Packet.lerInt16(socket.getInputStream)
    Packet.pularLeitura(socket.getInputStream, 12)
    LoginRequest(sistemaOperacional = sistemaOperacional, versao = versao,
      accountNumber = Option(Packet.lerInt32(socket.getInputStream)).filter(_ > 0),
      password = Packet.lerString(socket.getInputStream)
        .map(otserver4s.utils.MD5Utils.str2md5(_)))
  }
}
