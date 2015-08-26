'use strict'

###*
 # @ngdoc directive
 # @name uiApp.directive:isAdmin
 # @description
 # # isAdmin
###
angular.module('uiApp')
  .directive('isAdmin', (identity) ->
    restrict: 'AC'
    link: ($scope, element, attrs) ->
      updateVisibility = ->
        element.css('display', if identity.isAdmin() then '' else 'none');
      $scope.$on "userChanged", ->
        updateVisibility()
      updateVisibility()
  )
