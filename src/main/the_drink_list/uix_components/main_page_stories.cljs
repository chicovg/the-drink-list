(ns the-drink-list.uix-components.main-page-stories
  (:require
   [the-drink-list.uix-components.main-page :as main-page]
   [the-drink-list.types.drink :as drink-type]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Pages/Main Page"
       :component main-page/main-page})

(def drinks (drink-type/gen-drinks 20))

(defn ^:export Default []
  ($ main-page/main-page {:drinks drinks}))

(defn ^:export Loading []
  ($ main-page/main-page {:drinks   drinks
                          :loading? true}))
