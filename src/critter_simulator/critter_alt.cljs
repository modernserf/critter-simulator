(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan <! >!]]
            [critter-simulator.point :as point]))


; message format
; [:name {params}]

; move behavior
(defn move [reply-ch]
  (let [ch (chan)]
    (go (while true
          (let [[msg params] (<! ch)
                bearing (:bearing params)]
            (case msg
              :collision (>! reply-ch [:set {:bearing  (+ 1 bearing)
                                             :velocity 10}])
              :default))))))







(def default-props
  { :color [:white :white :white]})

(defn init-props [props env]
  (merge default-props props))

(defn init-state [props env]
  (atom {:position (point/random env)
   :bearing (rand (* 2 Math/PI))
   :velocity 10
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
        c (Critter. name (init-props props env) (init-state props env) ch)]
    ; TODO: how to maintain critter identity/ connection to channel
    c))

(defn make-list [cs env] (map #(make % env) cs))

(defn execute! [c]
  (let [next-pos (point/add
                   (position c)
                   (point/polar->cartesian {:r (velocity c)
                                            :angle (bearing c)}))]
    (swap! (:state c) assoc :position next-pos)))
