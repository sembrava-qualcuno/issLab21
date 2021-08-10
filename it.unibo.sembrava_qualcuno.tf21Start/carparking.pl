%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8022").
 qactor( indoorparkingservicegui, ctxcarparking, "it.unibo.indoorparkingservicegui.Indoorparkingservicegui").
  qactor( outdoorparkingservicegui, ctxcarparking, "it.unibo.outdoorparkingservicegui.Outdoorparkingservicegui").
  qactor( clientservice, ctxcarparking, "it.unibo.clientservice.Clientservice").
  qactor( parkservicestatusgui, ctxcarparking, "it.unibo.parkservicestatusgui.Parkservicestatusgui").
  qactor( managerservice, ctxcarparking, "it.unibo.managerservice.Managerservice").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
