(ns xml.cyphy-cad-test
  (:require [midje.sweet :refer [facts fact]]
            [isis.geom.lang.cyphy-cad-stax :as cyphy]

            [clojure.java.io :as jio]
            [clojure.pprint :as pp]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
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

(defn- ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))

(with-open [fis (-> "excavator/cad_assembly_boom_dipper_csys.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
        constraints (:constraint kb)]

    ;; (pp/pprint constraints)
    (facts "about the parsed cad-assembly file with csys"
           (fact "about the constraints"
                 constraints =>
                 [{:type :coincident,
                   :m1
                   [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"]
                    {:e [1.0 0.0 0.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"]
                    {:e [1.0 0.0 0.0]}]}
                  {:type :coincident,
                   :m1
                   [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"]
                    {:e [0.0 1.0 0.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"]
                    {:e [0.0 1.0 0.0]}]}
                  {:type :coincident,
                   :m1
                   [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"]
                    {:e [0.0 0.0 1.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"]
                    {:e [0.0 0.0 1.0]}]}
                  {:type :coincident,
                   :m1
                   [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
                    {:e [-8625.71 4720.65 600.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
                    {:e [3455.57 5.0 302.5]}]}
                  {:type :coincident,
                   :m1
                   [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
                    {:e [-8802.045575687742 4477.944901687515 600.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
                    {:e [3755.57 5.0 302.5]}]}
                  {:type :coincident,
                   :m1
                   [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
                    {:e [-8302.10320225002 4485.53589908301 600.0]}],
                   :m2
                   [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
                    {:e [3455.57 -395.0 302.5]}]}])


           (fact "about the initial link settings"
                 (ref->str (:link kb)) =>
                 '{"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
                   [:ref
                    {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                     :tdof {:# 3} :rdof {:# 3}}],
                   "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
                   [:ref
                    {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                     :tdof {:# 3} :rdof {:# 3}}],
                   "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
                   [:ref
                    {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                     :tdof {:# 0} :rdof {:# 0}}]} )

           (fact "about the base link id"
                 "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" => (:base kb))

           (fact "about the initial marker invariants"
                 (ref->str (:mark kb)) =>
                 '{:loc [:ref  #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
                   :z [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
                   :x [:ref  #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]})
           )


    (let [mark-pattern
          '{:loc [:ref #{["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
                         ["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
                         ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
            :x [:ref #{["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
                       ["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
                       ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
            :z [:ref #{["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
                       ["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
                       ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]}

          link-pattern
          {"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
           [:ref
            {:versor
             {:xlate [2204.590945944376 -9748.074429784388 902.4999999999989],
              :rotate
              [2.779890061746713E-17
               -0.45399049973954625
               0.8910065241883681
               5.455841439333469E-17]},
             :tdof {:# 0, :point [3455.5699999999997 5.0 302.5]},
             :rdof {:# 0}}],
           "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
           [:ref
            {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
             :tdof {:# 0, :point [1.0 0.0 0.0]},
             :rdof {:# 0}}],
           "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
           [:ref
            {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
             :tdof {:# 0},
             :rdof {:# 0}}]}


          success-pattern
          '[{:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"]
              {:e [1.0 0.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"]
              {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"]
              {:e [0.0 1.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"]
              {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"]
              {:e [0.0 0.0 1.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"]
              {:e [0.0 0.0 1.0]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
              {:e [-8625.71 4720.65 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
              {:e [3455.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
              {:e [-8802.045575687742 4477.944901687515 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
              {:e [3755.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
              {:e [-8302.10320225002 4485.53589908301 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
              {:e [3455.57 -395.0 302.5]}]}]

          failure-pattern []
          ]
      (let [[success? result-kb result-success result-failure] (position-analysis kb constraints)
            {result-mark :mark result-link :link} (ref->str result-kb) ]

        ;; (pp/pprint result-success)
        ;; (pp/pprint result-link)
        (facts "about results of linkage-assembly"
               (fact "about the mark result"
                     result-mark => mark-pattern )
               (fact "about the success result"
                     result-success => success-pattern )
               (fact "about the failure result"
                     result-failure => failure-pattern )
               (fact "about the link result"
                     result-link => link-pattern ))

        (with-open [fis (-> "excavator/cad_assembly_boom_dipper_csys.xml"
                            jio/resource jio/input-stream)
                    fos (-> "/tmp/cad_assembly_boom_dipper_csys_aug.xml"
                            jio/output-stream)]

          (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) )))

;;    (pp/pprint (ref->str result-link))

;; trying to use staxmate rather than straight stax
#_(with-open [fos (-> "junk.xml"
                      jio/output-stream)]
    (cyphy/write-cad-assembly-using-knowledge fos nil)
    (with-open [fis (-> "junk.xml"
                        jio/input-stream)]
      (cyphy/read-cad-assembly-using-knowledge fis nil)))