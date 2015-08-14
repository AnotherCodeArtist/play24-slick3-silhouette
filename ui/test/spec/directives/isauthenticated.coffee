'use strict'

describe 'Directive: isAuthenticated', ->

  # load the directive's module
  beforeEach ->
    module 'uiApp'
    module 'identityMock'

  $scope = $rootScope = identity = {}

  beforeEach inject ($controller, _$rootScope_, _identity_) ->
    $rootScope = _$rootScope_
    identity = _identity_
    $scope = $rootScope.$new()

  it 'should hide elements if not user is logged in', inject ($compile) ->
    identity.expectIsAuthenticated().respond(false)
    element = angular.element '<div is-authenticated>Only for Users</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe 'none'
    identity.checkCalls()

  it 'should make elements visible if user is logged in', inject ($compile) ->
    identity.expectIsAuthenticated().respond(true)
    element = angular.element '<div is-authenticated style="display:none">Only for Users</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe ''
    identity.checkCalls()


  it 'should react on userChanged events', inject ($compile) ->
    identity.expectIsAuthenticated().respond(false)
    element = angular.element '<div is-authenticated>Only for Users</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe 'none'
    identity.expectIsAuthenticated().respond(true)
    $rootScope.$broadcast("userChanged")
    $rootScope.$digest()
    expect(element.css('display')).toBe ''
    identity.checkCalls()
