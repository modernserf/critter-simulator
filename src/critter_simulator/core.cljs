(ns ^:figwheel-always critter-simulator.core
    (:require
              [reagent.core                 :as reagent   :refer [atom]]
              [critter-simulator.behavior   :as behavior]
              [critter-simulator.point      :as point]
              [critter-simulator.critter    :as critter]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

; TODO: handle behaviors that change environment e.g. (eating, pooping)
(def critter-default-behaviors
  [behavior/lonely
   behavior/hungry
   behavior/afraid
   behavior/collision
   behavior/boundaries])

(def base-critters
  [["Slipper"     :hungry   {:color [:black :white :orange]}]
   ["Allegra"     :cowardly {:color [:black :orange :white]}]
   ["Totoro"      :friendly :hungry  {:color [:white :black :black]}]
   ["Squeaky"     :cowardly :orange]
   ["Sarah Jane"  :hungry   :cowardly :black]
   ["Gizmo"       :hungry   :orange]
   ["Twitch"      :cowardly :black]])

(def app-state
  (let [env       {:width 500
                   :height 500
                   :mouse nil
                   :behaviors critter-default-behaviors}
        critters  (critter/make-list base-critters env)]
    (atom (assoc env :critters critters))))

(defn trunc [[x y]] [(Math/round x) (Math/round y)])

(defn critter-report [c]
  (str (:name c) " " (:state c)  "\n"))

(defn move-critters! [env]
  (let [next-critters (map #(critter/next-position (critter/do-behaviors % env))
                           (:critters env))]
    ; (println (map critter-report next-critters))
    (swap! app-state assoc :critters next-critters)))

(defn app-loop! []
  (js/setTimeout (fn []
                   (move-critters! @app-state)
                   (app-loop!))
                 100))

(defn translate [x y] (str "translate(" x "px," y "px)"))
(defn wrap-map [f xs & args]
    (map-indexed (fn [idx it] [:g {:key idx} [apply f it args]]) xs))

(defn on-mouse-move [e]
    (let [  x   (.-clientX e)
            y   (.-clientY e)]
      (swap! app-state assoc :mouse [x y])))

(def pi Math/PI)

(def d-up       0)
(def d-right    (* pi .5))
(def d-down     pi)
(def d-left     (* pi 1.5))

(defn bearing->rotate [b]
  (str "rotate(" b "rad)"))

(defn module-critter [c]
  (let [[x y] (:position c)
        [head torso butt] (-> c :props :color)
        b  (critter/bearing c)]
    [:g
      [:g.module-critter {:style {:transition "transform 100ms"
                                 :transform (translate x y)}}
       [:g.critter-inner {:style {:transition "transform 100ms"
                                  :transform (bearing->rotate b)}}
         [:circle {:r 5 :cy 5  :style {:fill butt}}]
         [:circle {:r 5 :cy -5 :style {:fill head}}]
         [:rect {:x -5 :y -5 :width 10 :height 10 :style {:fill torso}}]]]
     ]
    ))

(defn module-critter-pen [env]
  (let [{:keys [width height critters]} env]
    [:svg.module-critter-pen {:width width
                              :height height
                              :on-mouse-move on-mouse-move}
      [:rect {:width width
              :height height
              :style {:fill "gray"}}]
      [:g.critters (wrap-map module-critter critters)]]))

(defn module-app-root []
  [:section.module-app-root
    [module-critter-pen @app-state]])

(reagent/render-component [module-app-root]
                          (. js/document (getElementById "app")))

(defonce do-app-loop (app-loop!))

