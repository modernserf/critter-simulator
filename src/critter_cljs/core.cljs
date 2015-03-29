(ns ^:figwheel-always critter-cljs.core
    (:require
              [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defn init-critters []
    (let [names ["Slipper" "Allegra" "Squeaky" "Sarah Jane" "Totoro"
        ]]
        (map #(hash-map 
            :name % 
            :x (rand-int 500) 
            :y (rand-int 500)
            :velocity {:r 0 :angle 0}) 
        names)))

(defonce app-state (atom {
    :critters (init-critters)
    :width 500
    :height 500 }))

; +/- x
(defn rand-center [x] (- (rand (* 2 x)) x))
(defn mean [xs] (/ (apply + xs) (count xs)))

(defn to-random-nearby [it] 
    (let [{:keys [x y]} it]        
        (assoc it :x (+ (rand-center 10) x) 
                  :y (+ (rand-center 10) y))))

(defn mean-hash [xs xs-keys]    
    (reduce 
        (fn [coll k] (assoc coll k (mean (map k xs)))) 
        {} xs-keys))

(defn center-of-points [ps]
    (mean-hash ps [:x :y]))

(defn average-velocity [cs]
    (mean-hash (map :velocity cs) [:r :angle]))

(defn to-point-fn [f] (fn [a b] 
    (assoc a :x (f (:x a) (:x b))
             :y (f (:y a) (:y b)))))

(defn polar->cartesian [{:keys [r angle]}]
    {:x (* r (Math/cos angle))
     :y (* r (Math/sin angle))})

(defn cartesian->polar [{:keys [x y]}]
    {:r     (Math/sqrt (+ (* x x) (* y y)))
     :angle (Math/atan2 y x)})

(def add-point (to-point-fn +))
(def subtract-point (to-point-fn -))
(def gt-point (to-point-fn >))

(defn abs-point [a] 
    (assoc a :x (Math/abs (:x a))
             :y (Math/abs (:y a))))

(defn move-towards [r start end]
    (let [  route       (cartesian->polar (subtract-point start end))
            velocity    (assoc route :r (min (:r route) r))
            with-v      (assoc start :velocity velocity)
            diff        (polar->cartesian velocity)]
        (subtract-point with-v diff)))

(defn critter-collision? [c1 c2]
    (let [  diff            (abs-point (subtract-point c1 c2))
            space           {:x 40 :y 40}
            {:keys [x y]}   (gt-point space diff)]
        (and (not= c1 c2) (and x y))))

(defn exclude [x xs] (filter #(not= % x) xs))

(defn rule-to-center [c cs]
    (let [  other-critters  (exclude c cs)
            other-center    (center-of-points other-critters)]
        (move-towards 5 c other-center)))

(defn rule-avoid-walls [c width height]
    (let [  {:keys [x y]}   c]
        (cond   (< x 0)         (assoc c :x 0)
                (< y 0)         (assoc c :y 0)
                (< width x)     (assoc c :x width)
                (< height y)    (assoc c :y height)
                :else c)))

(defn rule-avoid-collisions [c cs]
    (let [  collisions      (filter #(critter-collision? % c) cs)
            collision-area  (center-of-points collisions)
            no-collision?   (empty? collisions)]
        (cond   
            no-collision? c
            :else   (move-towards -10 c collision-area))))

(defn rule-match-velocity [c cs]
    (let [  other-critters  (exclude c cs)
            avg-velocity    (average-velocity other-critters)
            r               (:r avg-velocity)
            velocity        (assoc avg-velocity :r (/ r 4))
            with-v          (assoc c :velocity velocity)
            diff            (polar->cartesian velocity)]
        (subtract-point with-v diff)))

(defn critter-destination [c state]
    (let [  cs  (:critters state)]
        (-> c
            (rule-to-center cs)
            (rule-avoid-walls (:width state) (:height state))
            (rule-avoid-collisions cs)
            (rule-match-velocity cs)
            )))


(defn move-critters! [state] 
    (let [  {:keys [width height critters]} state
            next-critters (map #(critter-destination % state) critters)]
        (swap! app-state assoc :critters next-critters)))

(defn app-loop! [] 
    (js/setTimeout (fn [] 
        (move-critters! @app-state)
        (app-loop!)) 
    100))




(defn translate [x y] (str "translate(" x "px," y "px)"))
(defn wrap-map [f xs] 
    (map-indexed (fn [idx it] [:g {:key idx} [f it]]) xs))


(defn module-critter [critter] 
    (let [{:keys [x y]} critter]
        [:g.module-critter 
            {:style {:transition "transform 500ms"
                     :transform (translate x y)}}
            [:circle {:r 20 :style {:fill "brown"}}]]))

(defn module-critter-pen []
    (let [{:keys [width height critters]} @app-state]
        [:svg.module-critter-pen 
            {:width width :height height}
            [:rect {:width width :height height 
                :style {:fill "gray"}}]
            [:g.critters (wrap-map module-critter critters)]]))

(defn module-app-root []
    [:section.module-app-root
        [module-critter-pen]])

(reagent/render-component [module-app-root]
                          (. js/document (getElementById "app")))

(app-loop!)


