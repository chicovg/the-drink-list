(ns the-drink-list.uix-components.select-tags-input
  (:require
   ["@creativebulma/bulma-tagsinput" :as bulma-tagsinput]
   [uix.core :refer [$ defui use-effect use-ref]]))

;; TODO a little hack here to deal with webpack vs shadow cljs
(def BulmaTagsInput (or (.-default bulma-tagsinput)
                        bulma-tagsinput))

(def tags-input-base-options {:allowDuplicates false
                              :caseSensitive false
                              :clearSelectionOnTyping false
                              :closeDropdownOnItemSelect true
                              :delimiter ","
                              :freeInput true
                              :highlightDuplicate true
                              :highlightMatchesString true
                              :minChars 1
                              :noResultLabel "No results found"
                              :removable true
                              :searchMinChars 1
                              :searchOn "text"
                              :selectable true
                              :tagClass "is-small is-primary is-light"
                              :trim true})

(defui select-tags-input
  [{:keys [id label on-change options placeholder value]}]
  (let [tags-input-ref     (use-ref)
        bulma-tags-input   (atom nil)
        tags-input-options (clj->js (cond-> tags-input-base-options
                                      placeholder
                                      (assoc :placeholder placeholder)
                                      (not-empty options)
                                      (assoc :source options)))]

    (use-effect
     (fn []
       (reset! bulma-tags-input (new BulmaTagsInput @tags-input-ref tags-input-options)))
     [])

    ($ :div.field
       ($ :label.label {:for id} label)
       ($ :div.control
          ($ :input {:ref         tags-input-ref
                     :id          id
                     :placeholder placeholder
                     :on-change   on-change
                     :type        "text"
                     :value       value})))))
