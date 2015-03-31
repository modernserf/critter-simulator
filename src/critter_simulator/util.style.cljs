(ns ^:figwheel-always critter-simulator.util.style
  (:require [clojure.set    :refer [intersection]]
            [clojure.string :refer [replace]]))

(def safari-unsafe
  #{:transform :transform-origin :transform-function :transform-style
    :flex-direction :flex-wrap :flex-flow
    :justify-content :align-items :align-content
    :flex :flex-grow :flex-shrink :flex-basis :align-self})

; for items in hash-map m, if key in safari-unsafe,
; add prefixed version to m

(defn prefix-webkit [c k] (assoc c (keyword (str "-webkit-" (name k))) (c k)))
(defn unsafe-keys [m] (intersection  safari-unsafe (-> m keys set)))

(defn style [m]
  (let [safe   (reduce prefix-webkit m (unsafe-keys m))
        tx     (:transition safe)]
    {:style (if tx
                (assoc safe :-webkit-transition
                                (replace tx #"transform" "-webkit-transform"))
                safe)}))