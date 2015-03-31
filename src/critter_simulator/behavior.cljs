(ns ^:figwheel-always critter-simulator.behavior
    (:require
              [critter-simulator.point   :as point]
              [critter-simulator.critter :as critter]))

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

(defn find-closest-critter [c cs]
  (let [pos     (:position c)
        sorted  (reduce #(assoc %1 (point/distance pos (:position %2)) %2)
                        (sorted-map)
                        (filter #(not= c %) cs))]
    (-> sorted vals first)))

(defn go-to-neighbor [c env]
  (let [closest (find-closest-critter c (:critters env))]
    (critter/set-destination c 10 (:position closest))))

(defn near-critters? [a b dist]
  (cond (critter/eq? a b) nil
        :else (point/near? (:position a) (:position b) dist)))

(defn is-near-others? [c env]
  (some #(near-critters? c % 50) (:critters env)))

; TODO
(defn is-eating? [c env] true)
(defn go-to-food [c env] c)
;

(defn is-away-from-cursor? [c env]
  (not (point/near? (:position c) (:mouse env) 100)))

(defn run-away [c env]
  (critter/set-destination c -20 (:mouse env)))

(def lonely (behavior :lonely is-near-others?      go-to-neighbor))
(def hungry (behavior :hungry is-eating?           go-to-food))
(def afraid (behavior :afraid is-away-from-cursor? run-away))

(defn collision [c env]
  (let [collisions      (filter #(near-critters? c % 20) (:critters env))
        collision-area  (point/center-of (map :position collisions))]
    ; TODO: should critters continue towards their destination when they bump?
    (cond (and (seq collisions) #_(critter/at-rest? c))
            (critter/set-destination c -10 collision-area)
          :else c)))

(defn clamp [val' min' max'] (min max' (max min' val')))

(defn boundaries [c {:keys [width height]}]
  (critter/set-destination
    c 10 [(clamp (-> c :destination first) 5 (- width 5))
          (clamp (-> c :destination second) 5 (- height 5))]))