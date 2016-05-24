
(function(cordova) {
  var Reader = function() {

  };

  console.log('----------- Hexdigits Reader -----------');

  Reader.prototype.scannerStart = function(params, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerStart', [params]);
  };

  Reader.prototype.scannerPause = function(success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerPause', []);
  };

  Reader.prototype.scannerResume = function(success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerResume', []);
  };

  Reader.prototype.scannerStop = function(success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerStop', []);
  };

  Reader.prototype.scannerMove = function(params, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerMove', [params]);
  };

  Reader.prototype.scannerProfile = function(mode, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'scannerProfile', [mode]);
  };

  Reader.prototype.setScanAreaPercent = function(percent, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'setScanAreaPercent', [percent]);
  };

  Reader.prototype.getCameraDistance = function(success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'getCameraDistance', []);
  };

  Reader.prototype.setCameraDistance = function(distance, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'setCameraDistance', ["" + distance]);
  };

  Reader.prototype.switchCameraAutofocus = function(on, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'switchCameraAutofocus', [on]);
  };

  Reader.prototype.switchFlashlight = function(on, success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'switchFlashlight', [on]);
  };

  Reader.prototype.isOpen = function(success, fail) {
    return cordova.exec(function(args) {
      success(args);
    }, function(args) {
      fail(args);
    }, 'Reader', 'isOpen', []);
  };

  window.hexdigitsReader = new Reader();

  // backwards compatibility
  cordova.plugins = cordova.plugins || {};
  cordova.plugins.hexdigitsReader = window.hexdigitsReader;

})(window.PhoneGap || window.Cordova || window.cordova);
