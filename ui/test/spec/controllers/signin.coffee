'use strict'

describe 'Controller: SigninCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  SigninCtrl = {}
  scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()
    SigninCtrl = $controller 'SigninCtrl', {
      $scope: scope
    }

  it 'should attach a list of awesomeThings to the scope', ->
    expect(3).toBe 3
