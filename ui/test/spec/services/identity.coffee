'use strict'

describe 'Service: identity', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  identity = {}
  beforeEach inject (_identity_) ->
    identity = _identity_

  it 'should do something', ->
    expect(!!identity).toBe true
