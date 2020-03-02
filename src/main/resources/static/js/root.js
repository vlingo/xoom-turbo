var app = angular.module("myApp", ["ngRoute"]);
var pageId;
var windowService;
var locationService;
var scopeService;
app.config(function ($routeProvider) {
    $routeProvider.when("/:id", {
        templateUrl: "/static/partials/main.html",
        cache: false
    });
}).directive('loader', ['$routeParams', '$window', '$location', function ($routeParams, $window, $location) {
    return {
        link: function (scope, element, attr) {

            scope.domain = attr.domain;

            pageId = $routeParams.id;
            windowService = $window;
            locationService = $location;
            scopeService = scope;

            var loadGraph = function () {
                flowClient.get(pageId, function (data) {
                    if (data.statusCode != null) {
                    } else {
                        renderProcessorGraph(function () {
                            console.log("Loaded graph diagram...");
                        }, data)
                    }
                });
            };

            loadGraph();

            return attr;
        }
    };
}]);