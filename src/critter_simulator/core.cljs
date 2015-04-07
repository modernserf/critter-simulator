(ns ^:figwheel-always critter-simulator.core
    (:require
        [reagent.core                 :as reagent   :refer [atom]]
        [critter-simulator.util.style               :refer [style]]
        [critter-simulator.behavior.defaults   :refer [critter-default-behaviors]]
        [critter-simulator.point      :as point]
        [critter-simulator.critter-alt    :as critter]
        [critter-simulator.food       :as food]
        [critter-simulator.views.core :as views]))

(enable-console-print!)

(def base-critters
  [["Slipper"    {:color [:black :white :orange]}]
   ["Allegra"    {:color [:black :orange :white]}]
   ["Totoro"     {:color [:white :black :black]}]
   ["Squeaky"    {:color [:orange :orange :orange]}]
   ["Sarah Jane" {:color [:black :black :black]}]
   ["Gizmo"      {:color [:white :orange :orange]}]
   ["Twitch"     {:color [:black :black :black]}]
   ["Professor Popcorn" {:color [:orange :white :white]}]
   ["Jareth"      {:color [:orange :orange :orange]}]
   ["Onigiri"     {:color [:black :white :white]}]
   ["Pui Pui"     {:color [:orange :white :white]}]
])

(def app-state
  (let [env   {:width 600
               :height 700
               :mouse nil
               :behaviors critter-default-behaviors}]
    (atom (assoc env :critters (critter/make-list base-critters env)
                     :food     (food/make env)))))

; (defn move-critters! [env]
;   (let [next-critters (map #(critter/next-position (critter/do-behaviors % env))
;                            (:critters env))]
;     ; (println (map critter-report next-critters))
;     (swap! app-state assoc :critters next-critters)))





(defn render []
  (reagent/render-component [views/module-app-root app-state]
                            (. js/document (getElementById "app"))))

(defn app-loop! []
  (js/setTimeout (fn []
                   (doall (map critter/execute! (:critters @app-state)))
                   (render)
                   (app-loop!))
                 100))

#_(defonce do-app-loop (app-loop!))
