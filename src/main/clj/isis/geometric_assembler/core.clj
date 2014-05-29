(ns isis.geometric-assembler.core)


(defn -main []
  (println " java version = "(System/getProperty "java.vm.version"))

  (let [ mv (c3ga_pkg.mv.)]
    (.set mv 25.0)
    (println "multi-vector =" (.toString mv)))

  (let [ e1 (c3ga_pkg.c3ga/vectorE1)
         e2 (c3ga_pkg.c3ga/vectorE2)
         e3 (c3ga_pkg.c3ga/vectorE3)
         e1+e2 (c3ga_pkg.c3ga/add e1 e2)]
    (println "e1 + e2 = " (.toString e1+e2)))

  )



