function reqenter() {
    let resStatus = 0
    var apiUrl = '/client/reqenter';
    fetch(apiUrl).then(response => {
        resStatus = response.status
        return response.json()
    }).then(res => {
      switch (resStatus) {
        case 200:
          console.log('success')
          console.log(res)
          break
        case 403:
          console.log('error')
          console.log(res)
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
