package otserver4s

import java.net.SocketException
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
  def remover(client: Client) = clientsConectados -= client
  def removerPorNome(nome: String) = buscarPorNome(nome).map(remover(_))
  def verificarSeConectado(nome: String) = buscarPorNome(nome).isDefined
}

object Client {
  val MENSAGEM_SOCKET_FECHADO = "Socket is closed"
  val logger = Logger.getLogger(classOf[Client]) 
}
case class Client(socket: java.net.Socket, var loop: Boolean = true,
    var jogador: Option[Personagem] = None) extends Thread {

  def packetRecebido() = {
    if(jogador.isEmpty) {
      val tamanhoPacket = Packet.lerInt16(socket.getInputStream)
      val tipoRequest = TipoRequest.tipoPorCodigo(Packet.lerByte(socket.getInputStream))
      Client.logger.debug(s"Packet recebido - Tamanho: $tamanhoPacket - $tipoRequest")
      val packet = tipoRequest match {
        case TipoRequest.LOGIN_REQUEST => 
          LoginRequest(socket, false).criarPacketLogin.enviar(socket, true)
        case TipoRequest.PROCESSAR_LOGIN => {
          val loginRequest = LoginRequest(socket, true)
          jogador = loginRequest.personagem
          Option(loginRequest.processarLogin) match {
            case Some(erro) => erro.enviar(socket, true)
            case None => iniciarConexaoAtiva(this).enviar(socket)
          }
        }
        case _ => Client.logger.error(s"Request desconhecido")
      }
    }
    if(jogador.isDefined) {
      socket.setKeepAlive(true)
      socket.setSoTimeout(8192)
      manterConexaoAtiva(this)
    }
    desconectar(new SocketException(Client.MENSAGEM_SOCKET_FECHADO))
  }
  
  def desconectar(ex: Throwable = null) = {
    loop = false
    socket.close
    Option(ex) match {
      case None => Unit
      case Some(ex) if (ex.getClass   == classOf[SocketException] && 
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
    jogador = None
  }
  
  override def run = while(socket != null && socket.isConnected && loop)
    Try(socket.getInputStream.available > 0) match {
      case Failure(ex) => desconectar(ex)
      case _ => packetRecebido
    }

}

import otserver4s.database.ConexaoBancoDados.criarConexao
import otserver4s.database.{ ConexaoBancoDados, Conta, Personagem }
import scala.util.Success

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
  
  Try(new java.net.ServerSocket(PORTA)) match {
    case Success(server) => {
      GameServer().logger.info(s"Servidor iniciado na porta $PORTA...")
      while(true) Client(server.accept).start
    }
    case Failure(ex: java.net.BindException) =>
      GameServer().logger.fatal(
        s"Um serviço rodando na porta $PORTA está impedindo a inicialização do OTServer...")
    case Failure(ex) => {
      ex.printStackTrace
      GameServer().logger.fatal(
        s"Erro ao iniciar servidor na porta $PORTA: ${ex.getMessage}")
    }
  }
}
