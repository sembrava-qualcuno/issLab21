/* Generated by AN DISI Unibo */ 
package ctxparkmanagerservice
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream
import java.net.URLDecoder

fun main() = runBlocking {
	QakContext.createContexts(
	        "localhost", this, URLDecoder.decode(javaClass.classLoader.getResource("carparking.pl").path, "UTF-8"), URLDecoder.decode(javaClass.classLoader.getResource("sysRules.pl").path, "UTF-8")
	)
}
