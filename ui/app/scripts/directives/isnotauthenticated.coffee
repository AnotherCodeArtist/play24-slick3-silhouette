'use strict'

###*
 # @ngdoc directive
 # @name uiApp.directive:isNotAuthenticated
 # @description
 # # isNotAuthenticated
###
angular.module('uiApp')
  .directive 'isNotAuthenticated',  (identity) ->
    restrict: 'AC'
    link: ($scope, element, attrs) ->
      updateVisibility = ->
        element.css('display', if not identity.isAuthenticated() then '' else 'none');
      $scope.$on "userChanged", ->
        updateVisibility()
      updateVisibility()
