'use strict'

describe 'Controller: NavigationCtrl', ->

  # load the controller's module
  beforeEach ->
    module 'uiApp'
    module 'authMock'

  NavigationCtrl = {}
  $scope = {}
  $auth = {}
  $rootScope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, _$rootScope_, _$auth_) ->
    $rootScope = _$rootScope_
    $scope = $rootScope.$new()
    $auth = _$auth_
    NavigationCtrl = $controller 'NavigationCtrl', {
      $scope: $scope
    }

  it 'should support logout operation', ->
    $auth.expectLogout(true).respond()
    spyOn($rootScope, '$broadcast').and.callThrough()
    $scope.logout()
    $auth.flush()
    expect($rootScope.$broadcast).toHaveBeenCalledWith('userChanged')



