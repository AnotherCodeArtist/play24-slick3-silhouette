'use strict'

describe 'Directive: isAdmin', ->

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
    identity.expectIsAdmin().respond(false)
    element = angular.element '<div is-admin>Only for Admins</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe 'none'

  it 'should make elements visible if user is logged in', inject ($compile) ->
    identity.expectIsAdmin().respond(true)
    element = angular.element '<div is-admin style="display:none">Only for Admins</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe ''

  it 'should react on userChanged events', inject ($compile) ->
    identity.expectIsAdmin().respond(false)
    element = angular.element '<div is-admin>Only for Admins</div>'
    element = $compile(element) $scope
    expect(element.css('display')).toBe 'none'
    identity.expectIsAdmin().respond(true)
    $rootScope.$broadcast("userChanged")
    $rootScope.$digest()
    expect(element.css('display')).toBe ''
    identity.checkCalls()
