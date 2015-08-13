'use strict'

describe 'Directive: isNotAuthenticated', ->

  # load the directive's module
  beforeEach module 'uiApp'

  scope = {}

  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()

  it 'should make hidden element visible', inject ($compile) ->
    element = angular.element '<is-not-authenticated></is-not-authenticated>'
    element = $compile(element) scope
    expect(element.text()).toBe 'this is the isNotAuthenticated directive'
