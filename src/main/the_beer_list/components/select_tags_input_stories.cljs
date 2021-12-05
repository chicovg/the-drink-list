(ns the-beer-list.components.select-tags-input-stories
  (:require [the-beer-list.components.form :as form]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Select Tags Input"
       :component (r/reactify-component form/select-tags-input)})

(defn- select-tags-input-with-value
  []
  (r/with-let [value (r/atom nil)]
    [form/select-tags-input {:id          :type
                             :label       "Type"
                             :on-change   #((prn %)
                                            (reset! value %))
                             :options     ["hoppy" "citrusy" "smoky" "toasty"]
                             :style       {:width 200}
                             :value       @value}]))

(defn ^:export Default []
  (r/as-element [select-tags-input-with-value]))
