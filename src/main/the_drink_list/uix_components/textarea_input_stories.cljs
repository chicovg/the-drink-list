(ns the-drink-list.uix-components.textarea-input-stories
  (:require
   [the-drink-list.uix-components.textarea-input :as textarea-input]
   [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title     "UIX Components/Textarea Input"
       :component textarea-input/textarea-input})

(defn ^:export Default []
  (let [[value set-value!] (use-state nil)]
    ($ textarea-input/textarea-input {:id          :comment
                                      :label       "Comment"
                                      :placeholder "Enter a comment"
                                      :on-change   set-value!
                                      :value       value})))
