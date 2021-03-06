(ns isis.geom.action.in-plane-o2p-slice
  "The table of rules for the in-plane constraint where
  the point marker, ?M_1, is FIXED and the plane marker, ?M_2, is mobile."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.machine [tolerance :as tol]]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]
            [clojure.pprint :as pp] ))



(defn assemble!->t0-r0
  "PFT entry: (0,0,in-plane)  (M_1 is fixed)

  Initial status:
  0-TDOF(?m1-link, ?m1-point)
  0-RDOF(?m1-link)

  0-TDOF(?m2-link, ?m2-point)
  0-RDOF(?m2-link)

  Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_2), gmp(?M_1));
  R[1] = inner-prod(R[0], gmp(?M_1));
  unless zero?(R[1])
  error(R[1], estring[9]);
  end;

  New status: unchanged

  Explanation:
  Geom ?m2-link is fixed, so the in-plane constraint
  can only be checked for consistency.
  "
  [kb m1 m2]
  (let [gm1 (ga/gmp m1 kb)
        sep (ga/vec-diff (ga/gmp m2 kb) gm1)
        off-plane (ga/inner-prod sep gm1) ]
    (if (tol/near-zero? :tiny off-plane)
      :consistent
      (do
        (println "inconsistently overconstrained in-plane" m1 m2)
        :inconsistent )) ))

(def slicer "in-plane-fixed")

(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  :not-applicable)

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  :not-applicable)
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r2 slicer kb m1 m2))
(defn assemble!->t2-r3 [kb m1 m2]  :not-applicable)

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))

(defn assemble!->t3-r3
  "PFT entry: (3,3,in-plane)

  Initial status:
  0-TDOF(?m1-link)
  0-RDOF(?m1-link)

  3-TDOF(?m2-link)
  3-RDOF(?m2-link)

  Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(gmp(?M_1), R[1]);
  translate(?m2-link, R[2]);
  R[3] = gmp(?M_1);
  end;

  New status:
  2-TDOF(?m2-link, R[3], R[0], R[0])
  3-RDOF(?m2-link)

  Explanation:
  Geom ?m2-link is free to translate, so the translation
  vector is measured and the ?m2-link is moved.
  No checks are required.
  "
  [kb m1 m2]
  ;; (pp/pprint ["t3r3 - in-plane-o2p-slice" "m1" m1 "m2" m2])
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])
        m1-gmp (ga/gmp m1 kb)
        m2-gmp (ga/gmp m2 kb)
        m2-gmz (ga/gmz m2 kb)
        m2-plane (ga/plane m2-gmp m2-gmz)
        reject (ga/rejection m1-gmp m2-plane) ]
    (dosync
     (alter m2-link merge
            (ga/translate @m2-link ga/vec-sum reject))
     (alter m2-link assoc
            :tdof {:# 2
                   :point m1-gmp
                   :plane m2-plane
                   :lf m2-plane}
            :rdof {:# 3 } ) ))
  :progress-made)

