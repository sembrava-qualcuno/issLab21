/* Generated by AN DISI Unibo */ 
package it.unibo.parkclientservice

import it.unibo.kactor.*
import alice.tuprolog.*
import it.unibo.sembrava_qualcuno.model.Message
import it.unibo.sembrava_qualcuno.sprint2.ParkingAreaKb
import it.unibo.utils.it.unibo.sembrava_qualcuno.sonar.SonarController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Parkclientservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
			val weightSensor : it.unibo.sembrava_qualcuno.weightsensor.WeightSensorInterface = it.unibo.sembrava_qualcuno.weightsensor.CoapWeightSensor("coap://localhost:8025/weightSensor")	 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("parkclientservice STARTS")
						updateResourceRep( "parkclientservice STARTS"  
						)
						  
									val inps = java.io.ObjectInputStream(java.io.FileInputStream("ServiceState.bin"))
									ParkingAreaKb.slot = inps.readObject() as MutableMap<Int, String>	
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("parkclientservice waiting ...")
						updateResourceRep( "parkclientservice waiting ..."  
						)
					}
					 transition(edgeName="t00",targetState="handleEnterRequest",cond=whenRequest("reqenter"))
					transition(edgeName="t01",targetState="handleOutRequest",cond=whenRequest("reqexit"))
				}	 
				state("handleEnterRequest") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("parkclientservice reply to reqenter")
						updateResourceRep( "parkclientservice reply to reqenter"  
						)
						 
						            lateinit var message : Message
						            var SLOTNUM = 0  
						if(  weightSensor.getWeight() == 0  
						 ){
						                for(i in 1..6) {
						                    if(ParkingAreaKb.slot.get(i).equals("")) {
						                        SLOTNUM = i
						                        break
						                    }
						                }
						if(  SLOTNUM == 0  
						 ){forward("goToWork", "goToWork(enter($SLOTNUM))" ,"parkclientservice" ) 
						}
						 message = Message(0, "$SLOTNUM")  
						}
						else
						 {forward("goToWork", "goToWork(enter($SLOTNUM))" ,"parkclientservice" ) 
						  message = Message(1, "The indoor area or trolley are engaged")  
						 }
						println("parkclientservice reply enter($SLOTNUM)")
						updateResourceRep( "$SLOTNUM"  
						)
						 val RESPONSE = Json.encodeToString(message)  
						answer("reqenter", "enter", "$RESPONSE"   )  
					}
					 transition(edgeName="t02",targetState="work",cond=whenDispatch("goToWork"))
					transition(edgeName="t03",targetState="enterthecar",cond=whenRequest("carenter"))
				}	 
				state("enterthecar") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("parkclientservice reply to carenter")
						updateResourceRep( "parkingclientservice reply to carenter"  
						)
						if( checkMsgContent( Term.createTerm("carenter(SLOTNUM)"), Term.createTerm("carenter(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var SLOTNUM = payloadArg(0).toInt()  
								if(  SLOTNUM in 1..6 && ParkingAreaKb.slot.get(SLOTNUM).equals("")  
								 ){if(  weightSensor.getWeight() > 0  
								 ){
														val sdf = java.text.SimpleDateFormat("dd/MM/yyyy-hh:mm:ss")
														val currentDate = sdf.format(java.util.Date())	
														val TOKENID = "$SLOTNUM-$currentDate"
								 val RESPONSE = Json.encodeToString(Message(0, "$TOKENID"))  
								answer("carenter", "receipt", "$RESPONSE"   )  
								forward("moveToIndoor", "moveToIndoor(indoor)" ,"trolley" ) 
								delay(2000) 
								println("parkclientservice moves the car to SLOTNUM = $SLOTNUM")
								updateResourceRep( "parkingclientservice moves the car to SLOTNUM = $SLOTNUM"  
								)
								forward("moveToPark", "moveToPark($SLOTNUM)" ,"trolley" ) 
								 ParkingAreaKb.slot.set(SLOTNUM, "$TOKENID")  
								
														val os = java.io.ObjectOutputStream( java.io.FileOutputStream("ServiceState.bin") )
														os.writeObject(ParkingAreaKb.slot)
														os.flush()
														os.close()
								}
								else
								 { val RESPONSE = Json.encodeToString(Message(2, "The indoor area is free"))  
								 answer("carenter", "receipt", "$RESPONSE"   )  
								 }
								}
								else
								 { val RESPONSE = Json.encodeToString(Message(3, "Invalid parking slot number"))  
								 answer("carenter", "receipt", "$RESPONSE"   )  
								 }
								println("parkclientservice reply")
								updateResourceRep( "parkclientservice reply"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleOutRequest") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("parkclientservice reply to reqexit")
						updateResourceRep( "parkingclientservice reply to reqexit"  
						)
						if( checkMsgContent( Term.createTerm("reqexit(TOKENID)"), Term.createTerm("reqexit(TOKENID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var TOKENID = payloadArg(0)  
								if(  SonarController.isOutdoorFree()
								 ){ 
													var SLOTNUM = 0
													ParkingAreaKb.slot.forEach { (k, v) ->
														if(v == TOKENID)
															SLOTNUM = k
													}
								if(  SLOTNUM != 0  
								 ){ val RESPONSE = Json.encodeToString(Message(0, "Success"))
								answer("reqenter", "exit", "$RESPONSE"   )  
								forward("moveToPark", "moveToPark($SLOTNUM)" ,"trolley" ) 
								delay(2000) 
								println("parkclientservice moves the car from SLOTNUM = $SLOTNUM")
								updateResourceRep( "parkclientservice moves the car from SLOTNUM = $SLOTNUM"  
								)
								forward("moveToOutdoor", "moveToOutdoor(outdoor)" ,"trolley" ) 
								 ParkingAreaKb.slot.set(SLOTNUM, "")  
								
														val os = java.io.ObjectOutputStream( java.io.FileOutputStream("ServiceState.bin") )
														os.writeObject(ParkingAreaKb.slot)
														os.flush()
														os.close()
								}
								else
								 { val RESPONSE = Json.encodeToString(Message(4, "Invalid tokenid"))  
								 answer("reqenter", "exit", "$RESPONSE"   )  
								 }
								}
								else
								 { val RESPONSE = Json.encodeToString(Message(5, "The outdoor area is engaged"))  
								 answer("reqenter", "exit", "$RESPONSE"   )  
								 }
								println("parkclientservice reply")
								updateResourceRep( "parkclientservice reply"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
