(ns the-drink-list.uix-components.slider-input-stories
  (:require
   [the-drink-list.uix-components.slider-input :as slider-input]
   [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title     "UIX Components/Slider Input"
       :component slider-input/slider-input})

(defn ^:export Default []
  (let [[value set-value!] (use-state 3)]
    ($ slider-input/slider-input {:id        :rating
                                  :min       "1"
                                  :max       "5"
                                  :label     "Rating"
                                  :on-change set-value!
                                  :step      "0.1"
                                  :value     value})))
