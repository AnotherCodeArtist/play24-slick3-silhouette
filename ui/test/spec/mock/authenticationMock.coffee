authMock = angular.module "authMock", []

authMock.service "$auth", ($q,$rootScope) ->
  @expectedResults =
    login: []
    logout: []
  @expectLogout = (success = true) ->
    self = @
    respond: (result) ->
      self.expectedResults.logout.push success
  @logout = ->
    if @expectedResults.logout.length is 0
      throw new error "No logout expected"
    result =  @expectedResults.logout.shift()
    defer = $q.defer()
    if result then defer.resolve() else defer.reject()
    defer.promise
  @flush = ->
    $rootScope.$digest()
    if @expectedResults.logout.length > 0
      throw new Error "Not all expected calls arrived"
  return
