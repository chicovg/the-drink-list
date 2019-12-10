(ns the-beer-list.views
  (:require
   [re-frame.core :as rf]
   [the-beer-list.events :as events]
   [the-beer-list.subs :as subs]
   [the-beer-list.paths :refer [edit-beer-path home-path]]))

;; form fields

(defn on-field-change
  [id]
  (fn [el]
    (let [val (-> el .-target .-value)]
      (rf/dispatch [::events/set-beer-form-value id val]))))

(defn text-input [{:keys [id]}]
  (let [field-value (rf/subscribe [::subs/beer-form-field-value id])
        field-error (rf/subscribe [::subs/beer-form-field-error id])]
    (fn [{:keys [id label placeholder]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:input.input {:type "text"
                       :class (if @field-error "is-danger")
                       :placeholder placeholder
                       :on-change (on-field-change id)
                       :value @field-value}]
        (if @field-error
          [:p.help.is-danger @field-error])]])))

(defn textarea-input
  [{:keys [id]}]
  (let [field-value (rf/subscribe [::subs/beer-form-field-value id])
        field-error (rf/subscribe [::subs/beer-form-field-error id])]
    (fn [{:keys [id label placeholder]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:textarea.textarea {:class (if @field-error "is-danger")
                             :rows 4
                             :cols 200
                             :max-length 800
                             :placeholder placeholder
                             :on-change (on-field-change id)
                             :value @field-value}]
        (if @field-error
          [:p.help.is-danger @field-error])]])))

(defn select-input
  [{:keys [id]}]
  (let [field-value (rf/subscribe [::subs/beer-form-field-value id])]
    (fn [{:keys [id label options default-option]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:div.select
         [:select {:on-change (on-field-change id)
                   :value (or @field-value default-option)}
          (for [{:keys [label value]} options]
            ^{:key value} [:option {:value value} label])]]]])))

(def rating-options [{:value 5 :label "5 - Amazing"}
                     {:value 4 :label "4 - Great!"}
                     {:value 3 :label "3 - Okay"}
                     {:value 2 :label "2 - Poor"}
                     {:value 1 :label "1 - Horrible"}])

;; beer modal

(defn save-beer
  [beer]
  (fn []
    (rf/dispatch [::events/try-save-beer beer])))

(defn close-save-success-message
  []
  (rf/dispatch [::events/update-beer-form-state :hide]))

(defn show-delete-confirm-modal
  [{id :id}]
  (fn []
    (rf/dispatch [::events/show-delete-confirm-modal id])))

(defn beer-form
  [beer show-delete-button?]
  [:div {:role "form"}
   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:div.tile.is-child
      [text-input {:id :name
                   :label "Beer Name"
                   :placeholder "A Delicious Brew"}]]
     [:div.tile.is-child
      [text-input {:id :brewery
                   :label "Brewery"
                   :placeholder "Your Favorite Brewery"}]]]]
   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:div.tile.is-child
      [text-input {:id :type
                   :label "Beer Type"
                   :placeholder "Ale? Lager? Gose?"}]]
     [:div.tile.is-child
      [select-input {:id :rating
                     :label "Rating"
                     :options rating-options
                     :default-option 3}]]]]
   [:div.tile.is-ancestor
    [:div.tile.is-parent.is-12
     [textarea-input {:id :comment
                      :label "Comment"
                      :placeholder "Provide more details here..."}]]]
   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:button.button.is-primary {:on-click (save-beer beer)} "Save"]
     (when show-delete-button?
       [:button.button.is-danger {:on-click (show-delete-confirm-modal beer)} "Delete"])
     [:a.button {:href "#/"} "Cancel"]]]])

(defn beer-panel-title
  [is-adding?]
  [:h1.title (if is-adding?
               "Add a new beer!"
               "Update this beer")])

(defn save-in-progress
  [is-saving?]
  (when is-saving?
    [:progress.progress.is-small.is-info {:max 100}]))

(defn save-failed-message
  [save-failed?]
  (when save-failed?
    [:article.message.is-danger
     [:div.message-body
      "Unable to save at this time, please try again later."]]))

(defn save-success-message
  [save-succeeded?]
  (when save-succeeded?
    [:article.message.is-success
     [:div.message-header
      [:p "Saved Successfully"]
      [:button.delete {:aria-label "delete"
                       :on-click close-save-success-message}]]
     [:div.message-body
      [:a.button {:href home-path
                  :on-click close-save-success-message}
       "Go Home"]]]))

(defn beer-panel-view
  [{:keys [beer is-adding? is-saving? save-failed? save-succeeded?]}]
  [:div.container
   [save-in-progress is-saving?]
   [save-failed-message save-failed?]
   [save-success-message save-succeeded?]
   [beer-panel-title is-adding?]
   [beer-form beer (not is-adding?)]])

