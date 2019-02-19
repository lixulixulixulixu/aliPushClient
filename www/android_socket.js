#!/usr/bin/env node

module.exports = function(context) {

    if (context.opts.platforms.indexOf('android') < 0) {
        return;
    }
  var fs = context.requireCordovaModule('fs'),
    path = context.requireCordovaModule('path');

  var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
  var platformRoot1 = path.join(context.opts.projectRoot, 'platforms/android/app/src/main');

  var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
  var manifestFile1 = path.join(platformRoot1, 'AndroidManifest.xml');
  if (fs.existsSync(manifestFile)) {
    fs.readFile(manifestFile, 'utf8', function (err,data) {
      if (err) {
        throw new Error('Unable to find AndroidManifest.xml: ' + err);
      }

      var appClass = 'cn.abrain.ldc.palm.MainApplication';

      var useclear=  'android:usesCleartextTraffic="true"';

        var useBackup = 'android:allowBackup="true"';
        var useClass= '';

        if (data.indexOf(useBackup) == -1) {
            useClass += ' ' + useBackup
        }

       if (data.indexOf(useclear) == -1) {
           useClass += ' ' + useclear
       }

      if (data.indexOf(appClass) == -1) {

        var result = data.replace(/<application/g, '<application android:name="' + appClass + '" ' +useClass);

        fs.writeFile(manifestFile, result, 'utf8', function (err) {
          if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
        })
      }
    });
  }
  if (fs.existsSync(manifestFile1)) {
      fs.readFile(manifestFile1, 'utf8', function (err,data) {
          if (err) {
              throw new Error('Unable to find AndroidManifest.xml: ' + err);
          }

          var appClass = 'cn.abrain.ldc.palm.MainApplication';
          var useclear=  'android:usesCleartextTraffic="true"';

          var useBackup = 'android:allowBackup="true"';
          var useClass= '';

          if (data.indexOf(useBackup) == -1) {
              useClass += ' ' + useBackup
          }

          if (data.indexOf(useclear) == -1) {
              useClass += ' ' + useclear
          }

          if (data.indexOf(appClass) == -1) {

              var result = data.replace(/<application/g, '<application android:name="' + appClass + '" '+ useClass);

              fs.writeFile(manifestFile1, result, 'utf8', function (err) {
                  if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
              })
          }
      });

  }


};