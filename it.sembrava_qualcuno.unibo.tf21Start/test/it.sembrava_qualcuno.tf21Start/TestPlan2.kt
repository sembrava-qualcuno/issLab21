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
class TestPlan2 {
		
	companion object {
		var testingObserver : CoapObserverForTesting ? = null
		var systemStarted = false
		val channelSyncStart = Channel<String>()
		val actors : Array<String> = arrayOf("indoorparkingservicegui", "parkclientservice", "trolley")
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
		if(testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}", "ctxcarparking", "trolley", "8023")
		
		// Reset the knowledge base to the assumption state
		ParkingAreaKb.indoorfree = true
		ParkingAreaKb.slotStateFree = booleanArrayOf(false, false, false, false, true, false)
		ParkingAreaKb.trolleyStopped = false
		ParkingAreaKb.outdoorfree = true
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
  	 *	This test will work as an integration/functional test for the "Car Parking" client use case
	 */
	@Test
	fun testCarParking() {
		println("+++++++++ testCarParking ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
			
			assertEquals(channelForObserver.receive(), "trolley IDLE")
			var doAction = MsgUtil.buildDispatch("testCarParking", "doAction", "doAction(X)", "indoorparkingservicegui")
			
			MsgUtil.sendMsg(doAction, QakContext.getActor("indoorparkingservicegui")!!)
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			delay(1000)
			assertEquals(channelForObserver.receive(), "trolley moveToPark(5)")
		}
	}
} 