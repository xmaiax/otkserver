package otkserver

import org.apache.mina.core.session.IoSession
import java.io.InputStream

class ProtocoloLogado {
	companion object {
		fun agir(acao: AcaoInGame, input: InputStream, sessao: IoSession) {
			AtributosSessao.CONTA_LOGADA.getAtributo(sessao)?.let {
				val jogador = it as Personagem
				when(acao) {
					AcaoInGame.DESLOGAR -> AtributosSessao.deslogar(sessao)
					AcaoInGame.FALAR -> {
						val tipoConversa =
							TipoConversa.getByCodigo(Packet.lerByte(input).toByte())
						val mensagem = Packet.lerString(input)
						println("$tipoConversa: $mensagem")
						val packet = Packet()
            			packet.escreverByte(0xaa)
            			packet.escreverString(jogador.nome)
            			packet.escreverByte(TipoConversa.NORMAL.codigo)
            			packet.escreverByte(50)
            			packet.escreverByte(0)
            			packet.escreverByte(50)
            			packet.escreverByte(0)
            			packet.escreverByte(7)
						mensagem?.let {
							packet.escreverString(it)
						}
						println("Tamanho packet: ${packet.tamanho}")
						packet.enviar(sessao, false)
					}
					else -> println("Ação: $acao")
				}
			} ?: run {
				System.err.println("Jogador estava desconectado!")
				AtributosSessao.deslogar(sessao)
			}
		}
	}
}