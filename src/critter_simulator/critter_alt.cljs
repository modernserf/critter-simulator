(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan <! >! put!]]
            [critter-simulator.point :as point]))


; message format
; [:name {params}]

; move behavior


; TODO: update based on last timestamp?
(defn next-state [state params]
  (let [s (merge @state params)
        next-pos (point/add
                   (:position s)
                   (point/polar->cartesian {:r (:velocity s)
                                            :angle (:bearing s)}))]
    (assoc s :position next-pos)))


(defn set-state! [c next-state]
  (swap! (:state c) merge next-state))

(defn move [c]
  (let [ch (chan)
        parent-ch (:channel c)]
    (go (while true
          (let [[msg state] (<! ch)]
            (case msg
              :state  (>! parent-ch [:set (next-state state nil)])
              :collision
                (let [s @state]
                    (>! parent-ch
                        [:set (next-state
                                state
                                {:bearing   (+ 1 (:bearing s))
                                 :velocity  10})
                         ])
                    (>! ch [:state state]))
              :default ))))
    ch))

(def default-props
  { :color [:white :white :white]})

(defn init-props [props env]
  (merge default-props props))

(defn init-state [props env]
  (atom {:position (point/random env)
   :bearing (rand (* 2 Math/PI))
   :velocity 0
   :hungry 0
   :cowardly 0
   :lonely 0 }))

(defn get-state [c key] (-> c :state deref key))

(defn position [c] (get-state c :position))
(defn bearing [c] (get-state c :bearing))
(defn velocity [c] (get-state c :velocity))

(defrecord Critter [name props state channel])

(defn make [[name props] env]
  (let [ch (chan 5)
        out-ch (:channel env)
        c (Critter. name
                    (init-props props env)
                    (init-state props env)
                    ch)
        mv (move c)]
    (put! mv [:state (:state c)])
    (go (while true
          (let [[msg data] (<! ch)]
            (case msg
              :set
                (do
                  (set-state! c data)
                  (>! out-ch [:critter c]))
              :default))
          ))
    c))

(defn make-list [cs env]
  (let [vs (map #(make % env) cs)]
    (reduce #(assoc %1 (:name %2) %2) {} vs)))

