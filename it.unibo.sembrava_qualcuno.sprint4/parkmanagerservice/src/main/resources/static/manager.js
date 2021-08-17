var prefix = ""
var fanElement = document.getElementById('fanValuePlaceholder')
var temperatureElement = document.getElementById('temperatureValuePlaceholder')
var trolleyElement = document.getElementById('trolleyValuePlaceholder')

var temperatureThreshold = 30

setInterval(function() {
    parkingArea()
}, 1000);

function parkingArea() {
  let resStatus = 0
  var apiUrl = prefix + '/parkingArea';
  fetch(apiUrl).then(response => {
      resStatus = response.status
      return response.json()
  }).then(res => {
    switch (resStatus) {
      case 200:
        console.log('success')
        console.log(res)
        //Update the values
        fanElement.innerHTML = res.fan
        temperatureElement.innerHTML = res.temperature
        //trolleyElement.innerHTML = res.trolley

        var trolleyState
        if(res.trolley == "idle")
            trolleyState = "Idle"
        else if(res.trolley == "working")
            trolleyState = "Working"
            
        $('#trolleyToggle').bootstrapToggle('destroy')
        $('#trolleyToggle').bootstrapToggle({
            on: trolleyState,
            off: 'Stopped'
        });

        if(res.fan == "on") {
            if(document.getElementById('trolleyToggle').checked)
                $('#trolleyToggle').bootstrapToggle('enable')
            else
                $('#trolleyToggle').bootstrapToggle('disable')
        }
        else if (res.fan == "off") {
            if(document.getElementById('trolleyToggle').checked)
                $('#trolleyToggle').bootstrapToggle('disable')
            else
                $('#trolleyToggle').bootstrapToggle('enable')
        }
        break
      default:
        console.log('unhandled')
        break
    }
  })
  .catch(err => {
    console.error(err)
  })
}

$(function() {
    $('#trolleyToggle').change(function() {
        if($(this).prop('checked'))
            updateTrolleyState("resume")
        else
            updateTrolleyState("stop")

        $(this).bootstrapToggle('disable')
    })
})

function updateTrolleyState(state) {    
    var apiUrl = prefix + '/parkingArea/trolley';
    const data = { "state": state };

    fetch(apiUrl, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    }).then(response => response).then(data => {
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

//WEB-SOCKET
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/manager/temperature', function (message) {
            console.log(message)
            var myMessage = JSON.parse(message.body)

            if(myMessage.code == 1) {
                alertWithIcon(myMessage.message, 'warning', false, "temperatureAlert")
            }
            else if(myMessage.code == 2) {
                var myAlert = document.getElementById('temperatureAlert')
                var bsAlert = new bootstrap.Alert(myAlert)
                bsAlert.close()
            }
        });
        stompClient.subscribe('/manager/sonar', function (message) {
            console.log(message)
            warningAlert(JSON.parse(message.body).message, true)
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

connect()
