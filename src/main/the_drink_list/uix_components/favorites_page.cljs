(ns the-drink-list.uix-components.favorites-page
  (:require
   ["recharts" :refer [CartesianGrid
                       Legend
                       ResponsiveContainer
                       Scatter
                       ScatterChart
                       XAxis
                       YAxis]]
   [clojure.set :as set]
   [the-drink-list.uix-components.context :as context]
   [uix.core :refer [$ defui use-context use-state]]))

(defui rating-history
  "A scatter chart which shows history of ratings.
   A separate color per drink type, can select which rating to chose"
  []
  ;; TODO filter start
  ;; how do I decide the range???
  ;; A slider, evently space min date -> max date?
  (let [{:keys [drinks]}         (use-context context/app)
        [data-key set-data-key!] (use-state "overall")
        on-radio-change          (fn [field e]
                                   (let [val (-> e .-target .-value)]
                                     (when (= "on" val)
                                       (set-data-key! field))))
        drink-data               (->> drinks
                                      (map #(update % :created inst-ms))
                                      (map #(set/rename-keys % {:style :drink-style})))
        drinks-by-type            (group-by :type drink-data)
        types                     (map :type drink-data)]
    ($ :div.p-2
       ($ ResponsiveContainer {:height 500 :width "100%"}
          ($ ScatterChart
             ($ CartesianGrid {:strokeDasharray "3 3"})
             ($ Legend)
             ($ XAxis {:dataKey       "created"
                       :domain        (clj->js ["dataMin", "dataMax"])
                       :tickCount     5
                       :tickFormatter #(.toLocaleDateString (js/Date. %))
                       :type          "number"})
             ($ YAxis {:dataKey data-key
                       :domain  (clj->js [0 5])})
             (when (some #{"Beer"} types)
               ($ Scatter {:data (clj->js (get drinks-by-type "Beer"))
                           :fill "#004777"
                           :name "Beer"}))
             (when (some #{"Cider"} types)
               ($ Scatter {:data (clj->js (get drinks-by-type "Cider"))
                           :fill "#A30000"
                           :name "Cider"}))
             (when (some #{"Mead"} types)
               ($ Scatter {:data (clj->js (get drinks-by-type "Mead"))
                           :fill "#FF7700"
                           :name "Mead"}))
             (when (some #{"Wine"} types)
               ($ Scatter {:data (clj->js (get drinks-by-type "Wine"))
                           :fill "#EFD28D"
                           :name "Wine"}))
             (when (some #{"Other"} types)
               ($ Scatter {:data (clj->js (get drinks-by-type "Other"))
                           :fill "#00AFB5"
                           :name "Other"}))))
       ($ :div.mb-2)
       ($ :div.field.is-horizontal
          ($ :div.field-label
             ($ :label.label "Rating Shown"))
          ($ :div.field-body
             ($ :div.field
                ($ :div.control

                   ($ :label.radio
                      ($ :input {:type      :radio
                                 :name      :rating
                                 :on-change (partial on-radio-change "overall")})
                      ($ :span.ml-2 "Overall"))
                   ($ :label.radio
                      ($ :input {:type      :radio
                                 :name      :rating
                                 :on-change (partial on-radio-change "appearance")})
                      ($ :span.ml-2 "Appearance"))
                   ($ :label.radio
                      ($ :input {:type      :radio
                                 :name      :rating
                                 :on-change (partial on-radio-change "smell")})
                      ($ :span.ml-2 "Smell"))
                   ($ :label.radio
                      ($ :input {:type      :radio
                                 :name      :rating
                                 :on-change (partial on-radio-change "taste")})
                      ($ :span.ml-2 "Taste")))))))))

(defui styles-breakdown
  "A pie chart which shows how many drinks of each style we tasted"
  [])

(defui favorites-page
  []
  (let [[favorites-panel set-favorites-panel!] (use-state :history)]
    ($ :div.mt-4.has-background-white
       ($ :div.tabs
          ($ :ul
             ($ :li {:class (when (= favorites-panel :history) "is-active")
                     :key   :history}
                ($ :a {:on-click #(set-favorites-panel! :history)} "Ratings History"))))
       ($ :div.pb-5.pl-4.pr-4
          (condp = favorites-panel
            :history ($ rating-history)
            nil)))))
