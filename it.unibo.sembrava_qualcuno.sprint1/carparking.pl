%====================================================================================
% carparking description   
%====================================================================================
context(ctxparkmanagerservice, "localhost",  "TCP", "8023").
context(ctxtrolley, "localhost",  "TCP", "8024").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( clientservice, ctxparkmanagerservice, "it.unibo.clientservice.Clientservice").
  qactor( trolley, ctxtrolley, "it.unibo.trolley.Trolley").
