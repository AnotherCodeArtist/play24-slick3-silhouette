authMock = angular.module "identityMock", []

authMock.service "identity", ($q,$rootScope) ->
  @expectedResults =
    authenticated: []
    isAdmin: []
  @expectIsAuthenticated =  ->
    self = @
    respond: (result) ->
      self.expectedResults.authenticated.push result
  @expectIsAdmin =  ->
    self = @
    respond: (result) ->
      self.expectedResults.isAdmin.push result
  @isAdmin = ->
    if @expectedResults.isAdmin.length is 0
      throw new error "No call to isAuthenticated expected"
    @expectedResults.isAdmin.shift()
  @isAuthenticated = ->
    if @expectedResults.authenticated.length is 0
      throw new error "No call to isAuthenticated expected"
    @expectedResults.authenticated.shift()
  @checkCalls = ->
    if @expectedResults.isAdmin.length + @expectedResults.authenticated.length is not 0
      throw new Error "Not all expected calls to identity have been made!"
  return
