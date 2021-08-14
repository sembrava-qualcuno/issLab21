package parkmanagerservice.utils

import it.unibo.actor0.ApplMessage
import java.util.*

class ApplMessageUtil {
    companion object {
        fun messageFromString(message: String): ApplMessage {
            val st = StringTokenizer(message.substringAfter("msg(").substringBefore(")"), ",")
            val msgId = st.nextToken()
            val msgType = st.nextToken()
            val msgSender = st.nextToken()
            val msgReceiver = st.nextToken()
            st.nextToken("'")
            val msgContent = st.nextToken("'")
            st.nextToken(",")
            val msgNum = st.nextToken()

            return ApplMessage(msgId, msgType, msgSender, msgReceiver, msgContent, msgNum)
        }
    }
}
