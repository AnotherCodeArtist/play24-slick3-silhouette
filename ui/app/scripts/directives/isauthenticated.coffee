'use strict'

###*
 # @ngdoc directive
 # @name uiApp.directive:isAuthenticated
 # @description
 # # isAuthenticated
###
angular.module('uiApp')
  .directive('isAuthenticated', (identity) ->
    restrict: 'AC'
    link: ($scope, element, attrs) ->
      updateVisibility = ->
        element.css('display', if identity.isAuthenticated() then '' else 'none');
      $scope.$on "userChanged", ->
        updateVisibility()
      updateVisibility()
)
