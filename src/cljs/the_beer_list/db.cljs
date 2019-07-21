(ns the-beer-list.db)

(def default-db
  {:active-panel nil
   :beer-map {}
   :beer-modal {:beer nil
                :operation nil
                :show false}
   :beer-list-filter nil
   :delete-confirm-modal {:id nil
                          :show false}
   :firebase {:error nil}
   :user-id nil})
