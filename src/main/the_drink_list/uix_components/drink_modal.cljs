(ns the-drink-list.uix-components.drink-modal
  (:require
   [clojure.spec.alpha :as s]
   [the-drink-list.api.firebase :as firebase]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.autocomplete :as autocomplete]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.select-tags-input :as select-tags-input]
   [the-drink-list.uix-components.slider-input :as slider-input]
   [the-drink-list.uix-components.text-input :as text-input]
   [the-drink-list.uix-components.textarea-input :as textarea-input]
   [uix.core :refer [$ defui use-context use-reducer]]))

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
  (let [{:keys [add-drink!
                drink-modal-drink
                hide-drink-modal!
                makers
                notes-options
                set-loading!
                styles
                types
                user]}                 (use-context context/app)
        [drink dispatch-field-change!] (use-reducer (fn [state {:keys [field value]}]
                                                      (assoc state field value))
                                                    drink-modal-drink)]
    ($ :div.modal.is-active.is-clipped
       ($ :div.modal-background)
       ($ :div.modal-content
          ($ :div.card
             ($ :div.card-header
                ($ :p.card-header-title
                   (if (:id drink)
                     (str "Update " (:name drink))
                     "Add a New Drink")))
             ($ :div.form.p-4
                ($ text-input/text-input {:id          :name
                                          :label       "Name"
                                          :placeholder "Enter a drink name"
                                          :on-change   #(dispatch-field-change! {:field :name :value %})
                                          :required    true
                                          :value       (:name drink)})
                ($ :div.columns
                   ($ :div.column.is-half
                      ($ autocomplete/autocomplete {:id          :type
                                                    :label       "Type"
                                                    :placeholder "Enter a drink type"
                                                    :on-change   #(dispatch-field-change! {:field :type :value %})
                                                    :suggestions types
                                                    :value       (:type drink)}))
                   ($ :div.column.is-half
                      ($ autocomplete/autocomplete {:id          :maker
                                                    :label       "Maker"
                                                    :placeholder "Enter a maker name"
                                                    :on-change   #(dispatch-field-change! {:field :maker :value %})
                                                    :suggestions makers
                                                    :value       (:maker drink)})))
                ($ :div.columns
                   ($ :div.column.is-half
                      ($ autocomplete/autocomplete {:id          :style
                                                    :label       "Style"
                                                    :placeholder "Enter the drink style"
                                                    :on-change   #(dispatch-field-change! {:field :style :value %})
                                                    :suggestions styles
                                                    :value       (:style drink)})))
                ($ :div.columns
                   ($ :div.column.is-half
                      ($ slider-input/slider-input {:id        :appearance
                                                    :label     "Appearance"
                                                    :min       "1"
                                                    :max       "5"
                                                    :on-change #(dispatch-field-change! {:field :appearance :value %})
                                                    :step      "0.1"
                                                    :value     (:appearance drink)}))
                   ($ :div.column.is-half
                      ($ slider-input/slider-input {:id        :smell
                                                    :label     "Smell"
                                                    :min       "1"
                                                    :max       "5"
                                                    :on-change #(dispatch-field-change! {:field :smell :value %})
                                                    :step      "0.1"
                                                    :value     (:smell drink)})))
                ($ :div.columns
                   ($ :div.column.is-half
                      ($ slider-input/slider-input {:id        :taste
                                                    :label     "Taste"
                                                    :min       "1"
                                                    :max       "5"
                                                    :on-change #(dispatch-field-change! {:field :taste :value %})
                                                    :step      "0.1"
                                                    :value     (:taste drink)}))

                   ($ :div.column.is-half
                      ($ :div.field
                         ($ :label.label {:for :overall} "Overall")
                         ($ :p (drink-type/round
                                (drink-type/calculate-overall drink))))))
                ($ :div.columns
                   ($ :div.column.is-half
                      ($ select-tags-input/select-tags-input {:id          :notes
                                                              :label       "Tasting Notes"
                                                              :on-change   #(dispatch-field-change! {:field :notes :value %})
                                                              :options     notes-options
                                                              :placeholder "Tasting notes"
                                                              :style       {:min-width 286}
                                                              :value       (:notes drink)}))

                   ($ :div.column.is-half
                      ($ textarea-input/textarea-input {:id          :comment
                                                        :label       "Comment"
                                                        :placeholder "Enter come comments about the drink"
                                                        :on-change   #(dispatch-field-change! {:field :comment :value %})
                                                        :value       (:comment drink)})))
                ($ :div.field.is-grouped
                   ($ :div.control
                      ($ :button.button.is-primary
                         {:disabled (not (s/valid? ::drink-form drink))
                          :on-click #(firebase/save-drink! {:user         user
                                                            :drink        (dissoc drink :overall)
                                                            :on-error     js/console.error
                                                            :on-success   (fn [drink]
                                                                            (add-drink! drink)
                                                                            (hide-drink-modal!))
                                                            :set-loading! set-loading!})
                          :type     :button}
                         "Save"))
                   ($ :div.control
                      ($ :button.button.is-light
                         {:on-click hide-drink-modal!}
                         "Cancel"))))))
       ($ :button.modal-close.is-large
          {:aria-label "close"
           :on-click hide-drink-modal!}))))
