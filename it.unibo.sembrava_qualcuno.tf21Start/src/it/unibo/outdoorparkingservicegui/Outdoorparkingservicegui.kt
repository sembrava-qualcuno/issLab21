/* Generated by AN DISI Unibo */ 
package it.unibo.outdoorparkingservicegui

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Outdoorparkingservicegui ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("outdoorparkingservicegui STARTS")
						updateResourceRep( "outdoorparkingservicegui STARTS"  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("outdoorparkingservicegui waiting for commands ...")
						updateResourceRep( "outdoorparkingservicegui waiting for commands ..."  
						)
					}
					 transition(edgeName="t04",targetState="requestToExit",cond=whenDispatch("doAction"))
				}	 
				state("requestToExit") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("doAction(X)"), Term.createTerm("doAction(TOKENID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var TOKENID = payloadArg(0).toInt()  
								println("outdoorparkingservicegui requestToExit($TOKENID)")
								updateResourceRep( "outdoorparkingservicegui requestToExit($TOKENID)"  
								)
								request("reqexit", "reqexit($TOKENID)" ,"clientservice" )  
						}
					}
					 transition(edgeName="t05",targetState="afterreceipt",cond=whenReply("exit"))
				}	 
				state("afterreceipt") { //this:State
					action { //it:State
						println("outdoorparkingservicegui leaves")
						updateResourceRep( "outdoorparkingservicegui leaves"  
						)
					}
				}	 
			}
		}
}
