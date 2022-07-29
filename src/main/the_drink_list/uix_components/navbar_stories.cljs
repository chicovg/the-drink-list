(ns the-drink-list.uix-components.navbar-stories
  (:require
    [the-drink-list.uix-components.navbar :as navbar]
    [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title "UIX Components/Navbar"
       :component navbar/navbar})

(defn ^:export Default []
  (let [[user] (use-state {:uid "abc123"})]
    ($ navbar/navbar {:user user})))

(defn ^:export LoggedOut []
  ($ navbar/navbar {}))
