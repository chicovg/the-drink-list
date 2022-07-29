(ns the-drink-list.uix-components.options-nav-stories
  (:require
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.options-nav :as options-nav]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Components/Options Nav"
       :component options-nav/options-nav})

(defn ^:export Default []
  ($ (.-Provider context/app) {:value (state/use-app-state)}
     ($ options-nav/options-nav)))
