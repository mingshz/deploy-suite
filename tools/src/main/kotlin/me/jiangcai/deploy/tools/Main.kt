package me.jiangcai.deploy.tools

import me.jiangcai.deploy.tools.job.Copy
import me.jiangcai.deploy.tools.job.PrintKey
import org.apache.commons.cli.*
import kotlin.system.exitProcess

/**
 * @author CJ
 */
fun main(args: Array<String>) {

    val options = Options()

    options.addRequiredOption("type", null, true, "type ofr deploy, copy,printKey.. etc,")

    options.addOption("destination", true, "deploy destination, like: username@hostname[:port]")
    options.addOption("destination_privateKey", true, "base64 of private_key")
//    options.addOption("destination_password", true, "raw password")

    // copy
    options.addOption("from", true, "")
    options.addOption("to", true, "")

    // printKey
    options.addOption("key", true, "private key path.")

    val parser = DefaultParser()
    try {
        val cmd = parser.parse(options, args)

        val type = cmd.getOptionValue("type")
        when (type) {
            "printKey" -> {
                PrintKey(cmd.getOptionValue("key")).run()
            }
            "copy" -> {
                val from = cmd.getOptionValue("from")
                val to = cmd.getOptionValue("to")
                if (from.isNullOrEmpty() || to.isNullOrEmpty())
                    throw MissingOptionException("")

                Copy(toDestination(cmd), from, to)
                    .run()
            }
            else -> throw MissingOptionException("")
        }


    } catch (e: MissingOptionException) {
        val formatter = HelpFormatter()
        formatter.printHelp("exec", options)
        exitProcess(2)
    }

}

fun toDestination(cmd: CommandLine): Destination? {
    val destination = cmd.getOptionValue("destination")
    if (destination.isNullOrEmpty())
        return null
    val privateKey = cmd.getOptionValue("destination_privateKey")
    if (privateKey.isNullOrEmpty())
        throw MissingOptionException("")
//    val password = cmd.getOptionValue("destination_password")
    return Destination(destination,privateKey)
}
