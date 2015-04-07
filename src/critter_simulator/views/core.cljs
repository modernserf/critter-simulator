(ns ^:figwheel-always critter-simulator.views.core
    (:require
        [critter-simulator.util.style     :refer [style]]))

(defn translate [x y] (str "translate(" x "px," y "px)"))
(defn wrap [tag f xs & args]
    (map-indexed (fn [idx it] [tag {:key idx} [apply f it args]]) xs))

(defn on-mouse-move [e env]
  (if e
    (let [x   (.-clientX e)
          y   (.-clientY e)]
      (swap! env assoc :mouse [x y]))
    (swap! env assoc :mouse nil)))

(defn on-status-hover [c env]
    (swap! env assoc :selected-critter c))

(defn is-selected? [c env]
    (= c (:selected-critter env)))

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
                 :opacity 0.3}}
      (status-emoji k)])

(defn module-critter-status [c env]
  [:div.module-critter-status
      {:on-mouse-enter #(on-status-hover c env)
       :on-mouse-leave #(on-status-hover nil env)
       :style {:cursor :pointer}}
      [:h3 (:name c)]
      [:ul.critter-stats.flex {:style {:padding-bottom 10}}
          (wrap :li module-stat @(:state c) c)]])

(defn module-critter-status-group [env]
  [:section.module-critter-status-group
      [:h1 "Critters"]
      (wrap :div module-critter-status (:critters @env) env)])

(defn module-food [f]
  [:g.module-food (style {:transform (apply translate (:position f))})
      [:circle {:r (:radius f)
                :style {:fill :green
                        :stroke :white
                        :stroke-width 2}}]])

(defn module-critter [c env]
  (let [[x y] (:position @c)
        [head torso butt] (:color @c)
        b  (:bearing @c)
        selected-ring (and (is-selected? c @env)
                           [:circle {:r 20 :style {:stroke :red
                                                   :fill :none}}])]
    [:g
        [:g.module-critter (style {:transition "transform 100ms"
                                   :transform (translate x y)})
            selected-ring
            [:g.critter-inner (style {:transition "transform 100ms"
                                      :transform (bearing->rotate b)})
                [:circle {:r 5 :cy 5  :style {:fill butt}}]
                [:circle {:r 5 :cy -5 :style {:fill head}}]
                [:rect {:x -5 :y -5 :width 10 :height 10 :style {:fill torso}}]

       ]]
     ]
    ))

(defn module-critter-pen [env]
  (let [{:keys [width height critters]} @env]
    [:svg.module-critter-pen {:width width
                              :height height
                              :on-mouse-move #(on-mouse-move % env)
                              :on-mouse-leave #(on-mouse-move nil env)}
      [:rect {:width width
              :height height
              :style {:fill "gray"}}]
      [module-food (:food @env)]
      [:g.critters (wrap :g module-critter (vals critters) env)]]))

(defn module-app-root [app-state]
  [:section.module-app-root.flex
      [module-critter-pen app-state]
      [:div {:style {:padding-left 20}}
          ; [module-critter-status-group app-state]
          ]
    ])
