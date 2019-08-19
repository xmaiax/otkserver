package otserver4s

import java.io.InputStream

case class TipoRequest(codigo: Byte, descricao: String)
object TipoRequest {
  val LOGIN_REQUEST = TipoRequest(0x01, "LoginRequest")
  val PROCESSAR_LOGIN = TipoRequest(0x0a, "ProcessarLogin")
  val values = Array(LOGIN_REQUEST, PROCESSAR_LOGIN)
  val INVALIDO = TipoRequest(-1, "Inválido")
  def tipoPorCodigo(codigo: Integer) =
    values.filter(_.codigo == codigo).headOption match {
    case Some(ok) => ok
    case None => INVALIDO
  }
}

case class Packet(
    private var posicao: Int = 2, 
    private var buffer: Array[Byte] = Array.ofDim[Byte](0xffff)) {
  def escreverByte(dado: Byte) = {
    buffer(posicao) = dado
    posicao += 1
  }
  def escreverInt16(num: Short) = {
    escreverByte(( num & 0x00ff      ).toByte)
    escreverByte(((num & 0xff00) >> 8).toByte)
  }
  def escreverInt32(num: Long) = {
    escreverByte( (num & 0x000000ff       ).toByte)
    escreverByte(((num & 0x0000ff00) >> 8 ).toByte)
    escreverByte(((num & 0x00ff0000) >> 16).toByte)
    escreverByte(((num & 0xff000000) >> 24).toByte)
  }
  def escreverBytes(dados: Array[Byte]) =
    for(i <- 0 until dados.length) buffer(posicao) = dados(i)
  def escreverString(str: String) = {
    escreverInt16(str.length.toShort)
    str.toCharArray.foreach(c => escreverByte(c.toByte))
  }
  def enviar(socket: java.net.Socket) = {
    val tamanho = (posicao - 2).toShort
    posicao = 0
    escreverInt16(tamanho)
    val pw = new java.io.PrintWriter(socket.getOutputStream, true)
    pw.write(buffer.splitAt(tamanho)._1.map(_.toChar))
    pw.close
  }
}
object Packet {
  val MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS: Byte = 0x64
  val CODIGO_PACKET_LOGIN_SUCESSO: Byte = 0x14
  val CODIGO_PACKET_LOGIN_ERRO: Byte = 0x0a
  val CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO: Byte = 0x0a
  val CODIGO_PACKET_PROCESSAR_LOGIN_ERRO: Byte = 0x14
  def lerByte(input: InputStream) = input.read
  def lerInt16(input: InputStream) = lerByte(input) | lerByte(input) << 8
  def lerInt32(input: InputStream) = lerByte(input)       | lerByte(input) << 8 |
                                     lerByte(input) << 16 | lerByte(input) << 24
  def lerString(input: InputStream) = {
    val array = Array.ofDim[Byte](Packet.lerInt16(input))
    Option(array).filter(_.length > 0)
      .map(array => array.map(b => Packet.lerByte(input).toByte))
      .map(new String(_))
  }
  def pularLeitura(input: InputStream, n: Long) = input.skip(n)
  private def criarPacketGenericoMensagem(codigo: Byte, mensagem: String) = {
    val packet = Packet()
    packet.escreverByte(codigo)
    packet.escreverString(mensagem)
    packet
  }
  def criarPacketErroLogin(mensagemErro: String) = 
    Packet.criarPacketGenericoMensagem(
      Packet.CODIGO_PACKET_LOGIN_ERRO, mensagemErro)
  def criarPacketProcessarLoginErro(mensagemErro: String) = 
    Packet.criarPacketGenericoMensagem(
      Packet.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO, mensagemErro)
}
