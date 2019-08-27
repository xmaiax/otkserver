package otkserver

import org.apache.mina.core.session.IoSession

class ProtocoloLogado {
	companion object {
		fun agir(acao: AcaoInGame, sessao: IoSession) {
			AtributosSessao.CONTA_LOGADA.getAtributo(sessao)?.let {
				// val jogador = it as Personagem
				when(acao) {
					AcaoInGame.DESLOGAR -> AtributosSessao.deslogar(sessao)
					else -> println("Ação: $acao")
				}
			} ?: run {
				System.err.println("Jogador estava desconectado!")
				AtributosSessao.deslogar(sessao)
			}
		}
	}
}