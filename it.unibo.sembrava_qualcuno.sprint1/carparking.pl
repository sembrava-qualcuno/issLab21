%====================================================================================
% carparking description   
%====================================================================================
context(ctxparkmanagerservice, "localhost",  "TCP", "8023").
context(ctxtrolley, "localhost",  "TCP", "8024").
 qactor( parkclientservice, ctxparkmanagerservice, "it.unibo.parkclientservice.Parkclientservice").
  qactor( trolley, ctxtrolley, "it.unibo.trolley.Trolley").
