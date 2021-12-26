(ns the-drink-list.components.login-panel-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.login-panel :as login-panel]))

(def ^:export default
  #js {:title     "Login Panel Component"
       :component (r/reactify-component login-panel/login-panel)})

(defn ^:export Default []
  (r/as-element [login-panel/login-panel]))
