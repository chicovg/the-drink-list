(ns the-beer-list.views
  (:require
   [clojure.string :refer [blank?]]
   [reagent.core :refer [atom]]
   [re-frame.core :as rf]
   [breaking-point.core :as bp]
   [the-beer-list.events :as events]
   [the-beer-list.subs :as subs]))

;; form fields

(defn on-field-change
  [id]
  (fn [el]
    (let [val (-> el .-target .-value)]
      (rf/dispatch [::events/set-beer-modal-value id val]))))

(defn text-input [{:keys [id label placeholder]}]
  (let [field-value (rf/subscribe [::subs/beer-modal-field-value id])
        field-error (rf/subscribe [::subs/beer-modal-field-error id])]
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
  [{:keys [id label placeholder]}]
  (let [field-value (rf/subscribe [::subs/beer-modal-field-value id])
        field-error (rf/subscribe [::subs/beer-modal-field-error id])]
    (fn [{:keys [id label placeholder]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:textarea.textarea {:class (if @field-error "is-danger")
                             :rows 4
                             :cols 50
                             :max-length 200
                             :placeholder placeholder
                             :on-change (on-field-change id)
                             :value @field-value}]
        (if @field-error
          [:p.help.is-danger @field-error])]])))

(defn select-input
  [{:keys [id label options default-option]}]
  (let [field-value (rf/subscribe [::subs/beer-modal-field-value id])]
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

(defn close-beer-modal
  []
  (rf/dispatch [::events/clear-and-hide-beer-modal]))

(defn save-beer
  [beer]
  (fn []
    (rf/dispatch [::events/try-save-beer beer])))

(defn beer-form
  []
  [:div {:role "form"}
   [text-input {:id :name
                :label "Beer Name"
                :placeholder "A Delicious Brew"}]
   [text-input {:id :brewery
                :label "Brewery"
                :placeholder "Your Favorite Brewery"}]
   [text-input {:id :type
                :label "Beer Type"
                :placeholder "Ale? Lager? Gose?"}]
   [select-input {:id :rating
                  :label "Rating"
                  :options rating-options
                  :default-option 3}]
   [textarea-input {:id :comment
                    :label "Comment"
                    :placeholder "Provide more details here..."}]])

(defn beer-modal-title
  [is-adding?]
  [:p.modal-card-title (if is-adding?
                         "Add a new beer!"
                         "Update this beer")])

(defn save-failed-message
  [save-failed?]
  (if save-failed?
    [:article.message.is-danger
     [:div.message-body
      "Unable to save at this time, please try again later."]]))

(defn beer-modal-view
  [{:keys [beer is-adding? showing? save-failed?]}]
  [:div.modal {:class (if showing? "is-active")}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [beer-modal-title is-adding?]
     [:button.delete {:aria-label "close"
                      :on-click close-beer-modal}]]
    [:section.modal-card-body
     [save-failed-message save-failed?]
     [beer-form]
    [:footer.modal-card-foot
     [:button.button.is-success {:on-click (save-beer beer)}
      "Save"]
     [:button.button {:on-click close-beer-modal}
      "Cancel"]]]]])

(defn beer-modal
  []
  (let [beer (rf/subscribe [::subs/beer-modal-beer])
        beer-modal-is-adding? (rf/subscribe [::subs/beer-modal-is-adding?])
        beer-modal-showing? (rf/subscribe [::subs/beer-modal-showing?])
        save-failed? (rf/subscribe [::subs/save-failed?])]
    [beer-modal-view {:beer @beer
                      :is-adding? @beer-modal-is-adding?
                      :showing? @beer-modal-showing?
                      :save-failed? @save-failed?}]))

;; delete confirm modal

(defn close-delete-confirm-modal
  []
  (rf/dispatch [::events/update-delete-confirm-state :hide]))

(defn delete-beer
  [id]
  (rf/dispatch [::events/delete-beer id]))

