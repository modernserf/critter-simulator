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
  [
   behavior/lonely
   behavior/hungry
   behavior/bored
   behavior/afraid
   behavior/collision
   behavior/boundaries
   ])

(def base-critters
  [["Slipper"     :hungry   {:color [:black :white :orange]}]
   ["Allegra"     :cowardly {:color [:black :orange :white]}]
   ["Totoro"      :friendly :hungry  {:color [:black :black :white]}]
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
(defn wrap [tag f xs & args]
    (map-indexed (fn [idx it] [tag {:key idx} [apply f it args]]) xs))

(defn on-mouse-move [e]
    (let [  x   (.-clientX e)
            y   (.-clientY e)]
      (swap! app-state assoc :mouse [x y])))

(defn on-status-hover [c]
    (swap! app-state assoc :selected-critter c))

(defn is-selected? [c env]
    (critter/eq? c (:selected-critter env)))

(defn bearing->rotate [b]
  (str "rotate(" b "rad)"))

(def status-emoji {:hungry  "🍕"
                   :lonely  "😢"
                   :afraid  "😱"
                   :bowel   "💩"
                   :bored   "😒"})

(defn module-stat [[k v] c]
  [:div {:style {:padding 10
                 :transition "opacity 100ms"
                 :opacity (if (critter/at-threshold? c k) 1 0.3)}}
      (status-emoji k)])

(defn module-critter-status [c]
  [:div.module-critter-status
      {:on-mouse-enter #(on-status-hover c)
       :on-mouse-leave #(on-status-hover nil)
       :style {:cursor :pointer}}
      [:h3 (:name c)]
      [:ul.critter-stats {:style {:display :flex
                                  :padding-bottom 10}}
          (wrap :li module-stat (:state c) c)]])

(defn module-critter-status-group [env]
  [:section.module-critter-status-group
      [:h1 "Critters"]
      (wrap :div module-critter-status (:critters env))])

(defn module-critter [c env]
  (let [[x y] (:position c)
        [head torso butt] (-> c :props :color)
        b  (critter/bearing c)
        selected-ring (and (is-selected? c env)
                           [:circle {:r 20 :style {:stroke :red
                                                   :fill :none}}])]
    [:g
        [:g.module-critter {:style {:transition "transform 100ms"
                                    :transform (translate x y)}}
            selected-ring
            [:g.critter-inner {:style {:transition "transform 100ms"
                                       :transform (bearing->rotate b)}}
                [:circle {:r 5 :cy 5  :style {:fill butt}}]
                [:circle {:r 5 :cy -5 :style {:fill head}}]
                [:rect {:x -5 :y -5 :width 10 :height 10 :style {:fill torso}}]

       ]]
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
      [:g.critters (wrap :g module-critter critters env)]]))



(defn module-app-root []
  [:section.module-app-root {:style {:display :flex}}
      [module-critter-pen @app-state]
      [:div {:style {:padding-left 20}}
          [module-critter-status-group @app-state]]

    ])

(reagent/render-component [module-app-root]
                          (. js/document (getElementById "app")))

(defonce do-app-loop (app-loop!))

