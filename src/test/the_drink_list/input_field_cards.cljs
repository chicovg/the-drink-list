(ns the-drink-list.input-field-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.types.drink :as drink]
   [the-drink-list.uix-components.autocomplete :as autocomplete]
   [uix.core :refer [$ defui use-state]]))

(declare autocomplete)

(defui autocomplete-story
  []
  (let [[value set-value!] (use-state nil)]
    ($ autocomplete/autocomplete {:id :makers
                                  :label "Brewery"
                                  :on-change set-value!
                                  :suggestions (vec drink/fake-brewers)
                                  :value value})))

(dc/defcard
  autocomplete
  ($ autocomplete-story))

(declare select-tags)

(dc/defcard
  select-tags
  "TODO")

(declare slider-input)

(dc/defcard
  slider-input
  "TODO")

(declare text-input)

(dc/defcard
  text-input
  "TODO")

(declare textarea-input)

(dc/defcard
  textarea-input
  "TODO")
