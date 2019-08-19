package otserver4s.login

import java.net.Socket
import scala.util.{ Try, Success, Failure }
import otserver4s.{ Packet, PropriedadeConfiguracoes => PConf }
import otserver4s.database.{ Conta, Personagem }
import otserver4s.utils.MD5Utils

case class LoginRequest(
  accountNumber: Option[Int],
  password: Option[String],
  personagem: Option[Personagem] = None,
  sistemaOperacional: SistemaOperacional = SistemaOperacional.OUTRO,
  versao: Int = -1
) {
  def criarPacketLogin: Packet = {
    otserver4s.Client.logger.debug(this)
    val conexao = otserver4s.database.ConexaoBancoDados.criarConexao
    val loginPacket = Try(Conta(this, conexao)) match {
      case Success(loginOk) => {
        otserver4s.Client.logger.debug(
          s"Conta encontrada, carregando lista de personagens: $loginOk")
        val packet = Packet()
        packet.escreverByte(Packet.CODIGO_PACKET_LOGIN_SUCESSO)
        packet.escreverString(
          s"${Mundo.INSTANCE.motd.codigo}\n${Mundo.INSTANCE.motd.mensagem}")
        packet.escreverByte(Packet.MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS)
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
      case Failure(ex) => Packet.criarPacketErroLogin(ex.getMessage)
    }
    conexao.close
    loginPacket    
  }
  def processarLogin: Option[Packet] = {
    otserver4s.Client.logger.debug(this)
    val conexao = otserver4s.database.ConexaoBancoDados.criarConexao
    val loginPacket = Try(Conta(this, conexao, false)) match {
      case Success(loginOk) => {
        otserver4s.Client.logger.debug(s"Conta encontrada, processando login...")
        Personagem.buscarPersonagemPorNomeEAccount(
            this.personagem.get.nome, loginOk.number, conexao) match {
          case Some(personagem) => 
            if (otserver4s.Conectados.verificarSeConectado(personagem.nome))
              Some(Packet.criarPacketProcessarLoginErro(
                PConf("mensagem.login.erro.personagem.ja.conectado")))
            // TODO: Retornar None quando a implementação do IN-GAME estiver apresentável
            // FIXME: Retorno NÃO deve ser Option
            else Some(Packet.criarPacketProcessarLoginErro("In-game nao implementado..."))
            //----
          case None => Some(Packet.criarPacketProcessarLoginErro(
                         PConf("mensagem.login.erro.personagem.outra.conta")))
        }
      }
      case Failure(ex) => Some(Packet.criarPacketProcessarLoginErro(ex.getMessage))
    }
    conexao.close
    loginPacket
  }
}
object LoginRequest {
  val VERSAO = 760
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
      val persn = Packet.lerString(socket.getInputStream).map(nome => {
        val p = new Personagem
        p.nome = nome
        p
      })
      LoginRequest(sistemaOperacional = sistemaOperacional, versao = versao,
      accountNumber = accountNumber, personagem = persn,
      password = Packet.lerString(socket.getInputStream).map(MD5Utils.str2md5(_)))
    }
  }
}
