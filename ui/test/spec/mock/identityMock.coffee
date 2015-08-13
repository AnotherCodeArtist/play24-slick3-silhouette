authMock = angular.module "identityMock", []

authMock.service "identity", ($q,$rootScope) ->
  @expectedResults =
    authenticated: []
  @expectIsAuthenticated =  ->
    self = @
    respond: (result) ->
      self.expectedResults.authenticated.push result
  @isAuthenticated = ->
    if @expectedResults.authenticated.length is 0
      throw new error "No call to isAuthenticated expected"
    result =  @expectedResults.authenticated.shift()
    result
    ###
    defer = $q.defer()
    defer.resolve(result)
    defer.promise
    ###
  @flush = ->
    $rootScope.$digest()
    if @expectedResults.logout.length > 0
      throw new Error "Not all expected calls arrived"
  return
