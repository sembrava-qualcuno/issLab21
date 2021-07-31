package it.unibo.sembrava_qualcuno.sprint1.test

import itunibo.planner.model.RoomMap
import org.junit.Test
import itunibo.planner.plannerUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream
import kotlin.io.path.Path

class PlannerUtilTest {
	
	@Test
	fun plannerUtilTest() {
		var GoalX = "4"
		var GoalY = "0"

		println(FileInputStream("roomMap.bin"))

		plannerUtil.initAI()

		plannerUtil.loadRoomMap("roomMap")
		println("INITIAL MAP") 
 		
		plannerUtil.showMap()
		plannerUtil.planForGoal("${GoalX}","${GoalY}")

		
		var CurrentPlannedMove = plannerUtil.getNextPlannedMove()
		while(CurrentPlannedMove.length > 0) {
			runBlocking {
				delay(500)//simulate some real movement ...
			}
	  		plannerUtil.updateMap(CurrentPlannedMove, "resumablewalker: exec $CurrentPlannedMove")
			
			CurrentPlannedMove = plannerUtil.getNextPlannedMove()
		}
		
		println("&&&  resumablewalker POINT ${GoalX},${GoalY} REACHED")  
 		plannerUtil.showCurrentRobotState()
	}
}