package otkserver

enum class TipoMensagem(val codigo: Byte) {
	AMARELA(0x01), AZUL_CLARO(0x04),
	EVENTO(0x14), LARANJA(0x11), AVISO(0x02),
	INFO(0x16), EVENTO_ANDAMENTO(0x13),
	AZUL(0x18), VERMELHO(0x19), STATUS(0x15),
	STATUS_DISCRETO(0x17);
	companion object {
		fun getByCodigo(codigo: Byte) =
			TipoMensagem.values().filter {
				it.codigo == codigo }.first()
	}
}

enum class Canal(val nome: String, val codigo: Int,
	val isPublico: Boolean, val isEspecial: Boolean) {
	CLAN("Clan", 0x00, false, false),
  VIOLACOES("Violação de regras", 0x03, false, true),
  CHAT("Chat", 0x04, true, false),
  TROCA("Troca", 0x05, true, false),
  RL_CHAT("Chat RL", 0x06, true, false),
  AJUDA("Ajuda", 0x07, true, false),
  DEVELS("Desenvolvedores", 0x08, false, true),
  TUTOR("Tutor", 0x09, false, true),
  GM("GM", 0x10, false, true),
  PRIVADO("Privado", 0xff, false, false)
}

data class Item(val codigo: Short)

enum class Direcao(val codigo: Byte) {
	NORTE(0x00), LESTE(0x01),
	SUL(0x02), OESTE(0x03),
  NORDESTE(0x04), NOROESTE(0x05),
  SUDESTE(0x06), SUDOESTE(0x07);
	companion object {
		fun getByCodigo(codigo: Byte) =
			Direcao.values().filter {
				it.codigo == codigo }.first()
	}
}

data class Posicao(
	val x: Int,
	val y: Int,
	val z: Byte) {
	fun moverParaDirecao(direcao: Direcao): Posicao =
		when(direcao) {
  		Direcao.LESTE  -> this.copy(x = x + 1)
			Direcao.NORTE -> this.copy(y = y - 1)
			Direcao.SUL -> this.copy(y = y + 1)
			Direcao.OESTE -> this.copy(x = x - 1)
			Direcao.NORDESTE ->
				this.moverParaDirecao(Direcao.NORTE)
					  .moverParaDirecao(Direcao.LESTE)
			Direcao.NOROESTE ->
				this.moverParaDirecao(Direcao.NORTE)
					  .moverParaDirecao(Direcao.OESTE)
			Direcao.SUDESTE ->
				this.moverParaDirecao(Direcao.SUL)
					  .moverParaDirecao(Direcao.LESTE)
			Direcao.SUDOESTE ->
				this.moverParaDirecao(Direcao.SUL)
					  .moverParaDirecao(Direcao.OESTE)
  	}
}

data class Luz(val raio: Int = 0xff, val cor: Int = 215)

data class Outfit(
	val tipo: Int, val cabeca: Int, val corpo: Int, 
  val pernas: Int, val pes: Int, val extra: Int)

enum class Caveira(val codigo: Byte) {
	NENHUMA(0x00), BRANCA(0x01),
	AMARELA(0x02), VERMELHA(0x03);
	companion object {
		fun getByCodigo(codigo: Byte) =
			Caveira.values().filter {
				it.codigo == codigo }.first()
	}
}

enum class Escudo(val codigo: Byte) {
	NENHUM(0x00)
}

enum class Slot(val codigo: Byte) {
  CABECA(0x01), AMULETO(0x02), MOCHILA(0x03),
	ARMADURA(0x04), MAO_DIREITA(0x05),
	MAO_ESQUERDA(0x06), PERNAS(0x07), PES(0x08),
	ANEL(0x09), EXTRA(0x0a), ULTIMO(0x0b);
	companion object {
		fun getByCodigo(codigo: Byte) =
			Slot.values().filter {
				it.codigo == codigo }.first()
	}
}

enum class TipoConversa(val codigo: Byte) {
  NORMAL(0x01), COCHICHAR(0x02), GRITAR(0x03),
  CANAL_AMARELO(0x05), CANAL_VIOL_REGRS(0x06),
  RESP_VIOL_REGRS(0x07), CONT_VIOL_REGRS(0x08),
  BROADCAST(0x09), PVT(0x04), CANAL_VERMELHO(0x0a),
  PVT_VERMELHO(0x0b), CANAL_LARANJA(0x0c),
  VERMELHO_ANONIMO(0x0d), MONSTRO(0x10),
	MONSTRO_BERRANDO(0x11)
}

enum class TipoPiso(val codigo: Byte) {
	GRAMA(0x6a)
}

data class Piso(val tipoPiso: TipoPiso,
  var entidades: ArrayList<Personagem> = ArrayList<Personagem>(0))

class Mapa {
	companion object {
    final val campoVisaoX = 8
    final val campoVisaoY = 6
		private var mapa = HashMap<Posicao, Piso>()
		fun inicializarMapa() {
			for(x in 0..100) for(y in 0..100)
				mapa.put(Posicao(x, y, 7),
					Piso(TipoPiso.GRAMA))
		}
		fun getPiso(posicao: Posicao) = mapa.get(posicao)
		fun moverEntidade(entidade: Personagem, posicao: Posicao) {
			getPiso(entidade.posicao())?.let { pisoEntidade ->
				if(pisoEntidade.entidades.contains(entidade)) {
					pisoEntidade.entidades.remove(entidade)
					getPiso(posicao)?.let { novoPiso ->
						novoPiso.entidades.add(entidade)
						entidade.posicao(posicao)
					}
				}
			}
		}
	}
}
