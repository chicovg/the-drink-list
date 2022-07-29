(ns the-drink-list.uix-components.drink-modal
  (:require
   [clojure.spec.alpha :as s]
   [the-drink-list.components.autocomplete :as autocomplete]
   [the-drink-list.components.form :as form]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [uix.core :refer [$ defui use-context]]
   [reagent.core :as r]))

(s/def ::drink-form
  (s/keys :req-un [::drink-type/name
                   ::drink-type/maker
                   ::drink-type/type
                   ::drink-type/style
                   ::drink-type/appearance
                   ::drink-type/smell
                   ::drink-type/taste]))

(defui drink-modal
  "A component which renders a modal for creating and editing a drink"
  []
  (let [{:keys [drink-modal-drink
                notes-options
                hide-drink-modal!
                makers
                styles
                types
                set-drink-modal-drink-value!]} (use-context context/app)]
    ($ :div.modal.is-active.is-clipped
       ($ :div.modal-background)
       ($ :div.modal-content
          ($ :div.card
             ($ :div.card-header
                ($ :p.card-header-title
                   (if (:id drink-modal-drink)
                     (str "Update " (:name drink-modal-drink))
                     "Add a New Drink")))
             ($ :div.form.p-4
                (r/as-element [form/text-input {:id          :name
                                                :label       "Name"
                                                :placeholder "Enter a drink name"
                                                :on-change   #(set-drink-modal-drink-value! :name %)
                                                :required    true
                                                :value       (:name drink-modal-drink)}])
                ($ :div.columns
                   ($ :div.column.is-half
                      (r/as-element [autocomplete/autocomplete {:id          :type
                                                                :label       "Type"
                                                                :placeholder "Enter a drink type"
                                                                :on-change   #(set-drink-modal-drink-value! :type %)
                                                                :suggestions types
                                                                :value       (:type drink-modal-drink)}]))
                   ($ :div.column.is-half
                      (r/as-element [autocomplete/autocomplete {:id          :maker
                                                                :label       "Maker"
                                                                :placeholder "Enter a maker name"
                                                                :on-change   #(set-drink-modal-drink-value! :maker %)
                                                                :suggestions makers
                                                                :value       (:maker drink-modal-drink)}])))
                ($ :div.columns
                   ($ :div.column.is-half
                      (r/as-element [autocomplete/autocomplete {:id          :style
                                                                :label       "Style"
                                                                :placeholder "Enter the drink style"
                                                                :on-change   #(set-drink-modal-drink-value! :style %)
                                                                :suggestions styles
                                                                :value       (:style drink-modal-drink)}])))
                ($ :div.columns
                   ($ :div.column.is-half
                      (r/as-element [form/slider-input {:id        :appearance
                                                        :label     "Appearance"
                                                        :min       "1"
                                                        :max       "5"
                                                        :on-change #(set-drink-modal-drink-value! :appearance %)
                                                        :step      "0.1"
                                                        :value     (:appearance drink-modal-drink)}]))
                   ($ :div.column.is-half
                      (r/as-element [form/slider-input {:id        :smell
                                                        :label     "Smell"
                                                        :min       "1"
                                                        :max       "5"
                                                        :on-change #(set-drink-modal-drink-value! :smell %)
                                                        :step      "0.1"
                                                        :value     (:smell drink-modal-drink)}])))
                ($ :div.columns
                   ($ :div.column.is-half
                      (r/as-element [form/slider-input {:id        :taste
                                                        :label     "Taste"
                                                        :min       "1"
                                                        :max       "5"
                                                        :on-change #(set-drink-modal-drink-value! :taste %)
                                                        :step      "0.1"
                                                        :value     (:taste drink-modal-drink)}]))

                   ($ :div.column.is-half
                      ($ :div.field
                         ($ :label.label {:for :overall} "Overall")
                         ($ :p (drink-type/round
                                (drink-type/calculate-overall drink-modal-drink))))))
                ($ :div.columns
                   ($ :div.column.is-half
                      (r/as-element [form/select-tags-input {:id          :notes
                                                             :label       "Tasting Notes"
                                                             :on-change   #(set-drink-modal-drink-value! :notes %)
                                                             :options     notes-options
                                                             :placeholder "Tasting notes"
                                                             :style       {:min-width 286} ; TODO style tags like card view
                                                             :value       (:notes drink-modal-drink)}]))

                   ($ :div.column.is-half
                      (r/as-element [form/textarea-input {:id          :comment
                                                          :label       "Comment"
                                                          :placeholder "Enter come comments about the drink"
                                                          :on-change   #(set-drink-modal-drink-value! :comment %)
                                                          :value       (:comment drink-modal-drink)}])))
                ($ :div.field.is-grouped
                   ($ :div.control
                      ($ :button.button.is-primary
                         {:disabled (not (s/valid? ::drink-form drink-modal-drink))
                          ;; TODO hook this up to firebase
                          :on-click #(js/console.log (dissoc drink-modal-drink :overall))
                          :type     :button}
                         "Save"))
                   ($ :div.control
                      ($ :button.button.is-light
                         {:on-click hide-drink-modal!}
                         "Cancel")))))))))
