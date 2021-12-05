(ns the-beer-list.components.drink-stories
  (:require [the-beer-list.components.drink :as drink]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Drink Card Component"
       :component (r/reactify-component drink/card)})

(def sample-drink {:name          "Easy Rider"
                   :maker         "Victory Brewing Company"
                   :type          "Beer"
                   :style         "Session IPA"
                   :appearance    1
                   :smell         3.219833
                   :taste         4.5
                   :notes         ["citrusy" "hoppy" "floral"]
                   :comment       "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque sed libero quam. Sed hendrerit id metus in pellentesque. Nam enim diam, fermentum porta erat eu, pellentesque feugiat mauris. Nullam molestie maximus quam vel mattis. Quisque sapien leo, maximus sit amet justo ut, dapibus dignissim nisi. Nullam mauris nisi, dictum vitae luctus quis, dapibus at leo. Vivamus semper lectus ut sem hendrerit dictum. Donec vitae nulla quis ante efficitur varius. Fusce nec ultricies tellus. Suspendisse dapibus lobortis justo et tempor. Phasellus eget erat aliquam, faucibus metus nec, faucibus risus."})

(defn ^:export Display []
  (r/as-element [drink/card sample-drink]))

(defn ^:export Editing []
  (r/as-element [drink/card (assoc sample-drink :editing? true)]))
