'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:ListusersCtrl
 # @description
 # # ListusersCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'ListusersCtrl', ($scope,UserFactory,User,$modalBox) ->
    update = ->
      $scope.users = User.query()
      UserFactory.count()
      .then (response) ->
        $scope.count = response.data
    update()

    deleteAction = (id) ->
      User.remove(id: id,update)
    $scope.showConfirm = (user) ->
      boxOptions =
        boxType: 'confirm'
        afterConfirm: ->
          console.log "about to delete user #{user.id}"
          deleteAction(user.id)
        content: "Do you really want to delete user #{user.firstname}, #{user.lastname}?"
        confirmText: "Delete #{user.firstname}"
        title: "Attention!"
        effect: 'flip-swing-x'
        speed: 'normal'
      $modalBox(boxOptions)