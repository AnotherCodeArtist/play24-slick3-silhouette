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
        $rootScope.$broadcast "userChanged"
        $alert({
          titel: "Success"
          content: 'You have successfully signed in'
          effect: 'fade-in'
          speed: 'normal'
          alertType:'success'
          placement: 'top-right'
          duration: 5000})
      .catch (response) ->
        console.log(response)
        $alert({
        content: response.data.message or response.data
        effect: 'fade-in'
        speed: 'normal'
        alertType:'danger'
        duration: 5000})


