(ns cyphy.demo-4bar-test
  (:require [midje.sweet :as tt]
            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]
            [isis.geom.visual
             [openscad :as openscad]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-constraint]
            [isis.geom.model.lower-joint :as lower-joint]
            [isis.geom.machine.misc :as misc]
            [isis.geom.algebra [geobj :as ga]]

            [isis.geom.analysis
             [position-analysis :as pa]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-dispatch]
             [helical-dispatch]
             [in-line-dispatch]
             [in-plane-dispatch]
             [offset-angle-dispatch]
             [offset-z-dispatch]
             [parallel-axis-dispatch]]))


(with-open [fis (-> "demo/four_bar_csys_a.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream fis)
        constraints-orig (:constraint kb)
        constraints-nil-patch (lower-joint/nil-patch-collection constraints-orig)
        constraints-meta (meta-constraint/expand-collection constraints-nil-patch)
        constraints-lower (lower-joint/expand-collection constraints-meta)

        choose-pair (fn [x c1 c2]
                      (let [l1 (-> x :m1 first first)
                            l2 (-> x :m2 first first) ]
                        (or (and (= l1 c1) (= l2 c2))
                            (and (= l1 c2) (= l2 c1)) ))) ]

    ;; _ (pp/pprint ["constraints-orig:" constraints-orig])

    ;; The arm2 {a93..51f} is connected to
    ;; the boom {99c..264} via a revolute joint.
    (tt/facts
     "about the parsed cad-assembly file ground (assembly)-to-carriage"
     (tt/fact "about the base link id" (:base kb) => "{ASSY}")

     (tt/fact
      "original the carriage is connected to the base assembly via three planes"
      (filterv #(choose-pair % "{ASSY}" "{BAR-1}")
               constraints-orig) =>
      [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :planar}
       {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :planar}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-meta) =>
      [{:m1 [["{BAR-1}" "CS1-origin"] {:e [50.0 0.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-origin"] {:e [-50.0 0.0 0.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-3x"] {:e [350.0 0.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-3x"] {:e [-50.0 299.99999999999994 0.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-4y"] {:e [50.0 400.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-4y"] {:e [349.9999999999999 0.0 0.0]}],
        :type :coincident}])


     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :parallel-z (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :parallel-z}
       {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :parallel-z}
       {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :parallel-z}])


     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :in-plane (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :in-plane}
       {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :in-plane}
       {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :in-plane}] )


     (tt/fact
      "fully expanded to in-line primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :in-line (:type %)) )
               constraints-lower) =>
      []) )


    (tt/facts
     "about the parsed cad-assembly file arm-to-boom"

     (tt/fact
      "original "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-orig) =>
      [{:m1 [["{BAR-1}" "CS1"] {:e [50.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{BAR-2}" "CS0"] {:e [-50.0 0.0 0.0], :pi 1.0, :q [1.0 1.0 0.0]}],
        :type :csys}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-lower) =>
      [{:m1 [["{BAR-1}" "CS1-origin"] {:e [50.0 0.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-origin"] {:e [-50.0 0.0 0.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-3x"] {:e [350.0 0.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-3x"] {:e [-50.0 299.99999999999994 0.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-4y"] {:e [50.0 400.0 0.0]}],
        :m2 [["{BAR-2}" "CS0-4y"] {:e [349.9999999999999 0.0 0.0]}],
        :type :coincident}])


    (tt/facts
     "about the parsed cad-assembly file invariants"

     (tt/fact
      "about the initial ASSY link settings"
      (-> kb :link (get "{ASSY}") deref) =>
      {:name nil
       :rdof {:# 0}, :tdof {:# 0},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}})

     (tt/fact
      "about the initial BAR-1 link settings"
      (-> kb :link (get "{BAR-1}") deref) =>
      {:name "BAR_1"
       :rdof {:# 3}, :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}})

     (tt/fact
      "about the initial BAR-2 link settings"
      (-> kb :link (get "{BAR-2}") deref) =>
      {:name "BAR_1"
       :rdof {:# 3},
       :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}})
     (tt/fact
      "about the initial BAR-3 link settings"
      (-> kb :link (get "{BAR-3}") deref) =>
      {:name "BAR_1"
       :rdof {:# 3},
       :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}})
     (tt/fact
      "about the initial BAR-4 link settings"
      (-> kb :link (get "{BAR-4}") deref) =>
      {:name "BAR_1"
       :rdof {:# 3},
       :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}})

     (tt/fact
      "about the initial marker invariants"
      (into {} (map (fn [[k v]] [k @v]) (:invar kb))) =>
      {:loc #{["{ASSY}"]}
       :dir #{["{ASSY}"]}
       :twist #{["{ASSY}"]}} )  )



    (let [result (pa/position-analysis kb constraints-lower)
          [success? result-kb result-success result-failure] result
          {result-mark :invar result-link :link} result-kb ]

      ;; (pp/pprint result-success)
      ;; (pp/pprint result-link)

     (tt/fact
      "about the initial marker invariants"
      (into {} (map (fn [[k v]] [k @v]) (:invar kb))) =>
      {:loc #{["{ASSY}"] ["{BAR-1}"] ["{BAR-2}"] ["{BAR-3}"] ["{BAR-4}"]}
       :dir #{["{ASSY}"] ["{BAR-1}"] ["{BAR-2}"] ["{BAR-3}"] ["{BAR-4}"]}
       :twist #{["{ASSY}"] ["{BAR-1}"] ["{BAR-2}"] ["{BAR-3}"] ["{BAR-4}"]} })

      (tt/fact
       "about the final ASSY link settings"
       (-> result-link (get "{ASSY}") deref) =>
       {:name nil,
        :rdof {:# 0}, :tdof {:# 0},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} )

      (tt/fact
       "about the final BAR-1 link settings"
       (-> result-link (get "{BAR-1}") deref) =>
       {:name "BAR_1",
        :rdof {:# 0}, :tdof {:# 0, :point [0.0 0.0 0.0]},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} )

      (tt/fact
       "about the final BAR-2 link settings"
       (-> result-link (get "{BAR-2}") deref) =>
       {:name "BAR_1", :rdof {:# 0}, :tdof {:# 0, :point [50.0 0.0 0.0]},
        :versor {:rotate [4.329780281177467E-17 -0.7071067811865475
                          -0.7071067811865476 -4.329780281177466E-17],
                 :xlate [50.00000000000008 49.999999999999986 0.0]}})

      (tt/fact
       "about the final BAR-3 link settings"
       (-> result-link (get "{BAR-3}") deref) =>
        {:name "BAR_1", :rdof {:# 0},
         :tdof {:# 0, :point [50.000000000000085 99.99999999999999 0.0]},
         :versor {:rotate [1.0 0.0 0.0 0.0],
                  :xlate [100.00000000000009 99.99999999999999 0.0]}})

      (tt/fact
       "about the final BAR-4 link settings"
       (-> result-link (get "{BAR-4}") deref) =>
       {:name "BAR_1", :rdof {:# 0},
        :tdof {:# 0, :point [150.00000000000009 99.99999999999999 0.0]},
        :versor {:rotate [4.329780281177467E-17 -0.7071067811865475
                          -0.7071067811865476 -4.329780281177466E-17],
                 :xlate [150.00000000000017 150.0 0.0]}})


      (tt/fact
       "about order of the constraint application in success result."
       (comment "only the names and types are important.
         we are checking the plan schedule.")
       (for [con result-success]
         {:m1 (-> con :m1 first)
          :m2 (-> con :m2 first)
          :type (:type con)})
           =>
       [{:m1 ["{BAR-1}" "FRONT"]
         :m2 ["{ASSY}" "ASM_FRONT"]
         :type :in-plane}
        {:m1 ["{BAR-1}" "FRONT"]
         :m2 ["{ASSY}" "ASM_FRONT"]
         :type :parallel-z}
        {:m1 ["{BAR-1}" "TOP"]
         :m2 ["{ASSY}" "ASM_TOP"]
         :type :parallel-z}
        {:m1 ["{BAR-1}" "RIGHT"]
         :m2 ["{ASSY}" "ASM_RIGHT"]
         :type :in-plane}
        {:m1 ["{BAR-1}" "RIGHT"]
         :m2 ["{ASSY}" "ASM_RIGHT"]
         :type :parallel-z}
        {:m1 ["{BAR-1}" "TOP"]
         :m2 ["{ASSY}" "ASM_TOP"]
         :type :in-plane}
        {:m1 ["{BAR-1}" "CS1-origin"]
         :m2 ["{BAR-2}" "CS0-origin"]
         :type :coincident}
        {:m1 ["{BAR-1}" "CS1-3x"]
         :m2 ["{BAR-2}" "CS0-3x"]
         :type :coincident}
        {:m1 ["{BAR-1}" "CS1-4y"]
         :m2 ["{BAR-2}" "CS0-4y"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS0-origin"]
         :m2 ["{BAR-2}" "CS1-origin"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS0-3x"]
         :m2 ["{BAR-2}" "CS1-3x"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS0-4y"]
         :m2 ["{BAR-2}" "CS1-4y"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS1-origin"]
         :m2 ["{BAR-4}" "CS0-origin"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS1-3x"]
         :m2 ["{BAR-4}" "CS0-3x"]
         :type :coincident}
        {:m1 ["{BAR-3}" "CS1-4y"]
         :m2 ["{BAR-4}" "CS0-4y"]
         :type :coincident}])


      (tt/fact
       "about the failure result" result-failure =>
       [] )

      (let [temp-dirs
            (filter #(.isDirectory (jio/as-file %))
                    ["/tmp" "/temp"])]
        (with-open [fis (-> "demo/four_bar_csys.xml"
                            jio/resource jio/input-stream)
                    fos (-> (first temp-dirs)
                            (jio/file "four_bar_csys_aug.xml")
                            jio/output-stream)]
          (stax/update-cad-assembly-using-knowledge fis fos kb) )

        (with-open [fos (-> (first temp-dirs)
                            (jio/file "four_bar_csys.scad")
                            jio/output-stream)]
          (openscad/write-knowledge fos kb "STL\") ) ) ))))


