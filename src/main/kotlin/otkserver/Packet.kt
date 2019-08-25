package otkserver

import java.io.ByteArrayOutputStream
import java.io.InputStream
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession

data class Packet(
  private var tamanho: Int = 0,
  val buffer: ByteArray = ByteArray(0xffff - 2) { 0x00 }
) {
  companion object {
    val MARCACAO_PACKET_INICIO_LISTA_PERSONAGENS = 0x64
    val CODIGO_PACKET_LOGIN_SUCESSO = 0x14
    val CODIGO_PACKET_LOGIN_ERRO = 0x0a
    val CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO = 0x0a
    val CODIGO_PACKET_PROCESSAR_LOGIN_ERRO = 0x14
    fun lerByte(input: InputStream)  = input.read() and 0xff
    fun lerInt16(input: InputStream) = lerByte(input) or (lerByte(input) shl 8)
    fun lerInt32(input: InputStream) = lerByte(input) or
                                    (lerByte(input) shl 8) or
                                    (lerByte(input) shl 16) or
                                    (lerByte(input) shl 24)
    fun lerString(input: InputStream): String? {
      val saida =
		    String(ByteArray(Packet.lerInt16(input)) { Packet.lerByte(input).toByte() })
		  return if(saida.length > 0) saida else null
	  }
    fun pularLeitura(input: InputStream, n: Long) = input.skip(n)
	  private fun criarPacketGenericoMensagem(
		    codigo: Int, mensagem: String): Packet {
      val packet = Packet()
      packet.escreverByte(codigo)
      packet.escreverString(mensagem)
      return packet
    }
    fun criarPacketErroLogin(mensagemErro: String) = 
      Packet.criarPacketGenericoMensagem(
        Packet.CODIGO_PACKET_LOGIN_ERRO, mensagemErro)
    fun criarPacketProcessarLoginErro(mensagemErro: String) = 
      Packet.criarPacketGenericoMensagem(
        Packet.CODIGO_PACKET_PROCESSAR_LOGIN_ERRO, mensagemErro)
  }
  fun escreverByte(_byte: Int) {
	  buffer[tamanho++] = (_byte and 0xff).toByte()
  }
  fun escreverInt16(_int: Int) {
    escreverByte(_int and 0x00ff)
    escreverByte((_int and 0xff00) shr 8)
  }
  fun escreverInt32(_long: Long) {
    escreverByte( (_long and 0x000000ff).toInt())
    escreverByte(((_long and 0x0000ff00) shr 8).toInt())
    escreverByte(((_long and 0x00ff0000) shr 16).toInt())
    escreverByte(((_long and 0xff000000) shr 24).toInt())
  }
  fun escreverString(_str: String) {
    escreverInt16(_str.length)
    _str.toCharArray().forEach { c -> escreverByte(c.toByte().toInt()) }
  }
  fun enviar(sessao: IoSession, desconectar: Boolean) {
    val stream = ByteArrayOutputStream(tamanho)
	  stream.write(byteArrayOf((tamanho and 0x00ff).toByte(), ((tamanho and 0xff00) shr 8).toByte()) + buffer)
	  stream.flush()
	  sessao.write(IoBuffer.wrap(stream.toByteArray()))
	  if(desconectar) sessao.closeOnFlush()
  }
}
