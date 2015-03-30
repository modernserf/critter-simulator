(ns ^:figwheel-always critter-simulator.test
  (:require
            [critter-simulator.test.point]
            [cljs.test :refer-macros [run-tests]]))

(cljs.test/run-tests 'critter-simulator.test.point)