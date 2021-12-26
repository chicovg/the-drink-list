(ns the-drink-list.components.navbar
  (:require
   [reagent.core :as r]
   [the-drink-list.db :as db]))

(defn navbar
  [_]
  (r/with-let [show-menu? (r/atom false)]
    (fn [{uid :uid}]
      [:nav.navbar.is-primary
       [:div.navbar-brand
        [:div.navbar-item
         [:p.is-size-4.has-text-weight-semi-bold.ml-2
          "The Drink List"]]
        (when uid
          [:a.navbar-burger{:aria-expanded "false"
                            :aria-label    "menu"
                            :class         (when @show-menu? "is-active")
                            :data-target   "nav-menu"
                            :on-click      #(swap! show-menu? not)
                            :role          "button"}
           [:span {:aria-hidden "true"}]
           [:span {:aria-hidden "true"}]
           [:span {:aria-hidden "true"}]])]

       (when uid
         [:div#nav-menu.navbar-menu
          {:class (when @show-menu? "is-active")}
          [:div.navbar-end
           [:a.navbar-item
            {:on-click #(db/sign-out)}
            "Sign Out"]]])])))
