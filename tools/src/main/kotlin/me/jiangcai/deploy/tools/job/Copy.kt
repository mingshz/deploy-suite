package me.jiangcai.deploy.tools.job

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.OVERWRITE
import com.jcraft.jsch.SftpException
import me.jiangcai.deploy.tools.Destination
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * @author CJ
 */
class Copy(private val destination: Destination?, private val from: String, private val to: String) : Runnable {
    override fun run() {
        if (destination == null) {
            // 本地复制
            val f = Paths.get(from)
            val t = Paths.get(to)

            f.toFile().copyRecursively(t.toFile(), true)
        } else {
            // 登录远程 destination 再实施复制
            val session = destination.createSession()
            try {

                val channel = session.openChannel("sftp") as ChannelSftp
                channel.connect()
                try {

                    val f = Paths.get(from)
                    val t = if (to.endsWith("/")) to.removeSuffix("/") else to
                    copyRecursivelyOverwrite(f, channel, t)


                } finally {
                    channel.disconnect()
                }

            } finally {
                session.disconnect()
            }
        }

    }

    private val checkedFolders = mutableSetOf<String>()

    private fun copyRecursivelyOverwrite(from: Path, channel: ChannelSftp, target: String) {
        // 检查目录是否存在 如果不存在 则建立目录
        if (Files.isDirectory(from)) {
            try {
                channel.ls(target)
            } catch (e: SftpException) {
                // 建立这个目录
                channel.mkdir(target)
            }

            // 迭代
            Files.list(from)
                .forEach {
                    copyRecursivelyOverwrite(it, channel, target + "/" + it.toFile().name)
                }
        } else if (Files.isRegularFile(from)) {
            // 检查上级目录是否存在。
            val parent = target.substring(0, target.lastIndexOf("/"))

            if (!checkedFolders.contains(parent)) {
                try {
                    channel.ls(parent)
                } catch (e: SftpException) {
                    // 建立这个目录
                    channel.mkdir(parent)
                }
                checkedFolders.add(parent)
            }

            Files.newInputStream(from)
                .use {
                    channel.put(it, target, OVERWRITE)
                }

        }
    }
}