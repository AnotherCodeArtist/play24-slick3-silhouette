'use strict'

###*
 # @ngdoc service
 # @name uiApp.User
 # @description
 # # User
 # Factory in the uiApp.
###
angular.module('uiApp')
  .factory 'User', ($resource)->
    $resource("/user/:id/:page/:pageSize",{id: '@id'},{
      page:
        isArray: true
        params:
          id: 'pages'
        method: 'GET'
    })
