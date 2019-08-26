package otkserver

class Mapa {
	companion object {
		final val PONTO_RESPAWN = Posicao(50, 50, 7)
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