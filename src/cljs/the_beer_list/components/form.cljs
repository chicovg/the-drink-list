(ns the-beer-list.components.form
  (:require [reagent.core :as r]))

(defn- event->value
  [event]
  (-> event .-target .-value))

(defn- error-message
  [error]
  (when error
    [:p.help.is-danger (str label " " error)]))

(defn text-input
  [_]
  (r/with-let [touched? (r/atom false)]
    (fn [{:keys [error label name on-change] :as props}]
      [:div.field
       [:label.label {:for name} label]
       [:div.control
        [:input.input (assoc props
                             :type      "text"
                             :on-change #((reset! touched? true)
                                          (on-change (event->value %)))
                             :class     (when error "is-danger"))]
        [error-message error]]])))

(defn textarea-input
  [_]
  (r/with-let [touched? (r/atom false)]
    (fn [{:keys [error label name on-change] :as props}]
      [:div.field
       [:label.label {:for name} label]
       [:div.control
        [:textarea.textarea (assoc props
                                   :class (when error "is-danger")
                                   :rows 4
                                   :cols 200
                                   :max-length 800
                                   :on-change #((reset! touched? true)
                                                (on-change (event->value %))))]
        [error-message error]]])))

(defn select-input
  [_]
  (r/with-let [touched? (r/atom false)]
    (fn [{:keys [error label options]}]
      [:div.field
       [:label.label {:for name} label]
       [:div.control
        [:div.select
         [:select
          (for [{:keys [label value]} options]
            ^{:key value} [:option {:value value} label])]]
        [error-message error]]])))
