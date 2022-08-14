(ns the-drink-list.uix-components.text-input
  (:require
   [uix.core :refer [$ defui use-state]]))

(defn validate
  [required label value]
  (when (and required (empty? value))
    (str label " is required")))

(defui text-input
  [{:keys [id label on-change required value] :as props}]
  (let [[dirty? set-dirty!] (use-state false)
        [error set-error!] (use-state nil)]
    ($ :div.field
       ($ :label.label {:for id} label)
       ($ :div.control
          ($ :input.input (assoc props
                                 :type      "text"
                                 :on-change #(let [val (-> % .-target .-value)]
                                               (set-dirty! true)
                                               (set-error! (validate required label val))
                                               (on-change val))
                                 :value     (or value ""))))
       (when (and dirty? error)
         ($ :p.help.is-danger error)))))
