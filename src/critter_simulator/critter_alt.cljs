(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! >!]]

            [critter-simulator.point :as point]))

; message format
; [:name {params}]

(defn next-pos [c {:keys [position velocity bearing]}]
  
  (point/add
    position
    (point/polar->cartesian {:r velocity
                             :angle bearing})))

(defn body [self ch-out]
  (let [ch-in (async/chan)]
    (go-loop [[msg params] (<! ch-in)]
      (let []
        (case msg
          :move (>! ch-out [:critter-move
                            self
                            (assoc params :position (next-pos self params))]))
        (recur (<! ch-in))))
    ch-in))

(defn avoid-collision [self ch-out]
  (let [ch-in (async/chan)]
    (go-loop []
      (let [[msg params] (<! ch-in)]
        (case msg
          :collision (>! ch-out [:move (update-in params [:bearing] + 1.5)])
          :default)
        (recur)))
    ch-in))

(defn executive [ch-in self ch-world]
  (let [ch-body  (body self ch-world)
        ch-coll  (avoid-collision self ch-body)]
    (go-loop [[msg state] (<! ch-in)]
      (case msg
        :collision (>! ch-coll [:collision state])
        :ok (>! ch-body [:move state]))
        (recur (<! ch-in)))
    ch-in))

(defn init-state [name props env]
  (atom (merge
          {:color [:white :white :white]
           :name name
           :position (point/random env)
           :bearing (rand (* 2 Math/PI))
           :velocity 10
           :hungry 0
           :cowardly 0
           :lonely 0 }
          props)))

(defrecord Critter [state channel])

(defn make [[name props] env]
  (let [ch (async/chan)
        c (Critter. (init-state name props env) ch)
        world-ch (:channel env)
        exec-ch (executive ch c world-ch)]
    (go (>! exec-ch [:ok @(:state c)]))
    c))

(defn make-list [cs env]
  (let [vs (map #(make % env) cs)]
    (reduce #(assoc %1 (:name @(:state %2)) (:state %2)) {} vs)))

