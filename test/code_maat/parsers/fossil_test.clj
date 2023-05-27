(ns code-maat.parsers.fossil-test
  (:require [code-maat.parsers.fossil :as fossil])
            (:use clojure.test incanter.core))

(def ^:const entry
  "Commit: c7697a0d45
Author: dan
Date: 2023-05-05 15:52:44
Comment: Reduce the maximum depth of nesting in json objects to 1000.
   EDITED src/json.c
"
)

(def ^:const entries
  "Commit: 83683e108b
Author: xyz
Date: 2023-05-04 14:44:53
Comment: Enhance the format() function
   EDITED src/printf.c

Commit: b2e0800b24
Author: dan
Date: 2023-05-04 14:41:10
Comment: Merge latest changes into this branch.
   ADDED file2
   DELETED Makefile
")

(def ^:const last-entry
  "Commit: 704b122e53
Author: pete
Date: 2000-05-29 14:16:00
Comment: initial empty check-in
+++ end of timeline (113) +++
")

(defn- parse
  [text]
  (fossil/parse-read-log text {}))

(deftest parses-single-entry-to-dataset
  (is (= (parse entry)
         [{
           :author  "dan"
           :rev     "c7697a0d45"
           :date    "2023-05-05"
           :entity  "src/json.c"
           :message "Reduce the maximum depth of nesting in json objects to 1000."
           }]
)))

(deftest parse-multiple-entries-to-dataset
  (is (= (parse entries)
         [{
           :author  "xzy"
           :rev     "83683e108b"
           :date    "2023-05-04"
           :entity  "src/printf.c"
           :message "Enhance the format() function"
           },
          {
           :author  "dan"
           :rev     "b2e0800b24"
           :date    "2023-05-04"
           :entity  "file2"
           :message "Merge latest changes into this branch."
           },
          {
           :author  "dan"
           :rev     "b2e0800b24"
           :date    "2023-05-04"
           :entity  "Makefile"
           :message "Merge latest changes into this branch."
           }]
         )))

(deftest parses-last-entry-to-empty-dataset
  (is (= (parse last-entry)
         []
         )))