modalBoxMock = angular.module "modalBoxMock", []

modalBoxMock.factory "$modalBox",  ->
  (boxoptions) -> boxoptions.afterConfirm()

