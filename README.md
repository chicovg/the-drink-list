# the-beer-list

[![Build Status](https://travis-ci.org/chicovg/the-beer-list.svg?branch=master)](https://travis-ci.org/chicovg/the-beer-list)

A [re-frame](https://github.com/Day8/re-frame) application which allows me to track my favorite brews.

## Development Mode

### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

### Install node dependencies

```
npm install
```

### Compile css:

Compile css file once.

```
lein sass once
```

Automatically recompile css file on change.

```
lein sass auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

#### Run with devcards

```
lein figwheel devcards
```

View the test results at http://localhost:2449/tests.html

#### Run with doo

Install karma and headless chrome

```
npm install -g karma-cli
npm install karma karma-cljs-test karma-chrome-launcher --save-dev
```

And then run your tests

```
lein clean
lein doo chrome-headless test once
```

Please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many JS environments (phantom, chrome, ie, safari, opera, slimer, node, rhino, or nashorn).

## Production Build

To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

To compile sass to css:

```
lein sass once
```

Deploy to firebase:

```
firebase deploy
```
