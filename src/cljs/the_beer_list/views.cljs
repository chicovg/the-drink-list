(ns the-beer-list.views
  (:require
   [re-frame.core :as re-frame]
   [breaking-point.core :as bp]
   [the-beer-list.events :as events]
   [the-beer-list.subs :as subs]))

;; form fields

(defn reset-value [value]
  (fn [el]
    (reset! value (-> el
                      .-target
                      .-value))))

(defn text-input [id label placeholder value]
  [:div.field
   [:label.label label]
   [:div.control
    [:input.input {:type "text"
                   :placeholder placeholder
                   :on-change (reset-value value)
                   :value @value}]]])

(defn textarea-input [id label placeholder value]
  [:div.field
   [:label.label label]
   [:div.control
    [:textarea.textarea {:placeholder placeholder
                         :rows 4
                         :cols 50
                         :max-length 200
                         :on-change #(reset! value (-> %
                                                       .-target
                                                       .-value))
                         :value @value}]]])

(defn select-input [id label options value]
  [:div.field
   [:label.label label]
   [:div.control
    [:div.select
     [:select {:value (or @value 3)
               :on-change #(reset! value (-> %
                                             .-target
                                             .-value))}
      (for [{:keys [label value]} options]
        ^{:key value} [:option {:value value} label])]]]])

(def rating-options [{:value 5 :label "5 - Amazing"}
                     {:value 4 :label "4 - Great!"}
                     {:value 3 :label "3 - Okay"}
                     {:value 2 :label "2 - Poor"}
                     {:value 1 :label "1 - Horrible"}])

;; beer modal

(defn beer-form [name
                 brewery
                 type
                 rating
                 comment]
  [:div {:role "form"}
   [text-input :name "Beer Name" "Delicious Ale" name]
   [text-input :brewery "Brewery" "A Top Tier Brewery" brewery]
   [text-input :type "Beer Type" "Ale? Gose? Lager?" type] ;; TODO dropdown?
   [select-input :rating "Rating" rating-options rating]
   [textarea-input :comment "Comment" "The beer was great..." comment]])

(defn close-beer-modal
  []
  (re-frame/dispatch-sync [::events/hide-beer-modal]))

(defn save-beer
  [beer]
  (re-frame/dispatch-sync [::events/save-beer beer]))

(defn beer-modal
  []
  (let [beer-modal (re-frame/subscribe [::subs/beer-modal])
        {:keys [beer operation show]} @beer-modal
        title (if (= :add operation)
                "Add a new beer!"
                "Update this beer")
        {:keys [id name brewery type rating comment]} beer
        name (atom name)
        brewery (atom brewery)
        type (atom type)
        rating (atom rating)
        comment (atom comment)]
    [:div.modal {:class (if show "is-active" "")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title title]
       [:button.delete {:aria-label "close"
                        :on-click close-beer-modal}]]
      [:section.modal-card-body
       [beer-form name brewery type rating comment]]
      [:footer.modal-card-foot
       [:button.button.is-success
        {:on-click #(save-beer {:id id
                                :name @name
                                :brewery @brewery
                                :type @type
                                :rating @rating
                                :comment @comment})}
        "Save"]
       [:button.button
        {:on-click close-beer-modal}
        "Cancel"]]]]))

;; delete confirm modal

(defn close-delete-confirm-modal
  []
  (re-frame/dispatch-sync [::events/hide-delete-confirm-modal]))

(defn delete-beer
  [id]
  (re-frame/dispatch-sync [::events/delete-beer id]))

(defn delete-confirm-modal
  []
  (let [delete-confirm-modal (re-frame/subscribe [::subs/delete-confirm-modal])
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
  (re-frame/dispatch-sync [::events/set-beer-list-filter value]))

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
                    #(re-frame/dispatch-sync [::events/show-add-beer-modal])}
    [:span.icon.is-small
     [:i.fas.fa-plus]]
    [:span "Add Beer"]]])

(defn show-edit-beer-modal
  [beer]
  (re-frame/dispatch-sync [::events/show-edit-beer-modal beer]))

(defn show-delete-confirm-modal
  [id]
  (re-frame/dispatch-sync [::events/show-delete-confirm-modal id]))

(defn beers-table []
  (let [beers (re-frame/subscribe [::subs/beers])]
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

(defn home-panel []
  (fn []
    [:div
     [toolbar]
     [beers-table]]))

;; about

(defn about-panel []
  [:div
   [:h1 "This is the About Page."]

   [:div
    [:a {:href "#/"}
     "go to Home Page"]]])

;; header

(defn toggle-is-active [cl]
  (.toggle cl "is-active"))

(defn get-element [id]
  (.getElementById js/document id))

(defn navbar []
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
     [:a.navbar-item {:href "#/about"} "About"]]]])

(defn header []
  [:header
   [navbar]])

;; footer

(defn footer []
  [:footer.footer
   [:div.content.is-small
    [:p
     "Vector Illustration by " [:a {:rel "nofollow" :href "https://www.vecteezy.com"} "www.Vecteezy.com."]]]])

;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [:div.section.main
     [beer-modal]
     [delete-confirm-modal]
     [header]
     [show-panel @active-panel]
     [footer]]))
