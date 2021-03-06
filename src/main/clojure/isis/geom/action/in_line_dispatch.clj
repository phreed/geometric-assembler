(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-line-o2p-slice :as o2p]
             [in-line-p2o-slice :as p2o]]
            [clojure.pprint :as pp]))

(defn the-alias [name] (get (ns-aliases *ns*) name))

(defn precondition
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb point line]
  (cond (invariant/marker-position? kb line)  [kb point line :p2o]
        (and (invariant/marker-position? kb point)
             (invariant/marker-direction? kb point)) [kb point line :o2p]
        :else nil))


(defn assemble-dispatch
  [kb point line motive]
  (let [[[link-name _] _] (case motive
                            :o2p line
                            :p2o point)
        link @(get (:link kb) link-name)
        tdof (get-in link [:tdof :#])
        rdof (get-in link [:rdof :#]) ]
    #_(pp/pprint ["in-line assemble!" (str tdof ":" rdof "-" motive)
                  "point" point "line" line])
    {:tdof tdof :rdof rdof :motive motive}))

(defmulti assemble!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  assemble-dispatch
  :default nil)


(defmethod ms/constraint-attempt?
  :in-line
  [kb constraint]
  (let [{point :m1 line :m2} constraint
        precon (precondition kb point line) ]
    (if-not precon
      :pre-condition-not-met
      (let [[kb+ point line motive] precon]
        (try
          (ms/show-constraint kb+ "in-line"
                              assemble-dispatch
                              point line motive)
          (assemble! kb point line motive)
          (catch Exception ex
            (ms/dump ex assemble-dispatch
                     "in-line" kb+ point line motive)
            :exception-thrown))))))


(ms/defmethod-transform assemble! {:o2p 'o2p :p2o 'p2o})

