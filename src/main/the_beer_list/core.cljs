(ns the-beer-list.core
  (:require [reagent.dom :as dom]
            [the-beer-list.components.header :as header]))

(defn init []
  (dom/render [header/header "Hello, World!"]
              (js/document.getElementById "app")))
