(ns assemble.in-plane-p2o-test
  "Test the in-plane-mobile assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action
             [in-plane-dispatch :as in-plane-mobile]]))

(defn kb-three-fixed-planes-w-mobile-point []
  (let
    [m1-link-name "mobile-point"
     m2-link-name "static-planes"]
    {:invar {:dir (ref #{[m2-link-name]}),
             :twist (ref #{[m2-link-name]}),
             :loc (ref #{[m2-link-name]})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 3},
        :rdof {:# 3}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}})}}))


(let [kb (kb-three-fixed-planes-w-mobile-point)]

  ;;===== t3-r3 =============

  (apply in-plane-mobile/assemble!
         (in-plane-mobile/precondition
          kb
          [["mobile-point" "MARK-POINT"]
           {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
          [["static-planes" "MP-1"]
           {:e [2.0 2.0 2.0], :pi 0.0, :q [0.0 0.0 1.0]}]))

  (tt/facts
   "in-plane-mobile : assemble t3-r3"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )
   (tt/fact "invar location"
            (-> kb :invar :loc deref)  => #{["static-planes"]}  )
   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 -3.0],  ;; [_ _ (2.0 - 5.0)]
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof"
              (:tdof mobile-point) =>
              {:# 2,
               :point [3.0 4.0 2.0],
               :plane (ga/plane [2.0 2.0 2.0] [0.0 0.0 3.0]),
               :lf [3.0 4.0 2.0]})

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) => {:# 3})  ) )

  ;;===== t2-r3 =============

  (apply in-plane-mobile/assemble!
         (in-plane-mobile/precondition
          kb
          [["mobile-point" "MARK-POINT"]
           {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
          [["static-planes" "MP-1"]
           {:e [0.0 10.0 -50.0], :pi 0.0, :q [0.0 1.0 0.0]}]))

  (tt/facts
   "in-plane-mobile : assemble t2-r3"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )
   (tt/fact "invar location"
            (-> kb :invar :loc deref)  => #{["static-planes"]}  )
   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor (no change expected)"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 -3.0],
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof (no change expected)"
              (:tdof mobile-point) =>
              {:# 2,
               :point [3.0 4.0 2.0],
               :plane (ga/plane [2.0 2.0 2.0] [0.0 0.0 1.0]),
               :lf [3.0 4.0 2.0]})

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) => {:# 3})  ) )

  ;;===== t1-r3 =============

  (apply in-plane-mobile/assemble!
         (in-plane-mobile/precondition
          kb
          [["mobile-point" "MARK-POINT"]
           {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
          [["static-planes" "MP-1"]
           {:e [2.0 0.0 -5.0], :pi 0.0, :q [1.0 0.0 0.0]}]))

  (tt/facts
   "in-plane-mobile : assemble t1-r3"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar location"
            (-> kb :invar :loc deref)  =>
            #{["static-planes"]} #{["mobile-point" "MARK-POINT"]}  )

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )

   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 -3.0],
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof"
              (:tdof mobile-point) =>
              {:# 2,
               :lf [3.0 4.0 2.0],
               :plane (ga/plane  [2.0 2.0 2.0] [0.0 0.0 1.0]),
               :point [3.0 4.0 2.0]} )

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) => {:# 3})  ) ) )


(defn test-t3-r3 []
(let
  [m1-link-name "{CARRIAGE}"
   m2-link-name "{ASSY}|1"

   [kb m1 m2 motive :as precon]
   (in-plane-mobile/precondition
    {:invar {:dir (ref #{m2-link-name}),
             :twist (ref #{m2-link-name}),
             :loc (ref #{m2-link-name})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 3},
        :rdof {:# 3}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}})}}
    [[m1-link-name "FRONT"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
    [[m2-link-name "FRONT"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}] ) ]

  (tt/fact "precondition statisfied" precon =not=> nil?)
  (in-plane-mobile/assemble! kb m1 m2 motive)

  (let [link (-> kb :link (get m1-link-name) deref)]
    (tt/facts
     "in-plane-mobile t3-r3 m1"
     (tt/fact "about keys"
              (set (keys link)) => #{:rdof :tdof :versor})
     (tt/fact "about rdof"
              (:rdof link) => {:# 3})
     (tt/facts "about tdof"
               (tt/fact "count"
                        (get-in link [:tdof :#]) =>  2)

               (tt/fact "count"
                        (get-in link [:tdof :lf])
                        => [0.0 0.0 0.0])

               (tt/fact "plane"
                        (get-in link [:tdof :plane])
                        => {:e [0.0 0.0 0.0],
                            :n [0.0 0.0 1.0]})
               (tt/fact "point"
                        (get-in link [:tdof :point])
                        =>  [0.0 0.0 0.0]) )
     (tt/fact "about versor"
              (:versor link) => {:rotate [1.0 0.0 0.0 0.0],
                                 :xlate [0.0 0.0 0.0]}) ))

  (tt/fact
   "in-plane-mobile t3-r3 m2"
   @(get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0},
    :tdof {:# 0},
    :versor {:rotate [1.0 0.0 0.0 0.0],
             :xlate [0.0 0.0 0.0]}}) ) )



(defn test-t1-r3 []
(let
  [m1-link-name "{CARRIAGE}"
   m2-link-name "{ASSY}|1"
   [kb m1 m2 motive :as precon]
   (in-plane-mobile/precondition
    {:invar {:dir (ref #{m2-link-name}),
             :twist (ref #{m2-link-name}),
             :loc (ref #{m2-link-name})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 2, :point [0.0 0.0 0.0]},
        :rdof {:# 3}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}})}}

    [[m1-link-name "TOP"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
    [[m2-link-name "TOP"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}])]

  (tt/fact "precondition statisfied" precon =not=> nil?)
  (in-plane-mobile/assemble! kb m1 m2 motive)

  (tt/fact
   "in-plane-mobile :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :point [0.0 0.0 0.0]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}})

  (tt/fact
   "in-plane-mobile :t2-r3 m2"
   @(clojure.core/get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0}
    :tdof {:# 0}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}}) ) )

(defn test-t2-r3 []
(let
  [m1-link-name "{ARM}"
   m2-link-name "{BOOM}"
   [kb m1 m2 motive :as precon]
   (in-plane-mobile/precondition
    {:invar {:dir (ref #{m2-link-name}),
             :twist (ref #{m2-link-name}),
             :loc (ref #{m2-link-name})},
     :link
     {m1-link-name
      (ref
       {:versor
        {:xlate [0.0 0.0 2486.3199999999997],
         :rotate [1.0 0.0 0.0 0.0]},
        :tdof
        {:# 2,
         :point [0.0 0.0 2236.3199999999997],
         :plane [0.0 0.0 2236.3199999999997]},
        :rdof {:# 3}}),
      m2-link-name
      (ref
       {:versor
        {:xlate [6056.068568289035
                 1285.2113238369175
                 220.0398552624306],
         :rotate
         [0.903723365158405
          -0.04068401573948011
          0.12144977372159717
          0.4085080691896436]},
        :tdof
        {:# 2,
         :point [0.0 0.0 2236.3199999999997],
         :plane {:e [0.0 0.0 -1636.32], :n [0.0 0.0 1.0]}},
        :rdof
        {:# 1,
         :dir [266.4038129165836
               -7142.156541926609
               2149.900428065357]}})}}
    [[m1-link-name "BOOM_CENTER_PLANE"]
     {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}]
    [[m2-link-name "BOOM_CENTER_PLANE"]
     {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}] ) ]

  (tt/fact "precondition statisfied" precon =not=> nil?)
  (in-plane-mobile/assemble! kb m1 m2 motive)

  (tt/fact
   "in-plane-mobile :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :plane [0.0 0.0 2236.3199999999997]
           :point [0.0 0.0 2236.3199999999997]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 2486.3199999999997]}} )

  (tt/fact
   "in-plane-mobile :t2-r3 m2"
   @(clojure.core/get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 1
           :dir [266.4038129165836
                 -7142.156541926609
                 2149.900428065357]}
    :tdof {:# 2
           :plane {:e [0.0 0.0 -1636.32]
                   :n [0.0 0.0 1.0]}
           :point [0.0 0.0 2236.3199999999997]}
    :versor {:rotate [0.903723365158405
                      -0.04068401573948011
                      0.12144977372159717
                      0.4085080691896436]
             :xlate [6056.068568289035
                     1285.2113238369175
                     220.0398552624306]}} ) ) )


(defn test-t2-r0 []
(let
 [m1-link-name "{CARRIAGE}"
  m2-link-name "{ASSY}|1"
  [kb m1 m2 motive :as precon]
  (in-plane-mobile/precondition
   {:invar
    {:dir (ref #{m2-link-name}),
     :twist (ref #{m2-link-name}),
     :loc (ref #{m2-link-name})},
    :link
    {m1-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof
       {:# 2,
        :point [0.0 0.0 0.0],
        :plane (ga/plane [0.0 0.0 0.0] [0.0 1.0 0.0]),
        :lf nil},
       :rdof {:# 0}}),
     m2-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 0},
       :rdof {:# 0}})}}
   [[m1-link-name "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   [[m2-link-name "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}] )]

 (tt/fact "precondition satisfied" precon =not=> nil?)
 (tt/fact "about motive" motive => :p2o)

 (tt/fact
  "about dispatch"
  (in-plane-mobile/assemble-dispatch kb m1 m2 motive) =>
  {:tdof 2 :rdof 0 :motive motive})

 (in-plane-mobile/assemble! kb m1 m2 motive)

 (tt/fact
  "in-plane t2-r0 m1"
  @(get-in kb [:link m1-link-name]) =>
   {:rdof {:# 0},
    :tdof {:# 1, :lf [0.0 0.0 0.0],
           :line (ga/line [0.0 0.0 0.0] [1.0 0.0 0.0]),
           :point [0.0 0.0 0.0]},
    :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} )

 (tt/fact
  "in-plane t2-r0 m2"
  @(get-in kb [:link m2-link-name]) =>
   {:rdof {:# 0}
    :tdof {:# 0}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}}) ) )


(test-t3-r3)
(test-t1-r3)
(test-t2-r3)
(test-t2-r0)
