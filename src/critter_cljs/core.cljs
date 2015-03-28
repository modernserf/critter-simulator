(ns ^:figwheel-always critter-cljs.core
    (:require
              [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {
    :critters [
        {:name "Slipper" :x 50 :y 50 :id 1}
        {:name "Allegra" :x 100 :y 150 :id 2}]
    :width 500
    :height 500 }))

(defn translate [x y] (str "translate(" x "," y ")"))
(defn wrap-map [f xs] 
    (map-indexed (fn [idx it] [:g {:key idx} [f it]]) xs))


(defn module-critter [critter] 
    (let [{:keys [x y]} critter]
        [:g.module-critter {:transform (translate x y)}
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


