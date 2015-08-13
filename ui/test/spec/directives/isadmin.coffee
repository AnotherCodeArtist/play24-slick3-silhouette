'use strict'

describe 'Directive: isAdmin', ->

  # load the directive's module
  beforeEach module 'uiApp'

  scope = {}

  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()

  it 'should make hidden element visible', inject ($compile) ->
    element = angular.element '<is-admin>Fake</is-admin>'
    element = $compile(element) scope
    expect(element.text()).toBe 'Fake'
