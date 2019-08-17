package otserver4s.utils

object MD5Utils {
  private val messageDigest = java.security.MessageDigest.getInstance("MD5")
  private val SALT = otserver4s.PropriedadeConfiguracoes("senha.salt")
  def str2md5(senha: String) = {
    messageDigest.digest(senha.concat(SALT)
      .getBytes(java.nio.charset.StandardCharsets.UTF_8))
      .map(b => String.format("%02x", b.asInstanceOf[Object])).mkString
  }
}
