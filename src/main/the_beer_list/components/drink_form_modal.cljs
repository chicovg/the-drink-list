(ns the-beer-list.components.drink-form-modal
  (:require [clojure.spec.alpha :as s]
            [the-beer-list.components.form :as form]
            [the-beer-list.types.beer-flavors :as beer-flavors]
            [the-beer-list.types.drink :as drink-type]))

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
  [{drink                  :drink
    hide-drink-modal!      :hide-drink-modal!
    save-drink!            :save-drink!
    set-drink-modal-drink! :set-drink-modal-drink!}]
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
                        :on-change   #(set-drink-modal-drink! (assoc drink :name %))
                        :required    true
                        :value       (:name drink)}]
      [form/text-input {:id          :name
                        :label       "Maker"
                        :placeholder "Enter a maker name"
                        :on-change   #(set-drink-modal-drink! (assoc drink :maker %))
                        :required    true
                        :value       (:maker drink)}]
      [form/select-input {:id        :type
                          :label     "Type"
                          :on-change #(set-drink-modal-drink! (assoc drink :maker %))
                          :options   drink-types
                          :required  true
                          :style     {:min-width 286}
                          :value     (:type drink)}]
          ;; TODO autocomplete from existing values
      [form/text-input {:id          :style
                        :label       "Style"
                        :placeholder "Enter the drink style"
                        :on-change   #(set-drink-modal-drink! (assoc drink :style %))
                        :required    true
                        :value       (:style drink)}]
      [:div.columns
       [:div.column.is-half
        [form/slider-input {:id        :appearance
                            :label     "Appearance"
                            :min       "1"
                            :max       "5"
                            :on-change #(set-drink-modal-drink! (assoc drink :appearance %))
                            :step      "0.1"
                            :value     (:appearance drink)}]]
       [:div.column.is-half
        [form/slider-input {:id        :smell
                            :label     "Smell"
                            :min       "1"
                            :max       "5"
                            :on-change #(set-drink-modal-drink! (assoc drink :smell %))
                            :step      "0.1"
                            :value     (:smell drink)}]]]
      [:div.columns
       [:div.column.is-half
        [form/slider-input {:id        :taste
                            :label     "Taste"
                            :min       "1"
                            :max       "5"
                            :on-change #(set-drink-modal-drink! (assoc drink :taste %))
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
                                 :on-change   #(set-drink-modal-drink! (assoc drink :notes %))
                                 :options     notes-options
                                 :placeholder "Tasting notes"
                                 :style       {:min-width 286} ; TODO style tags like card view
                                 :value       (:notes drink)}]]
       [:div.column.is-half
        [form/textarea-input {:id          :comment
                              :label       "Comment"
                              :placeholder "Enter come comments about the drink"
                              :on-change   #(set-drink-modal-drink! (assoc drink :comment %))
                              :value       (:comment drink)}]]]
      [:div.field.is-grouped
       [:div.control
        [:button.button.is-primary
         {:disabled (not (s/valid? ::drink-form drink))
          :on-click (fn []
                      (save-drink!
                       (dissoc drink :overall)
                       #(hide-drink-modal!)
                           ;; TODO show an error here
                       #(prn %)))
          :type     :button}
         "Save"]]
       [:div.control
        [:button.button.is-light
         {:on-click #(hide-drink-modal!)}
         "Cancel"]]]]]]
   [:button.modal-close.is-large
    {:aria-label "close"
     :on-click   #(hide-drink-modal!)}]])
