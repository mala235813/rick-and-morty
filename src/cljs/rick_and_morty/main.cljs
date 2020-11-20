(ns rick-and-morty.main
  (:require [reagent.dom :as rdom]))

(defn start
  []
  (rdom/render [:div
                [:h1 "Rick and morty!!!"]
                [:p "Hot reload!!!"]] (js/document.getElementById "app")))

(defn ^:dev/after-load restart
  []
  (start))

(defn init!
  []
  (start))
