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
class TestPlan3 {
		
	companion object {
		var testingObserver : CoapObserverForTesting ? = null
		var systemStarted = false
		val channelSyncStart = Channel<String>()
		val actors : Array<String> = arrayOf("outdoorparkingservicegui", "parkclientservice", "trolley")
		var myactor : ActorBasic ? = null
		var counter = 1
		
		@JvmStatic
        @BeforeClass
		fun init() {
			GlobalScope.launch {
				it.unibo.ctxcarparking.main() //keep the control
			}
			GlobalScope.launch {
 				while(!actorsReady()) {
					println("waiting for system startup ...")
					delay(500)
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
		
		fun actorsReady() : Boolean {
			for(actor in actors) {
				if(QakContext.getActor(actor) == null)
					return false
			}
			return true
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
		if(testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}", "ctxcarparking", "trolley", "8022")
		
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
	
	/*
 	 * This test plan will work as an integration test for the OutdoorParkingServiceGUI, ParkClientService and Trolley components.
   	 * It will also work as a functional test for client "Car Pickup" use case (PORs acceptOUT and moveToSlotOut). 
	 */
	@Test
	fun testAcceptOut() {
		println("+++++++++ testAcceptOut ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
			
			
			var doAction = MsgUtil.buildDispatch("testplan3", "doAction", "doAction(1)", "outdoorparkingservicegui")

			MsgUtil.sendMsg(doAction, QakContext.getActor("outdoorparkingservicegui")!!)
			delay(500)
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			delay(500)
			assertEquals(channelForObserver.receive(), "trolley moveToOut(1)")
		}
	}
} 