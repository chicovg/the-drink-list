(ns the-drink-list.uix-components.login-panel-stories
  (:require
   [the-drink-list.uix-components.login-panel :as login-panel]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Pages/Login Page"
       :component login-panel/login-panel})

(defn ^:export Default []
  ($ login-panel/login-panel))
