(ns the-drink-list.api.firebase
  (:require ["firebase/app" :refer [initializeApp]]
            ["firebase/auth" :refer [GoogleAuthProvider
                                     getAuth
                                     onAuthStateChanged
                                     signInWithPopup]]
            ["firebase/firestore" :refer [addDoc
                                          collection
                                          deleteDoc
                                          doc
                                          getDocs
                                          getFirestore
                                          setDoc
                                          Timestamp]]
            [clojure.walk :as walk]
            [the-drink-list.types.drink :as drink]
            [clojure.string :as str]))

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
  {:uid (.-uid user)})

(defn listen-to-auth
  [set-user]
  (onAuthStateChanged auth #(set-user (fb-user->user %))))

(defn sign-in
  [set-user]
  (-> (signInWithPopup auth provider)
      (.then #((set-user (fb-user->user (.-user %)))
               (js/console.log "Authenticated successfully")))
      (.catch #(js/console.log "Authentication failed " %))))

(defn sign-out
  [set-user set-drinks]
  (.signOut auth)
  (set-drinks nil)
  (set-user nil))

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

(defn get-drinks
  [uid set-drinks]
  (-> (getDocs (collection db "users" uid "drinks"))
      (.then (fn [qs]
               (let [drinks (atom {})]
                 (.forEach qs #(swap! drinks assoc (.-id %) (doc->drink %)))
                 (set-drinks @drinks))))))

(defn save-drink!
  [uid drink add-drink on-success on-error]
  (if (:id drink)
    (-> (setDoc (doc db "users" uid "drinks" (:id drink)) (-> drink
                                                              (dissoc :id)
                                                              (update :created date->timestamp)
                                                              clj->js))
        (.then #(add-drink (drink/set-overall drink)))
        (.then on-success)
        (.catch on-error))
    (let [timestamp (.now Timestamp)]
      (-> (addDoc (collection db "users" uid "drinks") (-> drink
                                                           (assoc :created timestamp)
                                                           clj->js))
          (.then #(add-drink (-> drink
                                 (assoc :id (.-id %))
                                 (assoc :created (.toDate timestamp))
                                 drink/set-overall)))
          (.then on-success)
          (.catch on-error)))))

(defn delete-drink!
  [uid drink-id delete-drink on-success on-error]
  (-> (deleteDoc (doc db "users" uid "drinks" drink-id))
      (.then #(delete-drink drink-id))
      (.then on-success)
      (.catch on-error)))
