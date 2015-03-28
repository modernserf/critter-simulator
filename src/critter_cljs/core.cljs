(ns ^:figwheel-always critter-cljs.core
    (:require
              [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defn init-critters []
    (let [names ["Slipper" "Allegra" "Squeaky" "Sarah Jane" "Totoro"]]
        (map #(hash-map :name % :x (rand-int 500) :y (rand-int 500)) names)))

(defonce app-state (atom {
    :critters (init-critters)
    :width 500
    :height 500 }))

; +/- x
(defn rand-center [x] (- (rand (* 2 x)) x))
(defn mean [xs] (/ (apply + xs) (count xs)))

(defn to-random-nearby [it] 
    (let [{:keys [x y]} it]        
        (assoc it :x (+ (rand-center 5) x) 
                  :y (+ (rand-center 5) y))))

(defn center-of-points [ps] 
    {:x (mean (map :x ps))
     :y (mean (map :y ps))})

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

(defn abs-point [a] 
    assoc a :x (Math/abs (:x a))
            :y (Math/abs (:y a)))

(defn move-towards [r start end]
    (let [angle (:angle (cartesian->polar (subtract-point start end)))
          diff (polar->cartesian {:angle angle :r r})]
        (subtract-point start diff)))

(defn move-critters! [] 
    (let [{:keys [width height critters]} @app-state
          critter-target (center-of-points critters)
          move-towards-target #(move-towards 10 % critter-target)
          next-critters (map move-towards-target critters)]
        (swap! app-state assoc :critters next-critters)))

(defn app-loop! [] 
    (js/setTimeout (fn [] 
        (move-critters!)
        (app-loop!)) 
    100))


(defn translate [x y] (str "translate(" x "px," y "px)"))
(defn wrap-map [f xs] 
    (map-indexed (fn [idx it] [:g {:key idx} [f it]]) xs))


(defn module-critter [critter] 
    (let [{:keys [x y]} critter]
        [:g.module-critter 
            {:style {:transition "transform 100ms"
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


