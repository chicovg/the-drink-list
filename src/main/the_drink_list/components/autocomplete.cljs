(ns the-drink-list.components.autocomplete
  (:require [clojure.string :as str]
            [reagent.core :as r]))

(defn autocomplete
  [_]
  (let [input-ref      (atom nil)
        show-dropdown? (r/atom false)
        active-option  (r/atom 0)]
    (fn [{:keys [id
                 label
                 on-change
                 placeholder
                 suggestions
                 value]}]
      (let [has-filter?          (pos? (count value))
            filtered-suggestions (when (and suggestions has-filter?)
                                   (filter #(str/starts-with? (str/lower-case %)
                                                              (str/lower-case value))
                                           suggestions))]
        [:div.field
         [:label.label {:for id} label]
         [:div.contol
          [:div.dropdown
           {:class (when (and has-filter?
                              @show-dropdown?)
                     "is-active")}
           [:div.drop-trigger.field.has-addons.mb-0
            [:p.control
             [:input.input {:auto-complete "off"
                            :id           id
                            :placeholder  placeholder
                            :on-blur      #(reset! show-dropdown? false)
                            :on-change    #(on-change
                                            (-> % .-target .-value))
                            :on-focus     #(reset! show-dropdown? true)
                            :on-key-down  #(let [key (.-keyCode %)]
                                             (condp = key
                                              ;; Enter
                                               13 (do
                                                    (on-change (nth filtered-suggestions @active-option))
                                                    (reset! active-option 0))
                                              ;; Up
                                               38 (when (pos? @active-option)
                                                    (swap! active-option dec))
                                              ;; Down
                                               40 (when (< @active-option (count filtered-suggestions))
                                                    (swap! active-option inc))
                                               nil))
                            :ref           #(reset! input-ref %)
                            :value         value}]]
            [:p.control
             [:button.button
              {:disabled (not value)
               :on-click #(do
                            (on-change nil)
                            (.focus @input-ref))}
              [:span.icon
               [:i.fas.fa-times]]]]]
           [:div.dropdown-menu
            {:role "menu"}
            (if (not-empty filtered-suggestions)
              [:ul.dropdown-content
               (doall
                (map-indexed
                 (fn [index suggestion]
                   ^{:key suggestion}
                   [:a.dropdown-item {:class         (when (= index @active-option)
                                                       "is-active")
                                      :on-click      #(do
                                                        (on-change suggestion)
                                                        (reset! show-dropdown? false))
                                      :on-mouse-down #(.preventDefault %)}
                    suggestion])
                 filtered-suggestions))]
              [:div.dropdown-content
               [:div.dropdown-item.has-text-grey.is-italic "No suggestions"]])]]]]))))
