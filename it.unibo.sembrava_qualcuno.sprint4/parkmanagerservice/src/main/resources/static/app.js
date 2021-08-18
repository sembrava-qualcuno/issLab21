var prefix = ""
var alertPlaceholder = document.getElementById('liveAlertPlaceholder')
var timeoutValue = 60
var seconds
var reqenterTimer

function alert(message, type) {
  //var wrapper = document.createElement('div')
  alertPlaceholder.innerHTML = alertPlaceholder.innerHTML + '<div id="my-alert" class="alert alert-' + type + ' .alert-dismissible d-flex p-3 align-items-center" role="alert" style="display:inline-block;">' + message + '</div>'
  //alertPlaceholder.append(wrapper)
}

function alertWithIcon(message, type) {
  var icon
  
  switch(type) {
    case 'success':
      icon = 'check-circle-fill'
      break
    case 'danger':
    case 'warning':
      icon = 'exclamation-triangle-fill'
      break
    case 'primary':
      icon = 'info-fill'
      break
    default:
      icon = ''
  }

  if(icon == '')
    alert(message, type)
  else {
    var innerHTML = '<svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Info:"><use xlink:href="#' + icon + '"/></svg>' +
    '<div>' + message + "</div>"
  
    alert(innerHTML, type)
  }
}

function errorAlert(message) {
  alertWithIcon('<h4 class="alert-heading">There was an error</h4>' + message, 'danger')
}

function successAlert(message) {
  alertWithIcon(message, 'success')
}

function warningAlert(message) {
  alertWithIcon(message, 'warning')
}

function reqenter() {
  seconds = timeoutValue
  let resStatus = 0
  var apiUrl = prefix + '/client/reqenter';
  fetch(apiUrl).then(response => {
      resStatus = response.status
      return response.json()
  }).then(res => {
    switch (resStatus) {
      case 200:
        console.log('success')
        console.log(res)
        var btnToChange = document.getElementById("btnToChange")
        //TODO Make it better
        btnToChange.setAttribute('data-bs-dismiss', 'alert')
        btnToChange.setAttribute('data-bs-target', '#my-alert')
        if(res.parkingSlot == 0) {
          warningAlert("No parking slots available, try again later")
        }
        else {
          successAlert("Your parking slot is n. " + res.parkingSlot)
          btnToChange.innerHTML = "Car placed"
          btnToChange.onclick = function() { carenter(res.parkingSlot) }
          document.getElementById("returnDiv").setAttribute("style", "visibility:visible;")
          reqenterTimer = setInterval(function() {
            seconds--
            document.getElementById("countdown").innerHTML = seconds + "s ";
            if (seconds == 0) {
              clearInterval(reqenterTimer);
              window.location = '/client'
            }
          }, 1000);
        }
        break
      case 403:
        console.log('error')
        console.log(res)
        errorAlert(res.message)
        var btnToChange = document.getElementById("btnToChange")
        btnToChange.setAttribute('data-bs-dismiss', 'alert')
        btnToChange.setAttribute('data-bs-target', '#my-alert')
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

function carenter(slotnum) {
  let resStatus = 0
  var apiUrl = prefix + '/client/carenter?slotnum=' + slotnum;
  fetch(apiUrl).then(response => {
      resStatus = response.status
      return response.json()
  }).then(res => {
    switch (resStatus) {
      case 200:
        console.log('success')
        console.log(res)
        successAlert("Your TOKENID is: <b>" + res.tokenId + "</b><br><b>PRESERVE THIS TOKEN:</b> you will need it to collect your car<br>Have a good day!")
        var btnToChange = document.getElementById("btnToChange")
        btnToChange.innerHTML = "Return to Home Page"
        //TODO Make it also automatic
        btnToChange.onclick = function() { window.location = '/client' }
        clearInterval(reqenterTimer)
        document.getElementById("returnDiv").setAttribute("style", "visibility:visible;")
        seconds = timeoutValue
        var x = setInterval(function() {
          seconds--
          document.getElementById("countdown").innerHTML = seconds + "s ";
          if (seconds == 0) {
            clearInterval(x);
            window.location = '/client'
          }
        }, 1000);
        break
      case 403:
        console.log('error')
        console.log(res)
        errorAlert(res.message)
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

function reqexit() {
  seconds = timeoutValue
  let resStatus = 0
  var tokenid = document.getElementById("token").value
  if(tokenid == "") {
    warningAlert("Insert a token id")
    var btnToChange = document.getElementById("btnToChange")
    btnToChange.setAttribute('data-bs-dismiss', 'alert')
    btnToChange.setAttribute('data-bs-target', '#my-alert')
    return
  }
  var apiUrl = prefix + '/client/reqexit?tokenid=' + tokenid;
  fetch(apiUrl).then(response => {
      resStatus = response.status
      if(resStatus != 200)
        return response.json()
      else
        return response
  }).then(res => {
    switch (resStatus) {
      case 200:
        console.log('success')
        console.log(res)
        successAlert("The trolley will collect your car and bring it to you! Please stand by...<br><b>WHEN YOUR CAR ARRIVES, PLEASE LEAVE THE AREA IN ONE MINUTE:</b> <br> Thank you, and see you soon!", 'success')
        var btnToChange = document.getElementById("btnToChange")
        btnToChange.setAttribute('value', "Return to Home Page")
        btnToChange.onclick = function() { window.location = '/clientOut' }
        btnToChange.setAttribute('data-bs-dismiss', 'alert')
        btnToChange.setAttribute('data-bs-target', '#my-alert')
        document.getElementById("form").setAttribute("style", "display:none;")
        document.getElementById("returnDiv").setAttribute("style", "visibility:visible;")
        var x = setInterval(function() {
          seconds--
          document.getElementById("countdown").innerHTML = seconds + "s ";
          if (seconds == 0) {
            clearInterval(x);
            window.location = '/clientOut'
          }
        }, 1000);
        break
      case 400:
        console.log('error')
        console.log(res)
        warningAlert(res.message)
        var btnToChange = document.getElementById("btnToChange")
        btnToChange.setAttribute('data-bs-dismiss', 'alert')
        btnToChange.setAttribute('data-bs-target', '#my-alert')
        break
      case 403:
        console.log('error')
        console.log(res)
        errorAlert(res.message)
        var btnToChange = document.getElementById("btnToChange")
        btnToChange.setAttribute('data-bs-dismiss', 'alert')
        btnToChange.setAttribute('data-bs-target', '#my-alert')
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
