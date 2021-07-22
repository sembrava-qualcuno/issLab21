%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8002").
 qactor( indoorparkingservicegui, ctxcarparking, "it.unibo.indoorparkingservicegui.Indoorparkingservicegui").
  qactor( outdoorparkingservicegui, ctxcarparking, "it.unibo.outdoorparkingservicegui.Outdoorparkingservicegui").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( parkingservicestatusgui, ctxcarparking, "it.unibo.parkingservicestatusgui.Parkingservicestatusgui").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
