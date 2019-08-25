package otkserver

class MD5Utils {
	companion object {
		private val messageDigest = java.security.MessageDigest.getInstance("MD5")
    private val SALT = PropriedadeConfiguracoes("senha.salt")
    fun str2md5(senha: String) =
		  java.math.BigInteger(1,
			  messageDigest.digest("${senha}${SALT}".toByteArray(
			    java.nio.charset.StandardCharsets.UTF_8))
		  ).toString(16).padStart(32, '0')
	}
}
