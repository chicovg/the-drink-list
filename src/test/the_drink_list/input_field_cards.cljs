(ns the-drink-list.input-field-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.types.drink :as drink]
   [the-drink-list.uix-components.autocomplete :as autocomplete]
   [the-drink-list.uix-components.select-tags-input :as select-tags-input]
   [the-drink-list.uix-components.slider-input :as slider-input]
   [the-drink-list.uix-components.text-input :as text-input]
   [the-drink-list.uix-components.textarea-input :as textarea-input]
   [uix.core :refer [$ defui use-state]]))

(declare autocomplete)

(defui autocomplete-preview
  []
  (let [[value set-value!] (use-state nil)]
    ($ autocomplete/autocomplete {:id :makers
                                  :label "Brewery"
                                  :on-change set-value!
                                  :suggestions (vec drink/fake-brewers)
                                  :value value})))

(dc/defcard
  autocomplete
  ($ autocomplete-preview))

(defui select-tags-preview []
  (let [[value set-value!] (use-state nil)]
    ($ select-tags-input/select-tags-input {:id          :tags
                                            :label       "Tags"
                                            :on-change   set-value!
                                            :options     ["hoppy" "citrusy" "smoky" "toasty"]
                                            :placeholder "Add Tags"
                                            :style       {:width 200}
                                            :value       value})))

(declare select-tags)

(dc/defcard
  select-tags
  ($ select-tags-preview))

(defui slider-input-preview []
  (let [[value set-value!] (use-state 3)]
    ($ slider-input/slider-input {:id        :rating
                                  :min       "1"
                                  :max       "5"
                                  :label     "Rating"
                                  :on-change set-value!
                                  :step      "0.1"
                                  :value     value})))

(declare slider-input)

(dc/defcard
  slider-input
  ($ slider-input-preview))

(defui text-input-preview []
  (let [[value set-value!] (use-state nil)]
    ($ text-input/text-input {:id          :name
                              :label       "Name"
                              :placeholder "Enter a Name"
                              :on-change   set-value!
                              :required    true
                              :value       value})))

(declare text-input)

(dc/defcard
  text-input
  ($ text-input-preview))

(defui textarea-input-preview []
  (let [[value set-value!] (use-state nil)]
    ($ textarea-input/textarea-input {:id          :comment
                                      :label       "Comment"
                                      :placeholder "Enter a comment"
                                      :on-change   set-value!
                                      :value       value})))

(declare textarea-input)

(dc/defcard
  textarea-input
  ($ textarea-input-preview))
