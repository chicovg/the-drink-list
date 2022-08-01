(ns the-drink-list.uix-components.autocomplete
  (:require
   [clojure.string :as str]
   [uix.core :refer [$ defui use-ref use-state]]))

(defui autocomplete
  [{:keys [id label on-change placeholder suggestions value]}]
  (let [input-ref             (use-ref)
        [show-dropdown?
         set-show-dropdown!]  (use-state false)
        [active-option
         set-active-option!]  (use-state 0)
        has-filter?           (pos? (count value))
        filtered-suggestions  (when (and suggestions has-filter?)
                                (filter #(str/starts-with? (str/lower-case %)
                                                           (str/lower-case value))
                                        suggestions))]
    ($ :div.field
       ($ :label.label {:for id} label)
       ($ :div.contol
          ($ :div.dropdown
             {:class (when (and has-filter? show-dropdown?)
                       "is-active")}
             ($ :div.drop-trigger.field.has-addons.mb-0
                ($ :p.control
                   ($ :input.input {:auto-complete "off"
                                    :id            id
                                    :placeholder   placeholder
                                    :on-blur       #(set-show-dropdown! false)
                                    :on-change     #(on-change (-> % .-target .-value))
                                    :on-focus      #(set-show-dropdown! true)
                                    :on-key-down   #(let [key (.-keyCode %)]
                                                      (condp = key
                                                        ;; Enter
                                                        13 (do
                                                             (on-change (nth filtered-suggestions active-option))
                                                             (set-active-option! 0))
                                                        ;; Up
                                                        38 (when (pos? active-option)
                                                             (set-active-option! (dec active-option)))
                                                        ;; Down
                                                        40 (when (< active-option (count filtered-suggestions))
                                                             (set-active-option! (inc active-option)))
                                                        nil))

                                    :value         value}))
                ($ :p.control
                   ($ :button.button
                      {:disabled (not value)
                       :on-click #(do
                                    (on-change nil)
                                    (when @input-ref
                                      (.focus @input-ref)))}
                      ($ :span.icon
                         ($ :i.fas.fa-times)))))
             ($ :div.dropdown-menu
                {:role "menu"}
                (if (not-empty filtered-suggestions)
                  ($ :ul.dropdown-content
                     (doall
                      (map-indexed
                       (fn [index suggestion]
                         ($ :a.dropdown-item {:key          suggestion
                                              :class        (when (= index active-option)
                                                              "is-active")
                                              :on-click     #(do
                                                               (on-change suggestion)
                                                               (set-show-dropdown! false))
                                              :on-mouse-down #(.preventDefault %)}
                            suggestion))
                       filtered-suggestions)))
                  ($ :div.dropdown-content
                     ($ :div.dropdown-item.has-text-grey.is-italic "No suggestions")))))))))

