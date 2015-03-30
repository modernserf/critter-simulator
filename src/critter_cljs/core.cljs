(ns ^:figwheel-always critter-cljs.core
    (:require
              [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

; point: [x y]
(defn random-point [env]
  [(rand-int (:width env)) (rand-int (:height env))])


; increment/decrement state counter k
; TODO: increment based on time elapsed vs 1-per-round
(defn inc-state [c k] (update-in c [:state k] inc))
; decrement until 0
(defn dec-0 [x] (max 0 (dec x)))
(defn dec-state [c k] (update-in c [:state k] dec-0))

(defn at-threshold? [c prop] 
  (let [threshold (- 10 (-> c :props prop))
        value     (-> c :state prop)]
    (>= value threshold)))

(defn behavior [k pred-recover? fn-threshold]
  (fn [c env]
    (cond (pred-recover? c env) (dec-state    c k)
          (at-threshold? c k)   (fn-threshold c env)
          :else                 (inc-state    c k))))

(defn offset [[x1 y1] [x2 y2]] [(- x1 x2) (- y1 y2)])
(defn add-point [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])
(defn distance' [[x y]] (Math/sqrt (+ (* x x) (* y y))))
(defn bearing' [[x y]] (Math/atan2 (- y) x))

(defn cartesian->polar [c]
  {:r     (distance' c)
   :angle (bearing' c)})

(defn polar->cartesian [{:keys [r angle]}]
    [(* r (Math/cos angle))
     (- (* r (Math/sin angle)))])

(defn distance [a b] (distance' (offset a b)))
(defn bearing [a b] (bearing' (offset a b)))

(defn set-destination [c v dest]
  (let [pos   (:position c)
        dist  (distance pos dest)]
    (cond (>= v 0)  (assoc c  :velocity v
                              :destination dest)
          :else     (assoc c  :velocity (- v)
                              :destination ))))

(defn set-destination [c v dest]
  (cond (>= v 0)  (assoc c  :velocity v :destination dest)
        :else     (let [pos   (:position c)
                        p     (cartesian->polar (offset pos dest))
                        p'    (update-in p [:angle] #(+ Math/PI))
                        o'    (polar->cartesian p')
                        dest' (add-point pos o')]
                    (assoc c :velocity    (- v)
                             :destination dest'))))

(defn find-closest-critter [c cs]
  (let [pos     (:position c)
        sorted  (reduce #(assoc %1 (distance pos (:position %2)) %2) 
                        (sorted-map)
                        (filter #(not= c %) cs))]
    (-> sorted vals first)))

(defn go-to-neighbor [c env]
  (let [closest (find-closest-critter c (:critters env))]
    (set-destination c 10 (:position closest))))

(defn near-points [a b dist] (> dist (distance a b)))

(defn critter-eq [a b]
  (= (:name a) (:name b)))

(defn near-critters? [a b distance]
  (cond (critter-eq a b) nil
        :else (near-points (:position a) (:position b) distance)))

(defn is-near-others? [c env]
  (some #(near-critters? c % 50) (:critters env)))

(defn mean [xs] (/ (apply + xs) (count xs)))

(defn center-of-points [ps]
  [(mean (map first ps)) (mean (map second ps))])

; TODO
(defn is-eating? [c env] true)
(defn go-to-food [c env] c)

(defn is-away-from-cursor? [c env] true)
(defn run-away [c env] c)
; 

(def behavior-lonely (behavior :lonely is-near-others?      go-to-neighbor))
(def behavior-hungry (behavior :hungry is-eating?           go-to-food))
(def behavior-afraid (behavior :afraid is-away-from-cursor? run-away)) 

(defn behavior-collision [c env]
  (let [collisions      (filter #(near-critters? c % 10) (:critters env))
        collision-area  (center-of-points (map :position collisions))]
    
    (cond (seq collisions) (set-destination c 10 (random-point env))
          :else c)))

(defn clamp [val' min' max'] (min max' (max min' val')))

(defn behavior-boundaries [c {:keys [width height]}]
  (set-destination c 10 [(clamp (-> c :destination first) 0 width)
                         (clamp (-> c :destination second) 0 height)]))

; TODO: handle behaviors that change environment e.g. (eating, pooping)
(def critter-default-behaviors
  [
   behavior-lonely
   ; behavior-hungry
   ; behavior-afraid
   behavior-collision
   behavior-boundaries
   ])

; critters
(def base-critters 
  [["Slipper"     :hungry   {:color [:black :white :orange]}]
   ["Allegra"     :cowardly {:color [:black :orange :white]}]
   ["Totoro"      :friendly :hungry  {:color [:white :black :black]}]
   ["Squeaky"     :cowardly :orange]
   ["Sarah Jane"  :hungry   :cowardly :black]
   ["Gizmo"       :hungry   :orange]
   ["Twitch"      :cowardly :black]])

; color is triple of head / torso / body
(defn make-colors [color] {:color [color color color]})

(def init-critter-state {:hungry 0 :lonely 0 :afraid 0 :bowel 0 })

(def critter-props
  {:default   {:hungry 5 :afraid 5 :lonely 5 :color (make-colors :white)}
   :hungry    {:hungry 7}
   :cowardly  {:afraid 7}
   :friendly  {:afraid 2 :lonely 7}
   :black     (make-colors :black)
   :orange    (make-colors :orange)})

(defrecord Critter [name props state position destination velocity behaviors])

(defn make-critter [name props env]
  (->Critter name 
             props 
             init-critter-state 
             (random-point env)
             (random-point env)
             0
             critter-default-behaviors))

(defn make-props [options]
  (reduce #(merge %1 (cond (keyword? %2) (%2 critter-props) :else %2))
          (:default critter-props) 
          options))

(defn init-critters [env]
  (map #(make-critter (first %) (make-props (rest %)) env) 
       base-critters))

(defn critter-do-behaviors [c env]
  (reduce #(%2 %1 env) c (:behaviors c)))

(defn critter-next-position [c]
  (let [pos   (:position c)
        dest  (:destination c)
        p     (cartesian->polar (offset dest pos))
        vel   (min (:r p) (:velocity c))
        o'    (polar->cartesian (assoc p :r vel))
        ; vel' (/ 100 (:velocity c))
        ; o     (offset dest pos)
        ; o'    [(/ (first o) vel') (/ (second o) vel')]
        pos'  (add-point pos o')
        ]
    (assoc c  :position pos')))

(defonce app-state
  (let [env       {:width 500 :height 500 :mouse nil}
        critters  (init-critters env)]
    (atom (assoc env :critters critters))))

(defn trunc [[x y]] [(Math/round x) (Math/round y)])

(defn critter-report [c]
  (str (:name c) " " (:state c)  "\n"))

(defn move-critters! [env] 
  (let [next-critters (map #(critter-next-position (critter-do-behaviors % env)) 
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
        (swap! app-state assoc :mouse { :x x :y y})))

(defn critter-bearing [c]
  (bearing (:position c) (:destination c) ))

(defn module-critter [critter] 
  (let [[x y] (:position critter)
        [head torso butt] (-> critter :props :color)
        b  (critter-bearing critter)]
    [:g.module-critter {:style {:transition "transform 100ms"
                                :transform (translate x y)}}
      [:g.critter-inner {:style {:transition "transform 100ms"
                                 :transform (str "rotate(" b "rad)")}}
        [:circle {:r 5 :cx 5  :style {:fill butt}}] 
        [:circle {:r 5 :cx -5 :style {:fill head}}]
        [:rect {:x -5 :y -5 :width 10 :height 10 :style {:fill torso}}]]]))

(defn module-critter-pen []
    (let [{:keys [width height critters]} @app-state]
        [:svg.module-critter-pen 
            {:width width 
             :height height
             :on-mouse-move on-mouse-move}
            [:rect {:width width :height height 
                :style {:fill "gray"}}]
            [:g.critters (wrap-map module-critter critters)]]))

(defn module-app-root []
    [:section.module-app-root
        [module-critter-pen]])

(reagent/render-component [module-app-root]
                          (. js/document (getElementById "app")))

(defonce do-app-loop (app-loop!))


