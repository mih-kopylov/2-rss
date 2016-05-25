(function () {
    var app = angular.module("app", ["ngMaterial", "ngclipboard"]);

    app.config(["$mdThemingProvider", function ($mdThemingProvider) {
        var lightGreyPalette = $mdThemingProvider.extendPalette("grey", {});
        $mdThemingProvider.definePalette("light-grey", lightGreyPalette);
        $mdThemingProvider.theme('default')
            .primaryPalette("grey", {"default": "200"})
            .accentPalette("orange")
            .warnPalette("deep-orange")
            .backgroundPalette("grey", {"default": "50"});
    }]);

    var CONST = {
        HTTP: "http://",
        HTTPS: "https://"
    };
    var SOCIAL = {
        VK: {
            protocol: CONST.HTTP,
            url: "vk.com",
            name: "ВКонтакте",
            image: "/resources/images/vk.png",
            rssKey: "vk"
        },
        PIKABU: {
            protocol: CONST.HTTP,
            url: "pikabu.ru",
            name: "Пикабу",
            image: "/resources/images/pikabu.png",
            rssKey: "pikabu"
        },
        TWITTER: {
            protocol: CONST.HTTPS,
            url: "twitter.com",
            name: "Твиттер",
            image: "/resources/images/twitter.png",
            rssKey: "twitter"
        }
    };

    app.directive("app", function () {
        return {
            restrict: "E",
            scope: {},
            templateUrl: "/resources/html/app.html",
            controller: ["$scope", function ($scope) {
                $scope.name = "My App";
                $scope.removeUrlPrefix = function (url, prefix) {
                    var result = url;
                    if (result.startsWith(prefix)) {
                        result = result.substr(prefix.length);
                    }
                    return result;
                };
                $scope.getCleanUrl = function (url) {
                    var result = url;
                    result = $scope.removeUrlPrefix(result, CONST.HTTP);
                    result = $scope.removeUrlPrefix(result, CONST.HTTPS);
                    return result;
                };
                $scope.getSocial = function () {
                    var url = $scope.getCleanUrl($scope.socialUrl || "");
                    var result;
                    Object.keys(SOCIAL).forEach(function (key) {
                        if (url.startsWith(SOCIAL[key].url)) {
                            result = SOCIAL[key];
                            result.rssValue = $scope.removeUrlPrefix(url, SOCIAL[key].url);
                        }
                    });
                    return result;
                };
                $scope.$watch("socialUrl", function () {
                    $scope.social = $scope.getSocial();
                    $scope.rssFeed = "";
                    if ($scope.social) {
                        $scope.rssFeed = location.host + "/" + $scope.social.rssKey + $scope.social.rssValue;
                    }
                });
                $scope.socials = [SOCIAL.VK, SOCIAL.PIKABU, SOCIAL.TWITTER];
            }]
        }
    });

    app.directive("socialImage", function () {
        return {
            restrict: "E",
            scope: {
                value: "="
            },
            templateUrl: "/resources/html/socialImage.html"
        }
    })
})();