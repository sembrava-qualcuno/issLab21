/* Generated by AN DISI Unibo */ 
package it.unibo.parkingservicegui

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Parkingservicegui ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var SLOTNUM = 0  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("parkingservicegui (client mock)  STARTS")
					}
					 transition( edgeName="goto",targetState="requestToenter", cond=doswitch() )
				}	 
				state("requestToenter") { //this:State
					action { //it:State
						println("client requestToenter")
						request("reqenter", "reqenter(bob)" ,"parkingmanagerservice" )  
						stateTimer = TimerActor("timer_requestToenter", 
							scope, context!!, "local_tout_parkingservicegui_requestToenter", 1000.toLong() )
					}
					 transition(edgeName="t00",targetState="retrylater",cond=whenTimeout("local_tout_parkingservicegui_requestToenter"))   
					transition(edgeName="t01",targetState="enterthecar",cond=whenReply("enter"))
				}	 
				state("enterthecar") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("enter(SLOTNUM)"), Term.createTerm("enter(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 SLOTNUM = payloadArg(0).toInt()  
								println("client receives SLOTNUM = $SLOTNUM")
						}
					}
					 transition( edgeName="goto",targetState="retrylater", cond=doswitchGuarded({ SLOTNUM == 0  
					}) )
					transition( edgeName="goto",targetState="movethecartoindoor", cond=doswitchGuarded({! ( SLOTNUM == 0  
					) }) )
				}	 
				state("movethecartoindoor") { //this:State
					action { //it:State
						println("client moving the car in the INDOOR and press CARENTER")
						request("carenter", "carenter($SLOTNUM)" ,"parkingmanagerservice" )  
					}
					 transition(edgeName="t02",targetState="afterreceipt",cond=whenReply("receipt"))
				}	 
				state("afterreceipt") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
					}
				}	 
				state("retrylater") { //this:State
					action { //it:State
						println("client will retry later")
						forward("clientLeave", "clientLeave(bob)" ,"parkingmanagerservice" ) 
					}
				}	 
			}
		}
}