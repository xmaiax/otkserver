package otserver4s

import java.net.{ SocketException => ScktExcp }
import scala.util.{ Try, Failure }
import org.apache.log4j.Logger
import otserver4s.database.Personagem
import otserver4s.game.PacketInGame.{ iniciarConexaoAtiva, manterConexaoAtiva }
import otserver4s.login.LoginRequest
import otserver4s.login.Mundo.INSTANCE.{ porta => PORTA }

object Conectados {
  private var clientsConectados = new scala.collection.mutable.ListBuffer[Client]()
  private def buscarPorNome(nome: String) =
    clientsConectados.filter(_.jogador.get.nome == nome).headOption
  def getJogadoresOnline = clientsConectados.toList.map(_.jogador.get)
  def adicionar(client: Client) = clientsConectados += client
  def remover(client: Client) = {
    clientsConectados -= client
    client.loop = false
    if(!client.socket.isClosed) client.socket.close
  }
  def removerPorNome(nome: String) = buscarPorNome(nome).map(remover(_))
  def verificarSeConectado(nome: String) = buscarPorNome(nome).isDefined
}

object Client {
  val MENSAGEM_SOCKET_FECHADO = "Socket is closed"
  val logger = Logger.getLogger(classOf[Client]) 
}
case class Client(socket: java.net.Socket,
  var jogador: Option[Personagem] = None,
  var loop: Boolean = true) extends Thread {

  def packetRecebido() = {
    loop = false
    val tamanhoPacket = Packet.lerInt16(socket.getInputStream)
    val tipoRequest = TipoRequest.tipoPorCodigo(Packet.lerByte(socket.getInputStream))
    Client.logger.debug(s"Packet recebido - Tamanho: $tamanhoPacket - $tipoRequest")
    val packet = tipoRequest match {
      case TipoRequest.LOGIN_REQUEST => {
        LoginRequest(socket, false).criarPacketLogin
      }
      case TipoRequest.PROCESSAR_LOGIN => {
        val loginRequest = LoginRequest(socket, true)
        jogador = loginRequest.personagem
        loginRequest.processarLogin.getOrElse(iniciarConexaoAtiva(this))
      }
      case _ => {
        Client.logger.error(s"Request desconhecido")
        Packet()
      }
    }
    packet.enviar(socket)
    while(loop && jogador.isDefined) manterConexaoAtiva(this)
    desconectar(new ScktExcp(Client.MENSAGEM_SOCKET_FECHADO))
  }
  
  def desconectar(ex: Throwable = null) = {
    socket.close
    Option(ex) match {
      case None => Unit
      case Some(ex) if (ex.getClass   == classOf[ScktExcp] && 
                        ex.getMessage == Client.MENSAGEM_SOCKET_FECHADO) => 
          Client.logger.debug(
            if(jogador.isEmpty) "Client desconectado sem login" 
            else s"${jogador.get.nome} foi desconectado...")
      case _ => {
        Client.logger.error(
          s"Ocorreu um erro ao processar packet: ${ex.getMessage}")
        ex.printStackTrace
      }
    }
    Conectados.remover(this)
    loop = false
    jogador = None
  }
  
  override def run = while(loop) 
    Try(socket.getInputStream.available > 0) match {
      case Failure(ex) => desconectar(ex)
      case _ => packetRecebido
    }

}

import otserver4s.database.ConexaoBancoDados.criarConexao
import otserver4s.database.{ ConexaoBancoDados, Conta, Personagem }
object Server extends App {
  case class GameServer(logger: Logger = Logger.getLogger(classOf[GameServer]))
  
  val conexao = criarConexao
  Conta.criarTabelaSeNaoExistir(conexao)
  if(Conta.contarTodos(conexao) < 1)
    Conta.persistirNova(123, "abc", conexao)
  Personagem.criarTabelaSeNaoExistir(conexao)
  if(Personagem.contarTodos(conexao) < 1)
    Personagem.persistirNovo(123, "Maia", conexao)
  conexao.close
  
  val server = new java.net.ServerSocket(PORTA)
  GameServer().logger.info(s"Servidor iniciado na porta $PORTA...")
  while(true) Client(server.accept).start
}
