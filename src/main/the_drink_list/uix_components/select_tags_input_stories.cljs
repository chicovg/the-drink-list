(ns the-drink-list.uix-components.select-tags-input-stories
  (:require
   [the-drink-list.uix-components.select-tags-input :as select-tags-input]
   [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title     "UIX Components/Select Tags Input"
       :component select-tags-input/select-tags-input})

(defn ^:export Default []
  (let [[value set-value!] (use-state nil)]
    ($ select-tags-input/select-tags-input {:id          :tags
                                            :label       "Tags"
                                            :on-change   set-value!
                                            :options     ["hoppy" "citrusy" "smoky" "toasty"]
                                            :placeholder "Add Tags"
                                            :style       {:width 200}
                                            :value       value})))
