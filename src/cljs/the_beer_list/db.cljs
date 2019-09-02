(ns the-beer-list.db)

(def beer-form-default {:beer {:rating 3}})

;; TODO make a slice for each section?
(def default-db {:active-panel nil
                 :app-state :ready
                 :beer-map {}
                 :beer-modal-state :ready
                 :beer-form beer-form-default
                 :beer-form-state :ready
                 :beer-list-filter nil
                 :delete-confirm-id nil
                 :delete-confirm-state :ready
                 :firestore-failure nil
                 :loading-modal-state :ready
                 :log-in-state :ready
                 :user nil})

(def beer-form-states {:ready {:field-changed :ready
                               :save-no-name :name-required
                               :save-no-brewery :brewery-required
                               :save-no-type :type-required
                               :try-save :saving}
                       :saving {:firestore-failure :save-failed
                                :firestore-success :save-succeeded}
                       :save-failed {:try-save :saving
                                     :hide :ready}
                       :name-required {:field-changed :showing
                                       :hide :ready}
                       :brewery-required {:field-changed :showing
                                          :hide :ready}
                       :type-required {:field-changed :showing
                                       :hide :ready}
                       :save-succeeded {:hide :ready}})

(def delete-confirm-states {:ready {:show :showing}
                            :showing {:hide :ready
                                      :try-delete :deleting}
                            :deleting {:firestore-failure :delete-failed
                                       :firestore-success :ready}
                            :delete-failed {:try-delete :deleting
                                            :hide :ready}})

(def loading-modal-states {:ready {:show :showing}
                           :showing {:firestore-failure :load-failed
                                     :hide :ready}
                           :load-failed {:show :showing}})

(def log-in-states {:ready {:log-in :logging-in
                            :user-received :logged-in}
                    :logging-in {:user-received :logged-in
                                 :no-user-received :log-in-failed}
                    :logged-in {:log-out :logging-out}
                    :log-in-failed {:log-in :logging-in}
                    :logging-out {:no-user-received :ready}})
