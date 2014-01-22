<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns:ng="http://angularjs.org">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/bootstrap-3.0.3/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/alertify/alertify.core.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/alertify/alertify.default.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/alertify/alertify.bootstrap.css" rel="stylesheet">

    <style type="text/css">
        .nav-tabs > li > a, button { outline: 0; }

        .audiojs { width: 26px;}
        .audiojs .play-pause { padding: 4px 0; }

        #internalWords .audiojs { float: left; margin-right: 0.5em; box-shadow: none; }

        .classtrue { color: #0d0; font-size: 1.19em; }
        .classfalse { color: red; font-size: 1.19em; }

        textarea.command-result { font: large monospace; color: #333; background: #fff; border: 1px solid #ccc; }
    </style>
</head>
<body>
<div style="font-size: x-small; margin-bottom: 1em;">This is a jsp file created at ${currentTime}.</div>
<div id="ng-app" ng-app="myApp" ng-controller="Controller">
<%--
    <button ng-click="testJAXRS()">testing jax-rs with ng-click</button>
    <button ng-click="testPOST()">testing jax-rs post</button>
    <button onclick="jQueryPOST()">post with jQuery</button>
--%>

    <ul class="nav nav-tabs">
        <li><span style="display: inline-block; width: 15px;"></span></li>
        <li class="active"><a href="#home" data-toggle="tab" target="_self">Home</a></li>
        <li ng-repeat="word in wordList">
            <a href="<c:out value='#'/>{{word.title | buildBootstrapTabId}}" data-toggle="tab" target="_self">
                {{word.title}}
            </a>
        </li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="home">
            <p ng-show="isLoadingSound">
                Please wait while we're downloading sounds ...
                <img src="${pageContext.request.contextPath}/loading.png" alt=""/>
            </p>
            <p>
                <a href="javascript:void(0)" ng-click="createTeachers()">create teachers</a><br/>
                <span ng-repeat="w in createTeachersResult">{{w}}&nbsp;</span>
            </p>
            <p>
                <div class="container">
                    <div class="row">
                        <div class="col-md-1">Command:</div>
                        <div class="col-md-8"><input type="text" ng-model="command" class="form-control"/></div>
                        <div class="col-md-1">
                            <button type="button" class="btn btn-default" ng-click="run()">Run</button>
                        </div>
                    </div>
                </div>
                <textarea ng-model="commandResult" readonly cols="130" rows="30" class="command-result"></textarea>
            </p>
        </div>
        <div ng-repeat="word in wordList" class="tab-pane" id="{{word.title | buildBootstrapTabId}}">
            <div class="container">
                <div class="row" ng-repeat="thisGroup in word.words" style="margin-top: 1em;">
                    <div class="col-md-6" ng-repeat="w in thisGroup">
                        <div class="row">
                            <div class="col-md-2" uri="{{w.uri}}"><%--<audio src="mp3 for this word"/>--%></div>
                            <div class="col-md-4">
                                <input type="text" ng-model="w.input" onclick="play(this)" class="form-control"/>
                            </div>
                            <div class="col-md-2">
                                <button type="button" class="btn btn-default" ng-click="check(w)">Check</button>
                            </div>
                            <div class="col-md-2" ng-class="'class' + w.answer">{{w.answer | checkmark}}</div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<div id="internalWords" style="position: relative;">
    <div style="clear: both;"></div>
    <div style="background: #fff; opacity: 1; position: absolute; top: 0; left: 0; width: 100%; height: 100%;"></div>
</div>

<script src="${pageContext.request.contextPath}/wordListMap.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-2.0.3.min.js"></script>
<script src="${pageContext.request.contextPath}/js/angular-1.2.9.min.js"></script>
<script src="${pageContext.request.contextPath}/js/angular-resource-1.2.9.min.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap-3.0.3/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/alertify/alertify.js"></script>
<%--<script src="${pageContext.request.contextPath}/bootstrap-3.0.3/js/respond.js"></script>--%>
<script src="${pageContext.request.contextPath}/audio/audio.min.js"></script>

<script type="text/javascript">
    var tryAudio, againAudio, correctAudios = {
        array: [],
        add: function(newSound) {
            this.array.push(newSound);
            return this;
        },
        play: function() {
            this.array[Math.floor(Math.random() * this.array.length)].click();
        }
    };

    function jQueryPOST() {
        $.post("${pageContext.request.contextPath}/resource/save-all-words", {
            a: ["a1", "a2"],
            b: ["b1", "b2", "b3"]
        }, function(data) {
            alert(JSON.stringify(data));
        })
    }

    // shuffle the words
    (function() {
        $.each(wordListMap, function(key, array) {
            var i = array.length, randomIndex, temp;
            while (i != 0) {
                randomIndex = Math.floor(Math.random() * i--);
                temp = array[randomIndex];
                array[randomIndex] = array[i];
                array[i] = temp;
            }
        });
    })();

    angular.module("myAppFilters", [])
            .filter("buildBootstrapTabId", function() {  // format a string so that it can be used as an element id
                return function(input) {
                    return input
                            .replace(/\s+|-+/g, "_")     // replace spaces or hyphens with underscores
                    ;
                };
            })
            .filter("checkmark", function() {            // convert a boolean to a checkmark
                return function(input) {
                    return input == null ? "" : input == true ? "\u2713" : "\u2718";
                };
            });
    var app = angular.module("myApp", ["ngResource", "myAppFilters"]);

    app.factory("testJaxRS", ["$resource", function($resource) {
        return $resource("${pageContext.request.contextPath}/resource/test-jaxrs/:something");
    }]);

    app.factory("testPOST", ["$resource", function($resource) {  // doesn't work
        return $resource("${pageContext.request.contextPath}/resource/word/save-all-words", null, {
            headers: {"Content-Type": "application/x-www-form-urlencoded"}
        });
    }]);

    app.factory("createTeachers", ["$resource", function($resource) {
        return $resource("${pageContext.request.contextPath}/resource/teacher/create", null, {
            get: {method: "GET", isArray: true}
        });
    }]);

    app.controller("Controller", ["$scope", "$resource", "$http", "createTeachers", "testJaxRS", "testPOST",
        function($scope, $resource, $http, createTeachers, testJaxRS, testPOST) {
            $scope.testJAXRS = function() {
                var temp = testJaxRS.get({something: "a thing 2"}, function() {
                    alert(JSON.stringify(temp));
                });
            };

            $scope.createTeachers = function() {
                alertify.log("createTeachers called", "", 3);
                $scope.createTeachersResult = createTeachers.get();
            };

            $scope.isLoadingSound = true;
            $scope.wordList = [];

            $http({
                method: "POST",
                url: "${pageContext.request.contextPath}/resource/word/save-all-words",
                data: $.param(wordListMap),
                headers: {"Content-Type": "application/x-www-form-urlencoded"}
            })
                    .success(function(data, status, headers, config) {
                        $.each(["try", "again", "correct", "lovely", "beautiful", "yes"], function(i, w) {
                            $("#internalWords").prepend("<audio src='${pageContext.request.contextPath}/sound/" + w +
                                    ".mp3'></audio>");
                        });

                        var errorMessages = [];
                        $.each(data, function(key, array) {
                            // words is an array of thisGroup which is an array of at most 2 elements so that we can
                            // display 2 words on a row in bootstrap
                            var words = [], thisGroup;
                            for (var i = 0; i < array.length; i++) {
                                if (!thisGroup) {
                                    thisGroup = [];
                                }

                                thisGroup.push({
                                    word: array[i].text,  // the word we need to display
                                    uri: array[i].uri,    // used to download mp3
                                    errorMessage: array[i].errorMessage ? array[i].errorMessage : null,
                                    input: "",            // to store the user input
                                    // null: user not enter any input, true: the userinput is correct, false: ...
                                    answer: null
                                });

                                if ((i + 1) % 2 == 0) {
                                    words.push(thisGroup);
                                    thisGroup = null;
                                }

                                if (array[i].errorMessage) {
                                    errorMessages.push(array[i].text + ": " + array[i].errorMessage);
                                }
                            }

                            if (thisGroup) {
                                words.push(thisGroup);
                            }

                            $scope.wordList.push({
                                title: key,          // used as the tab title
                                words: words
                            });
                        });
                        if (errorMessages.length > 0) {
                            alertify.error(errorMessages.join("<br/>"));
                        }

                        $scope.isLoadingSound = false;

                        // add <audio>
                        setTimeout(addAudios, 1094);
                    });

            $scope.check = function(word) {
                word.answer = word.input.toLowerCase().replace(/^\s+|\s+$/g, "") == word.word;
            };

            $scope.run = function() {
                $http({
                    method: "GET",
                    url: "${pageContext.request.contextPath}/resource/word/run-command",
                    params: {command: $scope.command}
                }).success(function(data) {
                    $.each(data, function(key, value) { $scope.commandResult = value; });
                });
            };
        }
    ]);

    function addAudios() {
        $("div.row > div[uri]").each(function() {
            $(this).append("<audio src='${pageContext.request.contextPath}" + $(this).attr("uri") +
                    "'/>");
        });
        audiojs.events.ready(function() {
            audiojs.createAll();
        });
    }

    function play(element) {
        $(element).closest("div.row").find("p.play").click();
    }
</script>
</body>
</html>