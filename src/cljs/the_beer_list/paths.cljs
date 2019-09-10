(ns the-beer-list.paths)

(def add-beer-path "#/beer/new")
(defn edit-beer-path
  [id]
  (str "#/beer/edit/" id))
(def home-path "#/")
