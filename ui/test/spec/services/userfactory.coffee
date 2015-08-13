'use strict'

describe 'Service: UserFactory', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  UserFactory = {}
  beforeEach inject (_UserFactory_) ->
    UserFactory = _UserFactory_

  it 'should do something', ->
    expect(!!UserFactory).toBe true
