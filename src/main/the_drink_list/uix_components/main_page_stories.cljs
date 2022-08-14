(ns the-drink-list.uix-components.main-page-stories
  (:require
   [the-drink-list.uix-components.state :as state]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.main-page :as main-page]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Pages/Main Page"
       :component main-page/main-page})

(def drinks (drink-type/gen-drinks 20))

(defn ^:export Default []
  ($ (.-Provider context/app)
     {:value (state/use-app-state {:drinks   drinks
                                   :loading? false})}
     ($ main-page/main-page {:drinks drinks})))

(defn ^:export Loading []
  ($ (.-Provider context/app)
     {:value (state/use-app-state {:drinks   drinks
                                   :loading? true})}
     ($ main-page/main-page {:drinks drinks})))
