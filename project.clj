(defproject the-beer-list "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [secretary "1.2.3"]
                 [breaking-point "0.1.2"]
                 [com.degel/re-frame-firebase "0.8.0"]
                 [day8.re-frame/test "0.1.5"]
                 [lein-doo "0.1.8"]
                 [devcards "0.2.4"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-sass "0.5.0"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :sass {:src "sass"
         :output-directory "resources/public/css"}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [day8.re-frame/re-frame-10x "0.4.0"]
                   [day8.re-frame/tracing "0.5.1"]
                   [figwheel-sidecar "0.5.18"]
                   [cider/piggieback "0.4.1"]]

    :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

    :plugins      [[lein-figwheel "0.5.18"]
                   [lein-doo "0.1.8"]]}

   :prod {:dependencies [[day8.re-frame/tracing-stubs "0.5.1"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "the-beer-list.core/mount-root"}
     :compiler     {:main                 the-beer-list.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true
                                           "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            the-beer-list.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:main          runners.doo
                    :output-dir    "resources/public/cljs/compiled/tests/out"
                    :output-to     "resources/public/cljs/tests/all-tests.js"
                    :optimizations :none}}
    {:id           "devcards-test"
     :source-paths ["src" "test"]
     :figwheel {:devcards true}
     :compiler {:main                 runners.tests
                :optimizations        :none
                :asset-path           "cljs/tests/out"
                :output-dir           "resources/public/cljs/tests/out"
                :output-to            "resources/public/cljs/tests/all-tests.js"
                :source-map-timestamp true}}]})
