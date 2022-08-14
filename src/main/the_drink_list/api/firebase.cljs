(ns the-drink-list.api.firebase
  (:require
   ["firebase/app" :refer [initializeApp]]
   ["firebase/auth" :refer [getAuth GoogleAuthProvider onAuthStateChanged
                          signInWithPopup]]
   ["firebase/firestore" :refer [addDoc collection deleteDoc doc getDocs
                               getFirestore setDoc Timestamp]]
   [clojure.walk :as walk]
   [the-drink-list.types.drink :as drink]))

(defonce app (initializeApp
              (clj->js
               {:apiKey          "AIzaSyBFPzb77tSL_b0ijkTlzDRVjOOfLYrOOkU"
                :authDomain      "the-drink-list.firebaseapp.com"
                :projectId       "the-drink-list"
                :storageBucket   "the-drink-list.appspot.com"
                :messageSenderId "778146380271"
                :appId           "1:778146380271:web:e4157552b5d0ad7fd01e1a"})))

(defonce auth (getAuth))
(defonce provider (GoogleAuthProvider.))
(defonce db (getFirestore))

(defn fb-user->user
  [user]
  (when user
    {:uid (.-uid user)}))

(defn listen-to-auth
  [set-user]
  (onAuthStateChanged auth #(set-user (fb-user->user %))))

(defn sign-in
  [on-success]
  (-> (signInWithPopup auth provider)
      (.then on-success)
      (.then #(js/console.log "Authenticated successfully"))
      (.catch #(js/console.log "Authentication failed " %))))

(defn sign-out
  []
  (.signOut auth))

(defn- doc->clj-map
  [doc]
  (let [id (.-id doc)]
    (some-> doc
            .data
            js->clj
            walk/keywordize-keys
            (assoc :id id))))

(defn- date->timestamp
  [date]
  (some->> date
           (.fromDate Timestamp)))

(defn- timestamp->date
  [^Timestamp ts]
  (some-> ts
          (.toDate)))

(defn- doc->drink
  [doc]
  (-> doc
      doc->clj-map
      (update :created timestamp->date)
      drink/set-overall))

;; TODO
;; each should take
;; set-loading!
;; on-success
;; on-error

(defn get-drinks
  [{:keys [user set-loading! on-success on-error]}]
  (set-loading! true)
  (-> (getDocs (collection db "users" (:uid user) "drinks"))
      (.then (fn [qs]
               (let [drinks (atom {})]
                 (.forEach qs #(swap! drinks assoc (.-id %) (doc->drink %)))
                 (on-success @drinks))))
      (.then #(set-loading! false))
      (.catch on-error)))

(defn save-drink!
  [{:keys [user drink set-loading! on-success on-error]}]
  (set-loading! true)
  (if (:id drink)
    (-> (setDoc (doc db "users" (:uid user) "drinks" (:id drink)) (-> drink
                                                                      (dissoc :id)
                                                                      (update :created date->timestamp)
                                                                      clj->js))
        (.then #(on-success drink))
        (.then #(set-loading! false))
        (.catch on-error))
    (let [timestamp (.now Timestamp)]
      (-> (addDoc (collection db "users" (:uid user) "drinks") (-> drink
                                                                   (assoc :created timestamp)
                                                                   clj->js))
          (.then #(on-success (-> drink
                                  (assoc :id (.-id %))
                                  (assoc :created (.toDate timestamp)))))
          (.then #(set-loading! false))
          (.catch on-error)))))

(defn delete-drink!
  [{:keys [user drink-id set-loading! on-success on-error]}]
  (set-loading! true)
  (-> (deleteDoc (doc db "users" (:uid user) "drinks" drink-id))
      (.then #(on-success drink-id))
      (.then #(set-loading! false))
      (.catch on-error)))
