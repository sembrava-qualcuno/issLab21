/* Generated by AN DISI Unibo */ 
package it.unibo.trolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Trolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("trolley STARTS")
						updateResourceRep( "trolley STARTS"  
						)
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
				state("idle") { //this:State
					action { //it:State
						println("trolley IDLE")
						updateResourceRep( "trolley IDLE"  
						)
					}
					 transition(edgeName="t012",targetState="stopped",cond=whenEvent("stop"))
					transition(edgeName="t013",targetState="working",cond=whenDispatch("moveToPark"))
					transition(edgeName="t014",targetState="working",cond=whenDispatch("moveToOut"))
				}	 
				state("working") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("trolley WORKING")
						updateResourceRep( "trolley WORKING"  
						)
						delay(1000) 
						if( checkMsgContent( Term.createTerm("moveToPark(SLOTNUM)"), Term.createTerm("moveToPark(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var SLOTNUM = payloadArg(0).toInt()  
								println("trolley moveToPark($SLOTNUM)")
								updateResourceRep( "trolley moveToPark($SLOTNUM)"  
								)
								delay(1000) 
						}
						if( checkMsgContent( Term.createTerm("moveToOut(SLOTNUM)"), Term.createTerm("moveToOut(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var SLOTNUM = payloadArg(0).toInt()  
								println("trolley moveToOut($SLOTNUM)")
								updateResourceRep( "trolley moveToOut($SLOTNUM)"  
								)
						}
						forward("goToIdle", "goToIdle(X)" ,"trolley" ) 
					}
					 transition(edgeName="t015",targetState="stopped",cond=whenEvent("stop"))
					transition(edgeName="t016",targetState="idle",cond=whenDispatch("goToIdle"))
				}	 
				state("stopped") { //this:State
					action { //it:State
						println("trolley STOPPED")
						updateResourceRep( "trolley STOPPED"  
						)
					}
					 transition(edgeName="t017",targetState="working",cond=whenEvent("resume"))
				}	 
			}
		}
}
