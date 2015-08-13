'use strict'

describe 'Service: User', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  User = {}
  beforeEach inject (_User_) ->
    User = _User_

  it 'should do something', ->
    expect(!!User).toBe true
