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
class ClientServiceUnitTest {
		
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
				myactor = QakContext.getActor("clientservice")
				
 				while(myactor == null) {
					println("waiting for system startup ...")
					delay(500)
					myactor = QakContext.getActor("clientservice")
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
		if(testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}", "ctxcarparking", "clientservice", "8023")
		
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
 	 * Test that a client request to park a car (requenter) doesn't continue
 	 * if the INDOOR area is not free (enter reply give back SLOTNUM=0, no subsequent carenter request,
   	 * instead I expect a goToWork dispatch)
	 */
	@Test
	fun testIndoorOutdoorNotFree() {
		println("+++++++++ testIndoorOutdoorNotFree ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Test if the actor is ready to accept requests
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Occupy the INDOOR area 
			ParkingAreaKb.indoorfree = false
			
			//Build and send reqenter request
			var msg = MsgUtil.buildRequest("testIndoorNotFree", "reqenter", "requenter(test)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			//Test the expected behaviour
			assertEquals(channelForObserver.receive(), "clientservice reply to reqenter")
			assertEquals(channelForObserver.receive(), "clientservice reply enter(0)")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Occupy the OUTDOOR area 
			ParkingAreaKb.outdoorfree = false
			
			//Build and send requexit request
			msg = MsgUtil.buildRequest("testOutdoorNotFree", "reqexit", "reqexit(3)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to reqexit")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 0")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
		}
	}
	
/*
 	 * Test that a client request to park a car (requenter) doesn't continue if there are
 	 * no free parking slots available (enter reply give back SLOTNUM=0,
 	 * no subsequent carenter request, instead I expect a goToWork dispatch)
	 */
	@Test
	fun testNoFreeSlots() {
		println("+++++++++ testNoFreeSlots ")
		
		//Send a requenter request
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Test if the actor is ready to accept requests
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Occupy all the parking slots
			ParkingAreaKb.slotStateFree[4] = false
			
			//Build and send reqenter request
			var msg = MsgUtil.buildRequest("testNoFreeSlots", "reqenter", "requenter(test)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			//Test the expected behaviour
			assertEquals(channelForObserver.receive(), "clientservice reply to reqenter")
			assertEquals(channelForObserver.receive(), "clientservice reply enter(0)")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
		}
	}
	
	/*
 	 * Test that if the trolley is stopped (infer this using the Knowledge base),
 	 *  every request is discarded (enter reply give back SLOTNUM=0,
 	 * no subsequent carenter request, instead I expect a goToWork dispatch)
	 */
	@Test
	fun testTrolleyStopped() {
		println("+++++++++ testTrolleyStopped ")
		
		//Send a requenter request
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Test if the actor is ready to accept requests
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Occupy all the parking slots
			ParkingAreaKb.trolleyStopped = true
			
			//Build and send reqenter request
			var msg = MsgUtil.buildRequest("testTrolleyStopped", "reqenter", "requenter(test)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			//Test the expected behaviour
			assertEquals(channelForObserver.receive(), "clientservice reply to reqenter")
			assertEquals(channelForObserver.receive(), "clientservice reply enter(0)")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Build and send requexit request
			msg = MsgUtil.buildRequest("testOutdoorNotFree", "reqexit", "reqexit(3)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to reqexit")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 0")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
		}
	}
	
	/*
 	 * Test if the service correctly sends moveToPark message
 	 * to the trolley (via its CoAP-observable state)
	 */
	@Test
	fun testMoveToPark() {
		println("+++++++++ testMoveToPark ")
		
		//Send a requenter request
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Test if the actor is ready to accept requests
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Build and send reqenter request
			var msg = MsgUtil.buildRequest("testMoveToPark", "reqenter", "requenter(test)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "clientservice reply to reqenter")
			assertEquals(channelForObserver.receive(), "clientservice reply enter(5)")
			
			//Build and send carenter request
			msg = MsgUtil.buildRequest("testMoveToPark", "carenter", "carenter(5)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			//Test the expected behaviour
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to enterthecar")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 5")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			assertFalse(ParkingAreaKb.slotStateFree[4])
		}
	}
	
	/*
 	 * Test if the service correctly sends moveToOut message
 	 * to the trolley (via its CoAP-observable state)
	 */
	@Test
	fun testMoveToOut() {
		println("+++++++++ testMoveToOut ")
		
		//Send a requenter request
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Test if the actor is ready to accept requests
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Build and send requexit request
			var msg = MsgUtil.buildRequest("testMoveToOut", "reqexit", "reqexit(4)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to reqexit")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 4")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			assertTrue(ParkingAreaKb.slotStateFree[3])
			
			//Build and send wrong requexit request
			msg = MsgUtil.buildRequest("testMoveToOut", "reqexit", "reqexit(7)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to reqexit")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 0")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
			
			//Build and send another wrong requexit request
			msg = MsgUtil.buildRequest("testMoveToOut", "reqexit", "reqexit(5)", "clientservice")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "parkingclientservice reply to reqexit")
			assertEquals(channelForObserver.receive(), "parkingclientservice moves the car to SLOTNUM = 0")
			assertEquals(channelForObserver.receive(), "clientservice waiting ...")
		}
	}
	
	/*
 	 * In the tests it is also implicitely tested that if a goToWork dipatch arrives, the service goes to the work state
 	 * and that at the end of each tasks the service returns to the work state.
 	 * This is because at the end of each task tested, the service sends to itself a goToWork dispatch
	 */
 }
