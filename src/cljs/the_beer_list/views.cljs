(ns the-beer-list.views
  (:require
   [clojure.string :refer [blank?]]
   [reagent.core :refer [atom]]
   [re-frame.core :as rf]
   [breaking-point.core :as bp]
   [the-beer-list.events :as events]
   [the-beer-list.subs :as subs]))

;; form fields

(defn required-ok [required value]
  (if required
    (not (blank? value))
    true))

(defn text-input [{:keys [id label placeholder value required]}]
  (let [is-valid (atom true)]
    (fn [{:keys [id label placeholder value required]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:input.input {:type "text"
                       :class (if (not @is-valid) "is-danger")
                       :placeholder placeholder
                       :on-change #(let [val (-> % .-target .-value)]
                                     (do
                                       (reset! value val)
                                       (reset! is-valid (required-ok required-ok val))))
                       :value @value}]]])))

(defn textarea-input [{:keys [id label placeholder value required]}]
  (let [is-valid (atom true)]
    (fn [{:keys [id label placeholder value required]}]
      [:div.field
       [:label.label label]
       [:div.control
        [:textarea.textarea {:class (if (not @is-valid) "is-danger")
                             :rows 4
                             :cols 50
                             :max-length 200
                             :placeholder placeholder
                             :on-change #(let [val (-> % .-target .-value)]
                                           (do
                                             (reset! value val)
                                             (reset! is-valid (required-ok required-ok val))))
                             :value @value}]]])))

(defn select-input [{:keys [id label options default-option value]}]
  (fn [{:keys [id label options default-option value]}]
    [:div.field
     [:label.label label]
     [:div.control
      [:div.select
       [:select {:on-change #(reset! value (-> % .-target .-value))
                 :value @value}
        (for [{:keys [label value]} options]
          ^{:key value} [:option {:value value} label])]]]]))

(def rating-options [{:value 5 :label "5 - Amazing"}
                     {:value 4 :label "4 - Great!"}
                     {:value 3 :label "3 - Okay"}
                     {:value 2 :label "2 - Poor"}
                     {:value 1 :label "1 - Horrible"}])

;; beer modal

(defn close-beer-modal
  []
  (rf/dispatch [::events/hide-beer-modal]))

;; beer
(defn validate-beer
  [])

(defn save-beer
  [beer]
  (rf/dispatch [::events/write-beer-to-firestore beer]))

(defn beer-modal
  []
  (let [beer-modal (rf/subscribe [::subs/beer-modal])
        {:keys [beer operation show]} @beer-modal
        {:keys [id name brewery type rating comment]} beer
        aname (atom (or name ""))
        abrewery (atom (or brewery ""))
        atype (atom (or type ""))
        arating (atom (or rating ""))
        acomment (atom (or comment ""))]
    [:div.modal {:class (if show "is-active")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title (if (= :add operation)
                              "Add a new beer!"
                              "Update this beer")]
       [:button.delete {:aria-label "close"
                        :on-click close-beer-modal}]]
      [:section.modal-card-body
       [:div {:role "form"}
        [text-input {:id :name
                     :label "Beer Name"
                     :placeholder "A Delicious Brew"
                     :value aname
                     :required true}]
        [text-input {:id :brewery
                     :label "Brewery"
                     :placeholder "Your Favorite Brewery"
                     :value abrewery
                     :required true}]
        [text-input {:id :type
                     :label "Beer Type"
                     :placeholder "Ale? Lager? Gose?"
                     :value atype
                     :required true}] ;; TODO dropdown?
        [select-input {:id :rating
                       :label "Rating"
                       :options rating-options
                       :default-option 3
                       :value arating}]
        [textarea-input {:id :comment
                         :label "Comment"
                         :placeholder "Provide more details here..."
                         :value acomment
                         :required false}]]]
      [:footer.modal-card-foot
       [:button.button.is-success
        {:on-click #(save-beer {:id id
                                :name @aname
                                :brewery @abrewery
                                :type @atype
                                :rating @arating
                                :comment @acomment})}
        "Save"]
       [:button.button
        {:on-click close-beer-modal}
        "Cancel"]]]]))

;; delete confirm modal

(defn close-delete-confirm-modal
  []
  (rf/dispatch [::events/hide-delete-confirm-modal]))

(defn delete-beer
  [id]
  (rf/dispatch [::events/delete-beer id]))

(defn delete-confirm-modal
  []
  (let [delete-confirm-modal (rf/subscribe [::subs/delete-confirm-modal])
        {:keys [id show]} @delete-confirm-modal]
    [:div.modal {:class (if show "is-active" "")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Please confirm"]
       [:button.delete {:aria.label "close"
                        :on-click close-delete-confirm-modal}]]
      [:section.modal-card-body
       [:p "Are you sure that you want to delete?"]]
      [:footer.modal-card-foot
       [:button.button.is-success
        {:on-click #(delete-beer id)}
        "Delete"]
       [:button.button {:on-click close-delete-confirm-modal}
        "Cancel"]]]]))

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

(defn beers-table
  []
  (let [beers (rf/subscribe [::subs/beers])]
    [:table.table
     [:thead
      [:tr
       [:th "Beer"]
       [:th "Brewery"]
       [:th "Type"]
       [:th "Rating"]
       [:th "Comment"]
       [:th "Actions"]]]
     [:tbody
      (for [{:keys [id name brewery type rating comment] :as beer} @beers]
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
                      [:span "Delete"]]]])]]))

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

(defn main-panel
  []
  (let [is-logged-in (rf/subscribe [::subs/is-logged-in])
        active-panel (rf/subscribe [::subs/active-panel])]
    [:div.section.main
     [beer-modal]
     [delete-confirm-modal]
     [header @is-logged-in]
     (if @is-logged-in
       [show-panel @active-panel]
       [login-page])
     [footer]]))
