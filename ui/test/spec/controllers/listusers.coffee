'use strict'

describe 'Controller: ListusersCtrl', ->

  # load the controller's module
  beforeEach ->
    module 'uiApp'
    module 'modalBoxMock'

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

  it 'should support a way to delete a user', ->
    $httpBackend.expectGET('/user').respond 200,[]
    $httpBackend.expectGET('/user/count').respond 200,0
    ListusersCtrl = $controller 'ListusersCtrl', $scope: $scope
    $httpBackend.flush()
    $httpBackend.expectDELETE('/user/88').respond 200
    $httpBackend.expectGET('/user').respond 200,[]
    $httpBackend.expectGET('/user/count').respond 200,0
    $scope.showConfirm(id:88)
    $httpBackend.flush()
