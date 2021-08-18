%====================================================================================
% carparking description   
%====================================================================================
context(ctxparkmanagerservice, "localhost",  "TCP", "8023").
context(ctxtrolley, "localhost",  "TCP", "8024").
context(ctxbasicrobot, "basicrobot",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( clientservice, ctxparkmanagerservice, "clientservice.Clientservice").
  qactor( managerservice, ctxparkmanagerservice, "managerservice.Managerservice").
  qactor( trolley, ctxtrolley, "trolley.Trolley").
