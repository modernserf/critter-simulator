(ns ^:figwheel-always critter-simulator.core
    (:require
        [reagent.core                 :as reagent   :refer [atom]]
        [critter-simulator.util.style               :refer [style]]
        [critter-simulator.behavior.defaults   :refer [critter-default-behaviors]]
        [critter-simulator.point      :as point]
        [critter-simulator.critter    :as critter]
        [critter-simulator.food       :as food]
        [critter-simulator.views.core :as views]))

(enable-console-print!)

(def base-critters
  [["Claritin"    :hungry   {:color [:black :white :orange]}]
   ["Allegra"     :cowardly {:color [:black :orange :white]}]
   ["Totoro"      :friendly :hungry  {:color [:white :black :black]}]
   ["Squeaky"     :cowardly :orange]
   ["Sarah Jane"  :hungry   :cowardly :black]
   ["Gizmo"       :hungry   {:color [:white :orange :orange]}]
   ["Twitch"      :cowardly :black]
   ; ["Professor Popcorn" :hungry {:color [:orange :white :white]}]
   ; ["Jareth"      :hungry :cowardly :orange]
   ["Onigiri"     :cowardly {:color [:black :white :white]}]
   ["Pui Pui"     :hungry   {:color [:orange :white :white]}]
])

(def app-state
  (let [env   {:width 600
               :height 700
               :mouse nil
               :poops []
               :behaviors critter-default-behaviors}]
    (atom (assoc env :critters (critter/make-list base-critters env)
                     :food     (food/make env)))))

(swap! app-state assoc :do-poop 
       (fn [c]
         "receive poop"
         (swap! app-state update-in [:poops] conj (:position c))))

(defn trunc [[x y]] [(Math/round x) (Math/round y)])

(defn critter-report [c]
  (str (:name c) " " (:state c)  "\n"))

(defn move-critters! [env]
  (let [next-critters (map #(critter/next-position (critter/do-behaviors % env))
                           (:critters env))]
    (swap! app-state assoc :critters next-critters)))

(defn app-loop! []
  (js/setTimeout (fn []
                   (move-critters! @app-state)
                   (app-loop!))
                 100))

(reagent/render-component [views/module-app-root app-state]
                          (. js/document (getElementById "app")))

(defonce do-app-loop (app-loop!))
