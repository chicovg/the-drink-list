(ns the-drink-list.uix-components.login-panel-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.uix-components.login-panel :as login-panel]))

(def ^:export default
  #js {:title     "Pages/Login Page"
       :component (r/reactify-component login-panel/login-panel)})

;; TODO
#_(defn ^:export Default []
  (r/as-element [login-panel/login-panel]))