(ns ^:figwheel-always critter-simulator.critter
    (:require
        [critter-simulator.point :as point]
        [critter-simulator.protocols.collidable :as collidable]))

(def collision-radius 10)

(defrecord Critter [name props state
                    position destination
                    velocity bearing
                    behaviors]
  collidable/Collidable
    (= [a b] (= (:name a) (:name b)))
    (position [self] (:position self))
    (closest-perimeter-distance [self point] collision-radius))
(def is-colliding? collidable/is-colliding?)

(defn init-critter-state [data]
  (zipmap (keys data) (map rand (vals data))))

; color is triple of head / torso / body
(defn make-colors [color] {:color [color color color]})

(def critter-props
  {:default   {:hungry 30 :afraid 0.5 :lonely 5 :bowel 5 :bored 5
               :color (make-colors :white)}
   :hungry    {:hungry 10 }
   :cowardly  {:afraid 0.2}
   :friendly  {:afraid 3 :lonely 1}
   :black     (make-colors :black)
   :orange    (make-colors :orange)})

(defn make-props [options]
  (reduce #(merge %1 (if (keyword? %2) (%2 critter-props) %2))
          (:default critter-props)
          options))



(defn make [params env]
  (let [props (make-props (rest params))]
    (Critter.  (first params) props (init-critter-state props)
             (point/random env) (point/random env)
             0 0
             (:behaviors env))))

(defn at-threshold? [c prop]
  (let [threshold (-> c :props prop)
        value     (-> c :state prop)]
    (>= value threshold)))

(defn make-list [cs env]
  (map #(make % env) cs))

(defn do-behaviors [c env]
  (reduce #(%2 %1 env) c (:behaviors c)))

(defn next-position [c]
  (let [pos   (:position c)
        dest  (:destination c)
        p     (point/cartesian->polar (point/offset pos dest))
        vel   (min (:r p) (:velocity c))
        o'    (point/polar->cartesian (assoc p :r vel))
        pos'  (point/add pos o')
        ]
    (assoc c  :position pos'
              :bearing  (if (> vel 0) (:angle p) (:bearing c)))))

(def eq? collidable/=)

(defn at-rest? [c]
  (point/near? (:position c) (:destination c) 1))

(defn bearing [c] (:bearing c) )

(defn set-destination [c v dest]
  (if (>= v 0)
    (assoc c  :velocity v :destination dest)
    (let [pos     (:position c)
          dest'   (point/alter-bearing pos dest Math/PI)]
      (assoc c  :velocity    (- v)
                :destination dest'))))

(defn alter-bearing [c bearing]
  (point/alter-bearing (:position c) (:destination c) bearing))

