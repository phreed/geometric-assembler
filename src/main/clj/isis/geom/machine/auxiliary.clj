(ns isis.geom.machine.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
             [error-string :as emsg]
             [tolerance :as tolerance]
             [functions
              :refer [vec-scale vec-diff mag
                      error outer-prod perp-base
                      vec-angle parallel? point?
                      null? rotate perp-dist
                      line intersect plane sphere]]]))

(defn dof-2r:p->p
  "Procedure to rotate body ?link, about axes ?axis-1 and ?axis-2,
  leaving the position of point ?center invariant, and
  moving ?from-point on ?link to globally fixed ?to-point."
  [?link ?center ?from-point ?to-point ?axis-1 ?axis-2 ?q]
  (let [r0 (line ?center ?axis-2)
        r1 (line ?center ?axis-1)
        r2 (perp-base ?from-point r0)
        r3 (perp-base ?to-point r1)
        r4 (intersect (plane r2 ?axis-2) (plane r3 ?axis-1) 0)
        r5 (sphere ?center (perp-dist ?to-point ?center))
        r6 (intersect r5 r4 ?q)]
    (if-not (point? r6)
      (error (perp-dist r5 r4) emsg/emsg-7)
      (let [r7 (vec-diff ?from-point r3)
            r8 (vec-diff r6 r2)
            r9 (vec-angle r7 r8 (outer-prod r7 r8))
            r10 (vec-diff r6 r3)
            r11 (vec-diff ?to-point r3)
            r12 (vec-angle r10 r11 (outer-prod r10 r11)) ]
        (rotate ?link ?center ?axis-1 r12)))) )

(defn dof-1r:p->p
  "Procedure to rotate body ?link about ?axis in such a way
  as to not violate restrictions imposed by ?axis-1 and
  ?axis-2, if they exist.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?link to globally-fixed ?to-point."
  [?link ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2]
  (let [r0 (perp-base ?to-point (line ?center ?axis))
        r1 (vec-diff ?to-point r0)
        r2 (vec-diff ?from-point r0)]
    (if-not (tolerance/in-range? :default (mag r1) (mag r2))
      (error [:r1 r1, :r2 r2] emsg/emsg-4)
      (let [r4 (outer-prod r1 r2)]
        (if-not (parallel? r4 ?axis false)
          (error (vec-angle r4 ?axis
                            (outer-prod r4 ?axis)) emsg/emsg-3)
          (let [r5 (vec-angle r2 r1 ?axis)]
            (if (and (null? ?axis-1) (null? ?axis-2))
              (rotate ?link ?center ?axis r5)
              (dof-2r:p->p ?link ?center ?from-point ?to-point ?axis-1 ?axis-2))))))))

(defn dof-3r:p->p
  "Procedure to rotate body ?link about ?center,
  moving ?from-point on ?link to globally-fixed ?to-point.
  Done by constructing a rotational axis and calling dof-1r:p->p."
  [?link ?center ?from-point ?to-point]
  (let [r0 (vec-diff ?from-point ?center)
        r1 (vec-diff ?to-point ?center)]
    (cond (tolerance/equivalent? :default r0 r1) {:e [0.0 0.0 0.0]}
          (tolerance/in-range? :default (mag r0) (mag r1)) (error [:r0 r0 :r1 r1] emsg/emsg-4)
          :else (dof-1r:p->p ?link ?center ?from-point ?to-point
                   (outer-prod r0 r1) nil nil))))