(defn delete-confirm-modal
  []
  (let [delete-confirm-modal-showing? (rf/subscribe [::subs/delete-confirm-modal-showing?])
        delete-confirm-id (rf/subscribe [::subs/delete-confirm-id])
        delete-failed? (rf/subscribe [::subs/delete-failed?])]
    [:div.modal {:class (if @delete-confirm-modal-showing? "is-active" "")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Please confirm"]
       [:button.delete {:aria.label "close"
                        :on-click close-delete-confirm-modal}]]
      [:section.modal-card-body
       (if @delete-failed?
         [:article.message.is-danger
          [:div.message-body
           "Unable to delete at this time, please try again later."]])
       [:p "Are you sure that you want to delete?"]]
      [:footer.modal-card-foot
       [:button.button.is-success
        {:on-click #(delete-beer @delete-confirm-id)}
        "Delete"]
       [:button.button {:on-click close-delete-confirm-modal}
        "Cancel"]]]]))

;; loading modal

(defn loading-modal
  []
  (let [loading-modal-showing? (rf/subscribe [::subs/loading-modal-showing?])]
    [:div.modal {:class (if @loading-modal-showing? "is-active" "")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Loading..."]]
      [:section.modal-card-body
       [:progress.progress.is-medium.is-dark {:max 100}]]
      [:footer.modal-card-foot]]]))

;; home

(defn set-list-filter
  [value]
  (rf/dispatch [::events/set-beer-list-filter value]))

(defn toolbar
  []
  [:div.toolbar
   [:div.field.search
    [:p.control.has-icons-right
     [:input.input {:on-change #(set-list-filter (-> %
                                                    .-target
                                                    .-value))
                    :placeholder "Search beers..."}]
     [:span.icon.is-small.is-right
      [:i.fas.fa-search]]]]
   [:button.button {:on-click
                    #(rf/dispatch [::events/show-add-beer-modal])}
    [:span.icon.is-small
     [:i.fas.fa-plus]]
    [:span "Add Beer"]]])

(defn show-edit-beer-modal
  [beer]
  (rf/dispatch [::events/show-edit-beer-modal beer]))

(defn show-delete-confirm-modal
  [id]
  (rf/dispatch [::events/show-delete-confirm-modal id]))

(defn beers-table-view
  [beers]
  [:div.table-container
   [:table.table.is-striped
    [:thead
     [:tr
      [:th "Beer"]
      [:th "Brewery"]
      [:th "Type"]
      [:th "Rating"]
      [:th "Comment"]
      [:th "Actions"]]]
    [:tbody
     (for [{:keys [id name brewery type rating comment] :as beer} beers]
       ^{:key id} [:tr
                   [:td name]
                   [:td brewery]
                   [:td type]
                   [:td rating]
                   [:td comment]
                   [:td
                    [:button.button.row-button
                     {:on-click #(show-edit-beer-modal {:id id
                                                        :name name
                                                        :brewery brewery
                                                        :type type
                                                        :rating rating
                                                        :comment comment})}
                     [:span.icon.is-small
                      [:i.fas.fa-edit]]
                     [:span "Edit"]]
                    [:button.button.row-button
                     {:on-click #(show-delete-confirm-modal id)}
                     [:span.icon.is-small
                      [:i.fas.fa-trash]]
                     [:span "Delete"]]]])]]])

(defn beers-table
  []
  (let [beers (rf/subscribe [::subs/beers])]
    [beers-table-view @beers]))

(defn home-panel
  []
  (fn []
    [:div
     [toolbar]
     [beers-table]]))

;; about

(defn about-panel
  []
  [:div
   [:h1 "This is the About Page."]
   [:div
    [:a {:href "#/"}
     "go to Home Page"]]])

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
    (if @log-in-failed?
      [:article.message.is-danger
       [:div.message-body
        "Unable to log into the app right now. Please try again later."]])))

(defn main-panel
  []
  (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
        active-panel (rf/subscribe [::subs/active-panel])]
    [:div.section.main
     [beer-modal]
     [delete-confirm-modal]
     [loading-modal]
     [header @is-logged-in?]
     [log-in-error-panel]
     (if @is-logged-in?
       [show-panel @active-panel]
       [login-page])
     [footer]]))
