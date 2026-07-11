const fs = require("fs");

function resolveFirefoxBin() {
  const platform = process.platform;
  const candidates = {
    darwin: [
      "/Applications/Firefox Nightly.app/Contents/MacOS/firefox",
      "/Applications/Firefox.app/Contents/MacOS/firefox",
    ],
    linux: [
      "/usr/bin/firefox-nightly",
      "/usr/bin/firefox",
      "/snap/bin/firefox",
    ],
    win32: [
      "C:/Program Files/Nightly/firefox.exe",
      "C:/Program Files/Firefox Nightly/firefox.exe",
      "C:/Program Files/Mozilla Firefox/firefox.exe",
      "C:/Program Files (x86)/Mozilla Firefox/firefox.exe",
    ],
  };

  for (const bin of candidates[platform] || []) {
    if (fs.existsSync(bin)) {
      return bin;
    }
  }

  return null;
}

if (!process.env.FIREFOX_BIN) {
  const resolved = resolveFirefoxBin();
  if (resolved) {
    process.env.FIREFOX_BIN = resolved;
  }
}

module.exports = function (config) {
  config.set({
    basePath: "",
    frameworks: ["jasmine", "@angular-devkit/build-angular"],
    plugins: [
      require("karma-jasmine"),
      require("karma-firefox-launcher"),
      require("karma-jasmine-html-reporter"),
      require("karma-coverage"),
      require("@angular-devkit/build-angular/plugins/karma"),
    ],
    client: {
      jasmine: {},
      clearContext: false,
    },
    jasmineHtmlReporter: {
      suppressAll: true,
    },
    coverageReporter: {
      dir: require("path").join(__dirname, "./coverage/angular-102"),
      subdir: ".",
      reporters: [{ type: "html" }, { type: "text-summary" }],
    },
    reporters: ["progress", "kjhtml"],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    customLaunchers: {
      FirefoxNightlyHeadless: {
        base: "FirefoxHeadless",
        flags: ["-headless"],
      },
    },
    browsers: ["FirefoxNightlyHeadless"],
    singleRun: false,
    restartOnFileChange: true,
  });
};
