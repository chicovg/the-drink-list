(ns the-drink-list.components.navbar-stories
  (:require
    [reagent.core :as r]
    [the-drink-list.components.navbar :as navbar]))

(def ^:export default
  #js {:title "Navbar Component"
       :component (r/reactify-component navbar/navbar)})

(defn ^:export Default []
  (r/as-element [navbar/navbar {:sign-out #(prn "sign-out")
                                :uid      "abc123"}]))

(defn ^:export LoggedOut []
  (r/as-element [navbar/navbar {}]))
