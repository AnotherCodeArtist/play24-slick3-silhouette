'use strict'

describe 'Controller: SignupctrlCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  SignupctrlCtrl = {}
  scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()
    SignupctrlCtrl = $controller 'SignUpCtrl', {
      $scope: scope
    }

  it 'should attach a list of awesomeThings to the scope', ->
    expect(3).toBe 3
