/* Generated by AN DISI Unibo */ 
package it.unibo.clientservice

import it.unibo.kactor.*
import alice.tuprolog.*
import it.unibo.sembrava_qualcuno.model.Message
import it.unibo.sembrava_qualcuno.sprint1.ParkingAreaKb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Clientservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

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
						println("clientservice STARTS")
						updateResourceRep( "clientservice STARTS"  
						)
						  
									val inps = java.io.ObjectInputStream(java.io.FileInputStream("ServiceState.bin"))
									ParkingAreaKb.slot = inps.readObject() as MutableMap<Int, String>
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("clientservice waiting ...")
						updateResourceRep( "clientservice waiting ..."  
						)
					}
					 transition(edgeName="t00",targetState="handleEnterRequest",cond=whenRequest("reqenter"))
				}	 
				state("handleEnterRequest") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("clientservice reply to reqenter")
						updateResourceRep( "clientservice reply to reqenter"  
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
						 ){forward("goToWork", "goToWork(enter($SLOTNUM))" ,"clientservice" ) 
						}
						 message = Message(0, "$SLOTNUM")  
						}
						else
						 {forward("goToWork", "goToWork(enter($SLOTNUM))" ,"clientservice" ) 
						  message = Message(1, "The indoor area or trolley are engaged")  
						 }
						println("clientservice reply enter($SLOTNUM)")
						updateResourceRep( "$SLOTNUM"  
						)
						 val RESPONSE = Json.encodeToString(message)
						answer("reqenter", "enter", "$RESPONSE"   )  
					}
					 transition(edgeName="t01",targetState="work",cond=whenDispatch("goToWork"))
					transition(edgeName="t02",targetState="enterthecar",cond=whenRequest("carenter"))
				}	 
				state("enterthecar") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("clientservice reply to carenter")
						updateResourceRep( "parkingclientservice reply to carenter"  
						)
						if( checkMsgContent( Term.createTerm("carenter(SLOTNUM)"), Term.createTerm("carenter(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var SLOTNUM = payloadArg(0).toInt()  
								if(  SLOTNUM >= 1 && SLOTNUM <= 6 && ParkingAreaKb.slot.get(SLOTNUM).equals("")  
								 ){if(  weightSensor.getWeight() > 0  
								 ){
														val sdf = java.text.SimpleDateFormat("dd/MM/yyyy-hh:mm:ss")
														val currentDate = sdf.format(java.util.Date())	
														val TOKENID = "$SLOTNUM-$currentDate"
								 val RESPONSE = Json.encodeToString(Message(0, "$TOKENID"))  
								answer("carenter", "receipt", "$RESPONSE"   )  
								forward("moveToIndoor", "moveToIndoor(indoor)" ,"trolley" ) 
								delay(2000) 
								println("clientservice moves the car to SLOTNUM = $SLOTNUM")
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
								println("clientservice reply")
								updateResourceRep( "clientservice reply"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
