/* Generated by AN DISI Unibo */ 
package managerservice

import it.unibo.kactor.*
import alice.tuprolog.*
import fan.CoapFan
import fan.FanInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.californium.core.CoapClient
import parkmanagerservice.model.Message
import parkmanagerservice.model.ParkingArea
import parkmanagerservice.model.ParkingAreaKb
import sonar.CoapSonar
import sonar.SonarController
import thermometer.CoapThermometer
import thermometer.ThermometerController
import weightsensor.CoapWeightSensor
import weightsensor.WeightSensorInterface

class Managerservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
			
				val weightSensor : WeightSensorInterface = CoapWeightSensor("coap://localhost:8025/weightSensor")
		        val sonarController = SonarController(CoapSonar("coap://localhost:8026/sonar"), 60)
		        val trolleyResource : org.eclipse.californium.core.CoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")
		        val thermometerController = ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30)
		        val fanResource : FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("managerservice STARTS")
						updateResourceRep( "managerservice STARTS"  
						)
						
									managerservice.CoapManagerObserver("SonarController").start()
									managerservice.CoapManagerObserver("ThermometerController").start()
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("managerservice WAITING for requests")
						updateResourceRep( "managerservice WAITING for requests"  
						)
					}
					 transition(edgeName="t08",targetState="monitor",cond=whenRequest("getParkingArea"))
					transition(edgeName="t09",targetState="manage",cond=whenRequest("updateTrolley"))
				}	 
				state("manage") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("updateTrolley(ACTION)"), Term.createTerm("updateTrolley(ACTION)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var ACTION = payloadArg(0) 
											   lateinit var message : Message
											   lateinit var RESPONSE : String
								println("managerservice $ACTION the trolley")
								updateResourceRep( "managerservice $ACTION the trolley"  
								)
								if(
										ParkingAreaKb.highTemperature
								 ){
													when(ACTION){
														"stop" -> { 
															message = Message(0, "Success")
															RESPONSE = Json.encodeToString(message)
															
								forward("stop", "stop(X)" ,"trolley" ) 
								}
														"resume" -> {
															message = Message(0, "Success")
															RESPONSE = Json.encodeToString(message)
								forward("resume", "resume(X)" ,"trolley" ) 
								}
														else -> {
															println("action error")
															message = Message(8, "Bad Request")
															RESPONSE = Json.encodeToString(message)
														}
								}//end when 
								}
								else
								 { message = Message(8, "Bad Request")
								 				RESPONSE = Json.encodeToString(message)  
								 }
								answer("updateTrolley", "updateResult", "$RESPONSE"   )  
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("monitor") { //this:State
					action { //it:State
						println("managerservice GETTING parking area state")
						updateResourceRep( "managerservice GETTING parking area state"  
						)
						
									var fanState = fanResource.getState()
									var thermometerState = thermometerController.getTemperature()
									var trolleyState = trolleyResource.get().getResponseText()
									val RESPONSE = Json.encodeToString(	ParkingArea(fanState, thermometerState, trolleyState))
						answer("getParkingArea", "parkingAreaState", "$RESPONSE"   )  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
