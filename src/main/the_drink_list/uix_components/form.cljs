(ns the-drink-list.uix-components.form
  (:require
   [reagent.core :as r]))

(defn- event->value
  [e]
  (-> e .-target .-value))

(defn validate
  [required label value]
  (when (and required (empty? value))
    (str label " is required")))

(defn input
  [type element _]
  (r/with-let [dirty? (r/atom false)
               error  (r/atom nil)]
    (fn [{:keys [id label on-change required] :as props}]
      [:div.field
       [:label.label {:for id} label]
       [:div.control
        [element (assoc props
                        :type type
                        :on-change #(let [val (event->value %)]
                                      (reset! dirty? true)
                                      (reset! error (validate required label val))
                                      (on-change val)))]]
       (when (and @dirty? @error)
         [:p.help.is-danger @error])])))

(def text-input
  (partial input "text" :input.input))

(def textarea-input
  (partial input nil :textarea.textarea))
