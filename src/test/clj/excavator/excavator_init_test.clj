(ns excavator.excavator-init-test
  "Sample assembly consisting of a boom and a dipper."
  (:require [expectations :refer :all]
            [isis.geom.lang.cyphy-cad-zipper :as cyphy]

            [clojure.java.io :as jio]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as zx]

            [isis.geom.analysis
             [position-analysis
              :refer [enable-trace! position-analysis]]]

            [isis.geom.machine
             [misc :as misc]
             [tolerance :as tol]]

            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))




(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))


(let [_ (println "excavator init test : constraints")
      excavator-graph
      (with-open [is (-> "excavator/cad_assembly_boom_dipper.xml"
                         jio/resource jio/input-stream)]
        (cyphy/graph-from-cyphy-input-stream is))]
  (expect
   '[
     {:type :coincident
      :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"] {:e [1.0 0.0 0.0]}]
      :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_FRONT"] {:e [1.0 0.0 0.0]}]}
     {:type :coincident
      :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"] {:e [0.0 1.0 0.0]}]
      :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_TOP"] {:e [0.0 1.0 0.0]}]}
     {:type :coincident
      :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"] {:e [0.0 0.0 1.0]}]
      :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_RIGHT"] {:e [0.0 0.0 1.0]}]}
     {:type :coincident
      :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_2"] {:e [-8649.51 4688.51 600.0]}]
      :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT2"] {:e [3467.85 43.0687 302.5]}]}
     {:type :coincident
      :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_1"] {:e [-8625.71 4720.65 570.0]}]
      :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT1"] {:e [3455.57 5.0 332.5]}]}
     {:type :coincident
      :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_0"] {:e [-8625.71 4720.65 600.0]}]
      :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT0"] {:e [3455.57 5.0 302.5]}]}]
   (:constraint excavator-graph)))



(let [_ (println "excavator init test : position analysis")
      graph
      (with-open [is (-> "excavator/cad_assembly_boom_dipper.xml"
                         jio/resource jio/input-stream)]
        (cyphy/graph-from-cyphy-input-stream is))

      mark-pattern
      '{:loc [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                     ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                     ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]
        :z [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                   ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                   ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]
        :x [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                   ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                   ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]}

      link-pattern
      '{
        "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"
        [:ref {:versor {:xlate [0.0 0.0 0.0]
                        :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0} :rdof {:# 0}}]
        "{bb160c79-5ba3-4379-a6c1-8603f29079f2}"
        [:ref {:versor {:xlate [0.0 0.0 0.0]
                        :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0 :point [1.0 0.0 0.0]}
               :rdof {:# 0}}]
        "{62243423-b7fd-4a10-8a98-86209a6620a4}"
        [:ref {:versor {:xlate [12315.409884054143
                                -4259.980462767625
                                903.0008418651848]
                        :rotate [4.9967957578778144E-5
                                 -0.8894325067015925
                                 0.4570665300942336
                                 -2.435861248047122E-5]}
               :tdof {:# 0, :point [3467.8500000000004
                                    43.06869999999981 302.5]}
               :rdof {:# 0}}]}

      success-pattern
      '[
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"] {:e [1.0 0.0 0.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_FRONT"] {:e [1.0 0.0 0.0]}]}
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"] {:e [0.0 1.0 0.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_TOP"] {:e [0.0 1.0 0.0]}]}
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"] {:e [0.0 0.0 1.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_RIGHT"] {:e [0.0 0.0 1.0]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_2"] {:e [-8649.51 4688.51 600.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT2"] {:e [3467.85 43.0687 302.5]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_1"] {:e [-8625.71 4720.65 570.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT1"] {:e [3455.57 5.0 332.5]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_0"] {:e [-8625.71 4720.65 600.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "PNT0"] {:e [3455.57 5.0 302.5]}]}]

      failure-pattern
      []

      augmented-pattern
      '#clojure.data.xml.Element
      {:tag :versor
       :attrs
       {:x 12315.409884054143 :y -4259.980462767625 :z 903.0008418651848
        :i -0.8894325067015925 :j 0.4570665300942336 :k -2.435861248047122E-5
        :pi 0.9999681894102073}
       :content ()}
      ]
  (let [constraints (:constraint graph)
        kb graph
        ;; _ (enable-trace!)
        [success? result-kb result-success result-failure] (position-analysis kb constraints)
        {result-mark :mark result-link :link} (ref->str result-kb)
        augmented-zipper (zip/xml-zip (:augmented (cyphy/graph-to-cyphy-zipper graph)))
        augmented-sample (zip/node (zx/xml1-> augmented-zipper :Assembly :CADComponent :CADComponent
                                              (zx/attr= :Name "BOOM") :versor )) ]
    (tol/set-default-tolerance 0.01)
    (expect mark-pattern result-mark)
    (expect link-pattern result-link)
    (expect success-pattern result-success)
    (expect failure-pattern result-failure)
    (expect augmented-pattern augmented-sample)) )



