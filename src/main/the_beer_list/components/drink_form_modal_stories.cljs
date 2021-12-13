(ns the-beer-list.components.drink-form-modal-stories
  (:require [reagent.core :as r]
            [the-beer-list.components.drink-form-modal :as drink-form-modal]
            [the-beer-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title     "Drink Form Modal Component"
       :component (r/reactify-component drink-form-modal/modal)})

(defn ^:export Creating []
  (r/as-element [drink-form-modal/modal {:drink             {}
                                         :hide-drink-modal! #(prn "hiding modal")
                                         :save-drink!       #(prn "save drink" %)}]))

(defn ^:export Editing []
  (r/as-element [drink-form-modal/modal {:drink             (drink-type/gen-drink)
                                         :hide-drink-modal! #(prn "hiding modal")
                                         :save-drink!       #(prn "save drink" %)}]))