(defn add-beer-panel
  []
  (let [beer (rf/subscribe [::subs/beer-form-beer])
        is-saving? (rf/subscribe [::subs/is-saving?])
        save-failed? (rf/subscribe [::subs/save-failed?])]
    [beer-panel-view {:beer @beer
                      :is-adding? true
                      :is-saving? @is-saving?
                      :save-failed? @save-failed?}]))

(defn edit-beer-panel
  []
  (let [beer (rf/subscribe [::subs/beer-form-beer])
        is-saving? (rf/subscribe [::subs/is-saving?])
        save-failed? (rf/subscribe [::subs/save-failed?])]
    [beer-panel-view {:beer @beer
                      :is-adding? false
                      :is-saving? @is-saving?
                      :save-failed? @save-failed?}]))

;; delete confirm modal

(defn close-delete-confirm-modal
  []
  (rf/dispatch [::events/update-delete-confirm-state :hide]))

(defn delete-beer
  [id]
  (fn []
    (rf/dispatch [::events/delete-beer id])))

(defn delete-failed-message
  [delete-failed?]
  (when delete-failed?
    [:article.message.is-danger
     [:div.message-body
      "Unable to delete at this time, please try again later."]]))

(defn delete-confirm-modal-view
  [{:keys [modal-showing? id delete-failed?]}]
  [:div.modal {:class (when modal-showing? "is-active")}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Please confirm"]
     [:button.delete {:aria.label "close"
                      :on-click close-delete-confirm-modal}]]
    [:section.modal-card-body
     [delete-failed-message delete-failed?]
     [:p "Are you sure that you want to delete?"]]
    [:footer.modal-card-foot
     [:a.button.is-danger {:href home-path
                           :on-click (delete-beer id)} "Delete"]
     [:button.button {:on-click close-delete-confirm-modal} "Cancel"]]]])

(defn delete-confirm-modal
  []
  (let [delete-confirm-modal-showing? (rf/subscribe [::subs/delete-confirm-modal-showing?])
        delete-confirm-id (rf/subscribe [::subs/delete-confirm-id])
        delete-failed? (rf/subscribe [::subs/delete-failed?])]
    [delete-confirm-modal-view {:modal-showing? @delete-confirm-modal-showing?
                                :id @delete-confirm-id
                                :delete-failed? @delete-failed?}]))

;; loading modal

(defn loading-modal-view
  [showing?]
    [:div.modal {:class (if showing? "is-active" "")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Loading..."]]
      [:section.modal-card-body
       [:progress.progress.is-medium.is-dark {:max 100}]]
      [:footer.modal-card-foot]]])

(defn loading-modal
  []
  (let [loading-modal-showing? (rf/subscribe [::subs/loading-modal-showing?])]
    [loading-modal-view @loading-modal-showing?]))

;; sort modal

(defn update-sort
  [key]
  (fn [el]
    (let [value (-> el .-target .-value)
          value-as-keyword (keyword value)]
      (rf/dispatch [::events/set-beer-list-sort key value-as-keyword]))))

(defn select-sort-field
  [value]
  [:div.field
   [:label.label "Field"]
   [:div.control
    [:div.select
     [:select {:on-change (update-sort :field)
               :value value}
      [:option {:value :name} "Name"]
      [:option {:value :brewery} "Brewery"]
      [:option {:value :rating} "Rating"]]]]])

(defn select-sort-order
  [value]
  [:div.field
   [:label.label "Order"]
   [:div.control
    [:div.select
     [:select {:on-change (update-sort :order)
               :value value}
      [:option {:value :asc} "Ascending"]
      [:option {:value :desc} "Descending"]]]]])

(defn close-sort-modal
  []
  (rf/dispatch [::events/update-sort-modal-state :hide]))

(defn sort-done-button
  []
  [:button.button {:on-click close-sort-modal} "Done"])

(defn sort-modal-view
  [showing? {:keys [field order]}]
  [:div.modal {:class (when showing? "is-active")}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Set beer sort"]]
    [:section.modal-card-body.sort-modal-body
     [select-sort-field field]
     [select-sort-order order]]
    [:footer.modal-card-foot
     [sort-done-button]]]])

(defn on-sort-key-change
  [key sort-state-atom]
  (fn [el]
    (let [value (-> el .-target .-value)]
      (swap! sort-state-atom assoc key value))))

(defn sort-modal
  []
  (let [sort-modal-showing? (rf/subscribe [::subs/sort-modal-showing?])
        beer-list-sort (rf/subscribe [::subs/beer-list-sort])]
    [sort-modal-view @sort-modal-showing? @beer-list-sort]))

;; home panel

(defn set-list-filter
  [value]
  (rf/dispatch [::events/set-beer-list-filter value]))

(defn beer-filter-input
  []
  [:div.field.search
   [:p.control.has-icons-right
    [:input.input {:on-change #(set-list-filter (-> %
                                                    .-target
                                                    .-value))
                   :placeholder "Filter beers..."}]
    [:span.icon.is-small.is-right
     [:i.fas.fa-search]]]])

