'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:SigninCtrl
 # @description
 # # SigninCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'SigninCtrl', ($scope,$auth,$rootScope,$alert,UserFactory) ->
    $scope.signInInfo = {}
    $scope.singIn = ->
      $auth.setStorage($scope.signInInfo.rememberMe ? 'localStorage' : 'sessionStorage');
      $auth.login( email: $scope.signInInfo.email, password: $scope.signInInfo.password, rememberMe: $scope.signInInfo.rememberMe )
      .then ->
          UserFactory.get()
      .then (response) ->
        $rootScope.user = response.data
        $alert({
          content: 'You have successfully signed in',
          animation: 'fadeZoomFadeDown',
          type: 'material',
          duration: 3})
      .catch (response) ->
        console.log(response)
        $alert({
        content: response.data.message,
        animation: 'fadeZoomFadeDown',
        type: 'material',
        duration: 3})


