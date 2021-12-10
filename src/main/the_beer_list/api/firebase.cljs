(ns the-beer-list.api.firebase)

(defprotocol Firebase
  "A protocol describing the set of firebase operations that the app needs"
  (login [on-login-error on-login-success]
    "Sends user through the Google Oauth flow.
     Calls on-login-success callback if with user info is the login succeeds.
     Calls on-login-error if the login fails"))
