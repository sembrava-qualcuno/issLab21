/* Generated by AN DISI Unibo */ 
package it.unibo.parkmanagerservice

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.utils.ParkingAreaKb
	
class Parkmanagerservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("parkmanagerservice STARTS")
						updateResourceRep( "parkmanagerservice STARTS"  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("parkmanagerservice waiting ...")
						updateResourceRep( "parkmanagerservice waiting ..."  
						)
					}
					 transition(edgeName="t08",targetState="toggleTrolleyState",cond=whenDispatch("toggleState"))
				}	 
				state("toggleTrolleyState") { //this:State
					action { //it:State
						if(  ParkingAreaKb.trolleyStopped  
						 ){ ParkingAreaKb.trolleyStopped = false  
						println("parkmanagerservice emit resume")
						updateResourceRep( "parkmanagerservice emit resume"  
						)
						emit("resume", "resume(X)" ) 
						}
						else
						 { ParkingAreaKb.trolleyStopped = true  
						 println("parkmanagerservice emit stop")
						 updateResourceRep( "parkmanagerservice emit stop"  
						 )
						 emit("stop", "stop(X)" ) 
						 }
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
