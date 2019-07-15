(ns the-beer-list.db)

(def default-db
  {:name "re-frame"
   :beer-map {1 {:id 1
                 :name "Watermelon Gose"
                 :brewery "Three Notched"
                 :type "Gose"
                 :rating 4
                 :comment "Very tart with the sable, sweet, and refreshing watermelon taste"}
              2 {:id 2
                 :name "Punkin' Ale"
                 :brewery "Dogfish Head"
                 :type "Pumpkin Ale"
                 :rating 4
                 :comment "One of my favorite pumpkin ales."}}
   :beer-modal {:beer nil
                :operation nil
                :show false}
   :beer-list-filter nil
   :delete-confirm-modal {:id nil
                          :show false}
   :user-id nil})
