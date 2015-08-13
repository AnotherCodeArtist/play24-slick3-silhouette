'use strict'

describe 'Controller: ListusersCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  $controller = $httpBackend = {}
  $scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject (_$controller_, $rootScope, _$httpBackend_) ->
    $scope = $rootScope.$new()
    $controller = _$controller_
    $httpBackend = _$httpBackend_


  it 'should fetch the list of users', ->
    $httpBackend.expectGET('/user').respond 200,[]
    $httpBackend.expectGET('/user/count').respond 200,0
    ListusersCtrl = $controller 'ListusersCtrl', $scope: $scope
    $httpBackend.flush()


