package me.jiangcai.deploy.tools.job

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * @author CJ
 */
class PrintKey(
    private val path: String? = null
) : Runnable {

    companion object {
        fun toPrivateKey(base64: String): ByteArray {
            return Base64.getDecoder().decode(base64)
        }
    }

    override fun run() {
//        val file = path ?:
        // HOME
//        System.getenv().forEach { t, u -> println("$t:$u") }

        // user.home
//        System.getProperties().list(System.out)

        val file = if (path != null) Paths.get(path)
        else Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa")

        val code = Base64.getEncoder().encodeToString(Files.readAllBytes(file))
        println(code)
    }
}