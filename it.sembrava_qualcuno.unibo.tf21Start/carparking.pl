%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8023").
 qactor( indoorparkingservicegui, ctxcarparking, "it.unibo.indoorparkingservicegui.Indoorparkingservicegui").
  qactor( outdoorparkingservicegui, ctxcarparking, "it.unibo.outdoorparkingservicegui.Outdoorparkingservicegui").
  qactor( parkclientservice, ctxcarparking, "it.unibo.parkclientservice.Parkclientservice").
  qactor( parkservicestatusgui, ctxcarparking, "it.unibo.parkservicestatusgui.Parkservicestatusgui").
  qactor( parkmanagerservice, ctxcarparking, "it.unibo.parkmanagerservice.Parkmanagerservice").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
