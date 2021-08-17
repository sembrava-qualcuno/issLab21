var alertPlaceholder = document.getElementById('liveAlertPlaceholder')

function alert(message, type, dismissable=false, id="my-alert") {
    alertPlaceholder.innerHTML = alertPlaceholder.innerHTML + '<div id="' + id + '" class="alert alert-' + type + ' alert-dismissible fade show d-flex p-3 align-items-center" role="alert">' +
    message + ((dismissable) ? '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' : '') +
    '</div>'
}
  
function alertWithIcon(message, type, dismissable=false, id="my-alert") {
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
        alert(message, type, dismissable, id)
    else {
        var innerHTML = '<svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Info:"><use xlink:href="#' + icon + '"/></svg>' +
        '<div>' + message + "</div>"

        alert(innerHTML, type, dismissable, id)
    }
}

function errorAlert(message, dismissable=false, id="my-alert") {
    alertWithIcon('<h4 class="alert-heading">There was an error</h4>' + message, 'danger', dismissable, id)
}

function successAlert(message, dismissable=false, id="my-alert") {
    alertWithIcon(message, 'success', dismissable, id)
}

function warningAlert(message, dismissable=false, id="my-alert") {
    alertWithIcon(message, 'warning', dismissable, id)
}
