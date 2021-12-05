(ns the-beer-list.components.drink-form-modal
  (:require [reagent.core :as r]
            [the-beer-list.components.common :as common]
            [the-beer-list.components.form :as form]))

(def drink-types
  (->> ["Beer" "Cider" "Mead" "Other" "Wine"]
       (map (fn [v]
              {:value v :label v}))))

(defn modal
  "A component which renders a modal for creating and editing a drink"
  [props]
  (r/with-let [form-values (r/atom props)]
    (fn [props]
      [:div.modal.is-active.is-clipped
       [:div.modal-background]
       [:div.modal-content
        [:div.card
         [:div.card-header
          [:p.card-header-title
           (if (:editing? props)
             (str "Update " (:name @form-values))
             "Add a New Drink")]]
         [:form.form.p-4
          [form/text-input {:id          :name
                            :label       "Name"
                            :placeholder "Enter a drink name"
                            :on-change   #(swap! form-values assoc :name %)
                            :required    true
                            :value       (:name @form-values)}]
          [form/text-input {:id          :name
                            :label       "Maker"
                            :placeholder "Enter a maker name"
                            :on-change   #(swap! form-values assoc :maker %)
                            :required    true
                            :value       (:maker @form-values)}]
          [form/select-input {:id        :type
                              :label     "Type"
                              :on-change #(swap! form-values assoc :type %)
                              :options   drink-types
                              :required  true
                              :style     {:min-width 286}
                              :value     (:type @form-values)}]
          ;; TODO autocomplete from existing values
          [form/text-input {:id          :style
                            :label       "Style"
                            :placeholder "Enter the drink style"
                            :on-change   #(swap! form-values assoc :style %)
                            :required    true
                            :value       (:style @form-values)}]
          [:div.columns
           [:div.column.is-half
            [form/slider-input {:id        :appearance
                                :label     "Appearance"
                                :min       "1"
                                :max       "5"
                                :on-change #(swap! form-values assoc :appearance %)
                                :step      "0.1"
                                :value     (:appearance @form-values)}]]
           [:div.column.is-half
            [form/slider-input {:id        :smell
                                :label     "Smell"
                                :min       "1"
                                :max       "5"
                                :on-change #(swap! form-values assoc :smell %)
                                :step      "0.1"
                                :value     (:smell @form-values)}]]]
          [:div.columns
           [:div.column.is-half
           [form/slider-input {:id        :taste
                               :label     "Taste"
                               :min       "1"
                               :max       "5"
                               :on-change #(swap! form-values assoc :taste %)
                               :step      "0.1"
                               :value     (:taste @form-values)}]]
           [:div.column.is-half
            [:div.field
             [:label.label {:for :overall} "Overall"]
             [:p (.toFixed (common/calc-overall @form-values) 1)]]]]
          [:div.columns
           [:div.column.is-half
            [form/select-tags-input {:id          :notes
                                     :label       "Tasting Notes"
                                     :on-change   #(swap! form-values assoc :notes %)
                                     :options     [] ;TODO prepopulate from previous values
                                     :placeholder "Enter some tasting notes"
                                     :style       {:min-width 286} ; TODO style tags like card view
                                     :value       (:notes @form-values)}]]
           [:div.column.is-half
            [form/textarea-input {:id          :comment
                                  :label       "Comment"
                                  :placeholder "Enter come comments about the drink"
                                  :on-change   #(swap! form-values assoc :comment %)
                                  :value       (:comment @form-values)}]]]]]]
       [:button.modal-close.is-large {:aria-label "close"}]])))
