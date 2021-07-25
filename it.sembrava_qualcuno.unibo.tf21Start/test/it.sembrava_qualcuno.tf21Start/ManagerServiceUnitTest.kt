package it.sembrava_qualcuno.unibo.tf21Start

import org.junit.Assert.*
import java.net.UnknownHostException
import org.junit.BeforeClass
import cli.System.IO.IOException
import org.junit.Test
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import it.unibo.kactor.QakContext
import org.junit.Before
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import org.junit.AfterClass
import it.unibo.kactor.sysUtil
import it.unibo.kactor.ApplMessage
import org.junit.After
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import it.unibo.utils.ParkingAreaKb

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ManagerServiceUnitTest {
		
	companion object {
		var testingObserver : CoapObserverForTesting ? = null
		var systemStarted = false
		val channelSyncStart = Channel<String>()
		var myactor : ActorBasic? = null
		var counter = 1
		
		@JvmStatic
        @BeforeClass
		fun init() {
			GlobalScope.launch {
				it.unibo.ctxcarparking.main() //keep the control
			}
			GlobalScope.launch {
				myactor = QakContext.getActor("parkmanagerservice")
				
 				while(myactor == null) {
					println("waiting for system startup ...")
					delay(500)
					myactor = QakContext.getActor("parkmanagerservice")
				}				
				delay(2000)
				channelSyncStart.send("starttesting")
			}		 
		}
		
		@JvmStatic
	    @AfterClass
		fun terminate() {
			println("terminate the testing")
		}	
	}
	
	@Before
	fun checkSystemStarted()  {
		println("\n=================================================================== ") 
	    println("+++++++++ BEFOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE testingObserver=$testingObserver")
		if(!systemStarted) {
			runBlocking {
				channelSyncStart.receive()
				systemStarted = true
			    println("+++++++++ checkSystemStarted resumed ")
			}			
		} 
		if(testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}", "ctxcarparking", "parkmanagerservice", "8022")
		
		// Reset the knowledge base to assumption state 
		ParkingAreaKb.indoorfree = true
		ParkingAreaKb.outdoorfree = true
		ParkingAreaKb.slotStateFree = booleanArrayOf(false, false, false, false, true, false)
		ParkingAreaKb.trolleyStopped = false
  	}
	
	@After
	fun removeObs(){
		println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR  ${testingObserver!!.name}")
		
		testingObserver!!.terminate()
		testingObserver = null
		
		runBlocking{
			delay(1000)
		}
 	}
	
	//Test if the service correctly switches states after a toggle dispatch arrives (both from the CoAP-observable state and the Kwnoledge base)
	@Test
	fun testToggleTrolleyState() {
		println("+++++++++ testToggleTrolleyState ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
			
			var toggleState = MsgUtil.buildDispatch("managerserviceunittest", "toggleState", "toggleState(X)", "parkmanagerservice")
			
			MsgUtil.sendMsg(toggleState, myactor!!)
			assertEquals(channelForObserver.receive(), "parkmanagerservice emit stop")
			
			delay(500)
			
			MsgUtil.sendMsg(toggleState, myactor!!)
			assertEquals(channelForObserver.receive(), "parkmanagerservice emit resume")
		}
	}
}