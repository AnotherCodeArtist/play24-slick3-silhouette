'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:MainCtrl
 # @description
 # # MainCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'MainCtrl', ($scope) ->
    $scope.awesomeThings = [
      'HTML5 Boilerplate'
      'AngularJS'
      'Karma'
    ]
