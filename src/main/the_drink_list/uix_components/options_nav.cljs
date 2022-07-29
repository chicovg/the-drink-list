(ns the-drink-list.uix-components.options-nav
  (:require
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [uix.core :refer [$ defui use-context]]))

(defui search-input
  []
  (let [{:keys [search-term
                set-search-term!]} (use-context context/app)]
    ($ :div.level-item
       ($ :div.field.has-addons
          ($ :p.control.has-icons-left
             ($ :input.input.search-input
                {:on-change   #(set-search-term! (-> % .-target .-value))
                 :placeholder "Search"
                 :value       search-term})
             ($ :span.icon.is-left
                ($ :i.fas.fa-search {:aria-hidden "true"})))
          ($ :p.control
             ($ :button.button
                {:disabled (empty? search-term)
                 :on-click #(set-search-term! nil)}
                ($ :span.icon
                   ($ :i.fas.fa-times))))))))

(defn- new-sort-state [sort-state clicked-field]
  (if (= clicked-field (:field sort-state))
    (update sort-state :asc? not)
    {:asc?  false
     :field clicked-field}))

(defui sort-button
  [{:keys [label for-field]}]
  (let [{:keys [sort-state
                set-sort-state!]} (use-context context/app)
        {:keys [field asc?]}      sort-state
        active?                   (= field for-field)]
    ($ :p.control
       ($ :button.button
          {:class    (when active? "is-active")
           :on-click #(-> sort-state
                          (new-sort-state for-field)
                          set-sort-state!)}
          ($ :span label)
          (when active?
            ($ :span.icon
               (if asc?
                 ($ :i.fas.fa-sort-amount-up)
                 ($ :i.fas.fa-sort-amount-down))))))))

(defui sort-buttons
  []
  ($ :div.level-item
     ($ :div.field.has-addons
        ($ sort-button {:label     "Date"
                        :for-field :created})
        ($ sort-button {:label     "Rating"
                        :for-field :overall}))))

(defui new-drink-button
  []
  (let [{:keys [show-drink-modal!]} (use-context context/app)]
    ($ :div.level-item
       ($ :p.control
          ($ :button.button.is-primary
             {:on-click #(show-drink-modal! drink-type/default-values)}
             ($ :span.icon
                ($ :i.fas.fa-plus))
             ($ :span "New"))))))

(defui options-nav
  []
  ($ :nav.level.mr-2.ml-2
     ($ :div.level-left
        ($ search-input))
     ($ :div.level-right
        ($ sort-buttons)
        ($ new-drink-button))))
