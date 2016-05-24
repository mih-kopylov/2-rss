(function () {
    var app = angular.module("app", ["ngMaterial"]);

    app.config(["$mdThemingProvider", function ($mdThemingProvider) {
        var lightGreyPalette = $mdThemingProvider.extendPalette("grey", {

        });
        $mdThemingProvider.definePalette("light-grey", lightGreyPalette);
        $mdThemingProvider.theme('default')
            .primaryPalette("grey", {"default": "200"})
            .accentPalette("orange")
            .warnPalette("deep-orange")
            .backgroundPalette("grey", {"default": "50"});
    }]);

    app.directive("app", function () {
        return {
            restrict: "E",
            scope: {},
            templateUrl: "/resources/html/app.html",
            controller: ["$scope", function ($scope) {
                $scope.name = "My App"
            }]
        }
    });
})();