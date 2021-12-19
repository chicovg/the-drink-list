(ns the-drink-list.components.drink-form-modal
  (:require [clojure.spec.alpha :as s]
            [the-drink-list.db :as db]
            [the-drink-list.components.form :as form]
            [the-drink-list.types.beer-flavors :as beer-flavors]
            [the-drink-list.types.drink :as drink-type]))

(s/def ::drink-form
  (s/keys :req-un [::drink-type/name
                   ::drink-type/maker
                   ::drink-type/type
                   ::drink-type/style
                   ::drink-type/appearance
                   ::drink-type/smell
                   ::drink-type/taste]))

(def drink-types
  (->> drink-type/types
       seq
       sort
       (map (fn [v]
              {:value v :label v}))))

;; TODO get this from previous values (i.e. passed in from state)
(def notes-options
  (-> beer-flavors/flavors
      seq
      sort
      vec))

(defn modal
  "A component which renders a modal for creating and editing a drink"
  [{drink :drink}]
  [:div.modal.is-active.is-clipped
   [:div.modal-background]
   [:div.modal-content
    [:div.card
     [:div.card-header
      [:p.card-header-title
       (if (:id drink)
         (str "Update " (:name drink))
         "Add a New Drink")]]
     [:div.form.p-4
      [form/text-input {:id          :name
                        :label       "Name"
                        :placeholder "Enter a drink name"
                        :on-change   #(db/set-drink-modal-drink-value! :name %)
                        :required    true
                        :value       (:name drink)}]
      [form/text-input {:id          :name
                        :label       "Maker"
                        :placeholder "Enter a maker name"
                        :on-change   #(db/set-drink-modal-drink-value! :maker %)
                        :required    true
                        :value       (:maker drink)}]
      [form/select-input {:id        :type
                          :label     "Type"
                          :on-change #(db/set-drink-modal-drink-value! :type %)
                          :options   drink-types
                          :required  true
                          :style     {:min-width 286}
                          :value     (:type drink)}]
          ;; TODO autocomplete from existing values
      [form/text-input {:id          :style
                        :label       "Style"
                        :placeholder "Enter the drink style"
                        :on-change   #(db/set-drink-modal-drink-value! :style %)
                        :required    true
                        :value       (:style drink)}]
      [:div.columns
       [:div.column.is-half
        [form/slider-input {:id        :appearance
                            :label     "Appearance"
                            :min       "1"
                            :max       "5"
                            :on-change #(db/set-drink-modal-drink-value! :appearance %)
                            :step      "0.1"
                            :value     (:appearance drink)}]]
       [:div.column.is-half
        [form/slider-input {:id        :smell
                            :label     "Smell"
                            :min       "1"
                            :max       "5"
                            :on-change #(db/set-drink-modal-drink-value! :smell %)
                            :step      "0.1"
                            :value     (:smell drink)}]]]
      [:div.columns
       [:div.column.is-half
        [form/slider-input {:id        :taste
                            :label     "Taste"
                            :min       "1"
                            :max       "5"
                            :on-change #(db/set-drink-modal-drink-value! :taste %)
                            :step      "0.1"
                            :value     (:taste drink)}]]
       [:div.column.is-half
        [:div.field
         [:label.label {:for :overall} "Overall"]
         [:p (drink-type/round (drink-type/calculate-overall drink))]]]]
      [:div.columns
       [:div.column.is-half
        [form/select-tags-input {:id          :notes
                                 :label       "Tasting Notes"
                                 :on-change   #(db/set-drink-modal-drink-value! :notes %)
                                 :options     @(db/drink-notes-options)
                                 :placeholder "Tasting notes"
                                 :style       {:min-width 286} ; TODO style tags like card view
                                 :value       (:notes drink)}]]
       [:div.column.is-half
        [form/textarea-input {:id          :comment
                              :label       "Comment"
                              :placeholder "Enter come comments about the drink"
                              :on-change   #(db/set-drink-modal-drink-value! :comment %)
                              :value       (:comment drink)}]]]
      [:div.field.is-grouped
       [:div.control
        [:button.button.is-primary
         {:disabled (not (s/valid? ::drink-form drink))
          :on-click #(db/save-drink! (dissoc drink :overall))
          :type     :button}
         "Save"]]
       [:div.control
        [:button.button.is-light
         {:on-click #(db/hide-drink-modal!)}
         "Cancel"]]]]]]
   [:button.modal-close.is-large
    {:aria-label "close"
     :on-click   #(db/hide-drink-modal!)}]])
