(ns the-drink-list.uix-components.main-page-stories
  (:require
   [the-drink-list.uix-components.main-page :as main-page]
   [the-drink-list.types.drink :as drink-type]
   [uix.core :refer [$]]
   [the-drink-list.uix-components.context :as context]))

(def ^:export default
  #js {:title     "UIX Pages/Main Page"
       :component main-page/main-page})

(def drinks (drink-type/gen-drinks 20))

(defn ^:export Default []
  ($ (.-Provider context/app)
     {:value (context/use-app-state {:drinks   drinks
                                     :loading? false})}
     ($ main-page/main-page {:drinks drinks})))

(defn ^:export Loading []
  ($ (.-Provider context/app)
     {:value (context/use-app-state {:drinks   drinks
                                     :loading? true})}
     ($ main-page/main-page {:drinks drinks})))
