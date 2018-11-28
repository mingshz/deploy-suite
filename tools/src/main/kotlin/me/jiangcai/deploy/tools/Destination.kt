package me.jiangcai.deploy.tools

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import me.jiangcai.deploy.tools.job.PrintKey
import org.apache.commons.cli.MissingOptionException

/**
 * @author CJ
 */
class Destination(private val destination: String, private val privateKey: String) {

    private val fullDestinationRegex = Regex("(.+)@(.+):(\\d+)")
    private val destinationRegex = Regex("(.+)@(.+)")

    /**
     * @return 返回一次性的Session 记得用好关掉
     */
    fun createSession(): Session {
        val loader = JSch()
        loader.addIdentity("", PrintKey.toPrivateKey(privateKey), null, null)
        // 解析 destination

        val session = when {
            fullDestinationRegex.matches(destination) -> {
                val result = fullDestinationRegex.matchEntire(destination)!!
                loader.getSession(result.groupValues[1], result.groupValues[2], result.groupValues[3].toInt())
            }
            destinationRegex.matches(destination) -> {
                val result = destinationRegex.matchEntire(destination)!!
                loader.getSession(result.groupValues[1], result.groupValues[2])
            }
            else -> throw MissingOptionException("bad destination $destination")
        }

        session.setConfig("StrictHostKeyChecking", "no")

        session.connect()

        return session
    }

}