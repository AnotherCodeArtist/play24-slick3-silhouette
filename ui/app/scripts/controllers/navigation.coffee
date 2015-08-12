'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:NavigationCtrl
 # @description
 # # NavigationCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'NavigationCtrl', ($scope,$auth,$rootScope) ->
    $scope.logout = -> $auth.logout().then ->
      $rootScope.user={}
      $rootScope.$broadcast "userChanged"
    $scope.isAuthenticated = -> $auth.isAuthenticated()
