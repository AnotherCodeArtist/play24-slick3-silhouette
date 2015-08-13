'use strict'

###*
 # @ngdoc directive
 # @name uiApp.directive:mustMatch
 # @description
 # # mustMatch
###
angular.module('uiApp')
  .directive('mustMatch', ->
    require: 'ngModel'
    restrict: 'AC'
    scope:
      otherModelValue: "=mustMatch"
    link: (scope, element, attrs, ngModel) ->
      ngModel.$validators.mustMatch = (modelValue, viewValue) ->
        modelValue == scope.otherModelValue
      scope.$watch "otherModelValue", ->
        ngModel.$validate()
  )
