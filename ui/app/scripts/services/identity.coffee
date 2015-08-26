'use strict'

###*
 # @ngdoc service
 # @name uiApp.identity
 # @description
 # # identity
 # Factory in the uiApp.
###
angular.module('uiApp')
  .factory 'identity', ($auth,$rootScope,UserFactory)->
    adminRole = "ADMINISTRATOR"
    isAuthenticated: ->
      ok = $auth.isAuthenticated()
      if ok and not $rootScope.user.firstname?
        UserFactory.get().then (response) ->
          $rootScope.user=response.data
          $rootScope.$broadcast "userChanged"
      ok
    isAdmin: -> $auth.isAuthenticated() and $rootScope.user.roles? and adminRole in $rootScope.user.roles
