(ns the-drink-list.components.header-stories
  (:require [the-drink-list.components.header :refer [header]]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Header Component"
       :component (r/reactify-component header)})

(defn ^:export HelloWorldHeader []
  (r/as-element [header "Hello, World!"]))
