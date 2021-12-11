(ns the-beer-list.api.firebase
  (:require ["firebase/app" :refer [initializeApp]]
            ["firebase/auth" :refer [GoogleAuthProvider
                                     getAuth
                                     onAuthStateChanged
                                     signInWithPopup]]
            ["firebase/firestore" :refer [collection
                                          getDocs
                                          getFirestore]]))

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

(defn listen-to-auth
  [on-auth-change]
  (onAuthStateChanged
   auth
   on-auth-change))

(defn sign-in
  [set-user]
  (-> (signInWithPopup auth provider)
      (.then #((set-user (.-user %))
               (js/console.log "Authenticated successfully")))
      (.catch #(js/console.log "Authentication failed " %))))

(defn sign-out
  [set-user]
  (.signOut auth)
  (set-user nil))

(defn get-drinks
  [uid set-drinks]
  (-> (getDocs (collection db "users" uid "drinks"))
      (.then (fn [qs]
               (let [drinks (atom [])]
                 (.forEach qs (fn [d]
                                (js/console.log d)
                                (swap! drinks conj d)))
                 (set-drinks @drinks))))))
