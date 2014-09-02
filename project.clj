(defproject geometric-assembler "0.1.0-SNAPSHOT"
  :description "develop examples for geometric assembly"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :java-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  ;; :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 ;; [org.clojure/data ]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.codehaus.woodstox/stax2-api "3.1.4"]
                 [com.fasterxml/aalto-xml "0.9.9"]
                 [com.fasterxml.staxmate/staxmate "2.2.0"]
                 ;; [net.java.dev.stax-utils/stax-utils "20070216"]
                 [midje "1.6.3"]]
  :plugins [[lein-autoexpect "1.2.2"]
            [lein-ancient "0.5.5"]
            [lein-midje "3.0.0"] ]
  :main isis.geom.linkage-assembler
  :target-path "target/%s/"
  :dev-resources ["src/test/resources"]
  :uberjar-name "freed_linkage_assembler.jar"
  :profiles
  { :dev
    {:aot :all
     :test-paths ["src/test/clojure"]
     :resource-paths ["src/test/resources"]}

    :uberjar
    {:aot :all} }

  :aliases {} )
