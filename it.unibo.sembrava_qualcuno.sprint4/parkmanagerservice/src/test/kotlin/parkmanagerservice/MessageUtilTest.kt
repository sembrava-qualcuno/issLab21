package parkmanagerservice

import parkmanagerservice.utils.ApplMessageUtil
import org.junit.Assert
import org.junit.Test

class MessageUtilTest {

    @Test
    fun messageFromStringTest() {
        val messageString =
            "msg(enter,reply,clientservice,springcontroller,'{\"code\":1,\"message\":\"The indoor area or trolley are engaged\"}',16)"
        val message = ApplMessageUtil.messageFromString(messageString)

        Assert.assertEquals("enter", message.msgId)
        Assert.assertEquals("reply", message.msgType)
        Assert.assertEquals("clientservice", message.msgSender)
        Assert.assertEquals("springcontroller", message.msgReceiver)
        Assert.assertEquals("{\"code\":1,\"message\":\"The indoor area or trolley are engaged\"}", message.msgContent)
        Assert.assertEquals("16", message.msgNum)
    }
}
