package otkserver

enum class Vocacao(
	val codigo: Byte, val nome: String,
	val vidaBase: Int, val vidaLevel: Int,
	val manaBase: Int, val manaLevel: Int,
	val capacidadeBase: Int, val capacidadeLevel: Int,
	val almaBase: Int) {
	NECROMANCER(0x01, "Necromancer", 100, 10, 50, 20, 300, 10, 150),
	GUERREIRO(0x02, "Guerreiro", 200, 25, 20, 5, 450, 15, 100),
	MONGE(0x03, "Monge", 175, 20, 25, 10, 400, 10, 125),
	MAGO(0X04, "Mago", 125, 5, 75, 25, 250, 5, 150);
	companion object {
		fun getByCodigo(codigo: Byte) =
			Vocacao.values().filter {
				it.codigo == codigo }.first()
	}
	override fun toString() = nome
}

class ExpUtils {
	companion object {
		private fun qtdExp(level: Int) = (Math.pow(level.toDouble(), 1.5) * 100).toLong()
		fun levelDaExp(exp: Long) = Math.cbrt(Math.pow(
			java.math.BigDecimal.valueOf(exp).toDouble() / 100.0, 2.0)).toInt()
		fun percentualProxLevel(exp: Long): Byte {
			val levelAtual = levelDaExp(exp)
			val expBaseLevelAtual = qtdExp(levelAtual)
			return (((exp - expBaseLevelAtual) * 100) /
			  (qtdExp(levelAtual + 1) - expBaseLevelAtual)).toByte()
		}
	}
}
