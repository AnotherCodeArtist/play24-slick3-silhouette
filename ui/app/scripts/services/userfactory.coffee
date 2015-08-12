'use strict'

###*
 # @ngdoc service
 # @name uiApp.UserFactory
 # @description
 # # UserFactory
 # Factory in the uiApp.
###
angular.module('uiApp')
  .factory 'UserFactory', ($http) ->
    get: -> $http.get('/whoami')
    count: -> $http.get('/user/count')
