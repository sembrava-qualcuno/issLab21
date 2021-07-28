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
class TrolleyUnitTest {
		
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
				myactor = QakContext.getActor("trolley")
				
 				while(myactor == null) {
					println("waiting for system startup ...")
					delay(500)
					myactor = QakContext.getActor("trolley")
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
	
	//Test if the trolley returns in idle state at the end of a task
	@Test
	fun testEndJob() {
		println("+++++++++ testEndJob ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			//Send a job
			var job = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			MsgUtil.sendMsg(job, myactor!!)
			
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			assertEquals(channelForObserver.receive(), "trolley moveToPark(1)")
			assertEquals(channelForObserver.receive(), "trolley IDLE")
		}
	}
	
	//Test the ability of the trolley to queue requests in case one arrives during the execution of a task
	@Test
	fun testMoreJobs() {
		println("+++++++++ testMoreJobs ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
				
			var job1 = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			var job2 = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToOut(2)", "trolley")
			
			//Send both jobs
			MsgUtil.sendMsg(job1, myactor!!)
			MsgUtil.sendMsg(job2, myactor!!)
			
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			assertEquals(channelForObserver.receive(), "trolley moveToPark(1)")
			assertEquals(channelForObserver.receive(), "trolley IDLE")
			
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			assertEquals(channelForObserver.receive(), "trolley moveToOut(2)")
			assertEquals(channelForObserver.receive(), "trolley IDLE")
		}
	}
	
	//Test if the trolley immediately goes to stopped state in case he is in working state and a stop event arrives.
	//Test also if, once a resume event arrives, it returns to the working state
	@Test
	fun testStopAndResumeWork() {
		println("+++++++++ testStopAndResumeWork ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
			
			var job = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			var stopEvent = MsgUtil.buildEvent("trolleyunittest", "stop", "stop(X)")
			var resumeEvent = MsgUtil.buildEvent("trolleyunittest", "resume", "resume(X)")
			
			MsgUtil.sendMsg(job, myactor!!)
			
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			MsgUtil.sendMsg(stopEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley STOPPED")
			
			delay(500)
			
			MsgUtil.sendMsg(resumeEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley WORKING")
		}
	}
	
	//Test if the trolley immediately goes to stopped state in case he is in idle state and a stop event arrives.
	//Test also if, once a resume event arrives, it returns to the idle state
	@Test
	fun testStopAndResumeIdle() {
		println("+++++++++ testStopAndResumeIdle ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)
			
			var stopEvent = MsgUtil.buildEvent("trolleyunittest", "stop", "stop(X)")
			var resumeEvent = MsgUtil.buildEvent("trolleyunittest", "resume", "resume(X)")
			
			assertEquals(channelForObserver.receive(), "trolley IDLE")
			MsgUtil.sendMsg(stopEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley STOPPED")
			
			delay(500)
			
			MsgUtil.sendMsg(resumeEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			assertEquals(channelForObserver.receive(), "trolley IDLE")
		}
	}
	
	//Test if once the trolley is in stopped state, whatever request arrives it continues to stay in the stopped state, unless a resume event arrives
	@Test
	fun testJobStopState() {
		println("+++++++++ testJobStopState ")
		
		runBlocking {
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver)
			
			var stopEvent = MsgUtil.buildEvent("trolleyunittest", "stop", "stop(X)")
			var job = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			var resumeEvent = MsgUtil.buildEvent("trolleyunittest", "resume", "resume(X)")
			
			MsgUtil.sendMsg(stopEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley STOPPED")
			
			MsgUtil.sendMsg(job, myactor!!)
			delay(500)
			assertEquals(channelForObserver.receive(), "trolley STOPPED")
			
			MsgUtil.sendMsg(resumeEvent, myactor!!)
			assertEquals(channelForObserver.receive(), "trolley WORKING")
		}
	}
 }