(defn add-beer-button
  []
  [:a.button {:aria-label "Add new beer"
              :href "#/beer/new"}
   [:span.icon.is-small
    [:i.fas.fa-plus]]])

(defn open-sort-modal
  []
  (rf/dispatch [::events/update-sort-modal-state :show]))

(defn sort-button
  []
  [:button.button {:aria-label "Sort beers"
                   :on-click open-sort-modal}
   [:span.icon.is-small
    [:i.fas.fa-sort]]])

(defn toolbar
  []
  [:div.toolbar
    [beer-filter-input]
    [sort-button]
    [add-beer-button]])

(defn show-edit-beer-modal
  [beer]
  (rf/dispatch [::events/show-edit-beer-modal beer]))

(defn show-edit-beer-panel
  [beer]
  (rf/dispatch [::events/show-edit-beer-panel beer]))

(defn beer-list-rating
  [rating]
  [:div.is-pulled-left.rating {:class (str "rating-" rating)}
   [:p.is-small rating]])

(defn beer-list-item
  [{:keys [id name brewery rating]}]
  [:a.list-item {:tab-index "0"
                 :href (edit-beer-path id)}
    [beer-list-rating rating]
    [:strong.is-size-5 name]
    [:span.is-italic.is-size-6 (str " " brewery)]])

(defn beers-list-view
  [beers]
  [:div.list.is-hoverable
   (for [{:keys [id] :as beer} beers]
     ^{:key id} [beer-list-item beer])])

(defn beers-list
  []
  (let [beers (rf/subscribe [::subs/beers])]
    [beers-list-view @beers]))

(defn home-panel
  []
  (fn []
    [:div
     [toolbar]
     [beers-list]]))

;; about

(defn about-panel
  []
  [:div.container
   [:p "The beer list application helps you track and rate your favorite beers."]
   [:p "To get started, try a new beer, go to the home page, and then write a review!"]
   [:div
    [:a {:href "#/"}
     "Go to the home page"]]])

;; not found

(defn not-found-panel
  []
  [:div.container
   [:p "Nothing to see here"]])

;; header

(defn toggle-is-active
  [cl]
  (.toggle cl "is-active"))

(defn get-element
  [id]
  (.getElementById js/document id))

(defn navbar
  []
  [:nav.navbar {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "#/"}
     [:img.logo {:src "/img/BeerIconAndTitle.png" :alt "The Beer List"}]]
    [:a.navbar-burger.burger {:role "button"
                              :aria-label "menu"
                              :aria-expanded "false"
                              :data-target "navbarMenu"
                              :on-click #(do (-> %
                                                 .-target
                                                 .-dataset
                                                 .-target
                                                 get-element
                                                 .-classList
                                                 toggle-is-active))}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]]
   [:div#navbarMenu.navbar-menu
    [:div.navbar-start
     [:a.navbar-item {:href "#/about"} "About"]
     [:a.navbar-item {:on-click #(rf/dispatch [::events/sign-out])}
      "Logout"]]]])

(defn navbar-logged-out
  []
  [:nav.navbar {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "#/"}
     [:img.logo {:src "/img/BeerIconAndTitle.png" :alt "The Beer List"}]]]])

(defn header
  [is-logged-in]
  [:header
   (if is-logged-in
     [navbar]
     [navbar-logged-out])])

;; footer

(defn footer
  []
  [:footer.footer
   [:div.content.is-small
    [:p
     "Vector Illustration by "
     [:a {:rel "nofollow" :href "https://www.vecteezy.com"}
     "www.Vecteezy.com."]]]])

;; main

(defn- panels
  [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :add-beer-panel [add-beer-panel]
    :edit-beer-panel [edit-beer-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn login-page
  []
  [:div.hero-body
   [:div.container
    [:h1.title "Welcome to the beer list!"]
    [:h2.subtitle "Login with Google to track your beers"]]
   [:div.container
    [:button.button.is-link.login {:on-click #(rf/dispatch [::events/sign-in])}
     [:span.icon.is-small
      [:i.fab.fa-google]]
     [:span "Login"]]]])

(defn log-in-error-panel
  []
  (let [log-in-failed? (rf/subscribe [::subs/log-in-failed?])]
    (when @log-in-failed?
      [:article.message.is-danger
       [:div.message-body
        "Unable to log into the app right now. Please try again later."]])))

(defn main-panel
  []
  (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
        active-panel (rf/subscribe [::subs/active-panel])]
    [:div.main
     [:div
      [delete-confirm-modal]
      [loading-modal]
      [sort-modal]
      [header @is-logged-in?]
      [log-in-error-panel]
      (if @is-logged-in?
        [show-panel @active-panel]
        [login-page])
      [footer]]]))
