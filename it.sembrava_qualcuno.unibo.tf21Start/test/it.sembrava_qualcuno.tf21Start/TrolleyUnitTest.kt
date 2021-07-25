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
import it.unibo.qak21.basicrobot.CoapObserverForTestingOld
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
	
	
	@Test
	@ExperimentalCoroutinesApi
	@ObsoleteCoroutinesApi
	fun testEndJob() {
		println("+++++++++ testEndJob ")
		
		//Send a job
		runBlocking {
			val channelForObserver = Channel<String>()
 			testingObserver!!.addObserver(channelForObserver)	 
			
			var msg = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			MsgUtil.sendMsg(msg, myactor!!)
			
			assertEquals(channelForObserver.receive(), "trolley WORKING")
			assertEquals(channelForObserver.receive(), "trolley moveToPark(1)")
			assertEquals(channelForObserver.receive(), "trolley IDLE")
		}
	}

	/*
	@Test
	fun testEndJobOld() {
		println("+++++++++ testEndJob ")
		
		//Send a job
		var result : String
		runBlocking {
 			val channelForObserver = Channel<String>()
 			testingObserverOld!!.addObserver(channelForObserver, "trolley WORKING")	 
		    val cmd = MsgUtil.buildDispatch("trolleyunittest", "moveToPark", "moveToPark(1)", "trolley")
			MsgUtil.sendMsg(cmd, myactor!!)
			result = channelForObserver.receive()
			println("+++++++++ testEndJob RESULT=$result")
			assertEquals(result, "trolley WORKING")
			
			//testingObserverOld!!.addObserver(channelForObserver, "trolley moveToPark")
			result = channelForObserver.receive()
			println("+++++++++ testEndJob RESULT=$result")
			assertEquals(result, "trolley moveToPark")
			
			//testingObserverOld!!.addObserver(channelForObserver, "trolley IDLE")
			result = channelForObserver.receive()
			println("+++++++++ testEndJob RESULT=$result")
			assertEquals(result, "trolley IDLE")
		}	
	}
	*/
	
 }