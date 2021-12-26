(ns the-drink-list.components.select-input-stories
  (:require [the-drink-list.components.form :as form]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Select Input"
       :component (r/reactify-component form/select-input)})

(defn- select-input-with-value
  []
  (r/with-let [value (r/atom nil)]
    [form/select-input {:id          :type
                        :label       "Type"
                        :on-change   #(reset! value %)
                        :options     [{:value "Beer" :label "Beer"}
                                      {:value "Cider" :label "Cider"}
                                      {:value "Wine" :label "Wine"}]
                        :style       {:width 200}
                        :value       @value}]))

(defn ^:export Default []
  (r/as-element [select-input-with-value]))
