(ns code-maat.parsers.fossil
  (:require [code-maat.parsers.time-parser :as tp]
            [code-maat.parsers.hiccup-based-parser :as hbp]))

;;; This module is responsible for parsing a fossil log file.
;;;
;;; Input: a log file generated with the following command:
;;;
;;;    fossil timeline -F '%nCommit: %h%nAuthor: %a%nDate: %d%nComment: %c' -t ci -v | tail -n +2
;;; (the 'tail -n +2' is there to remove the first empty line of the output)

(def ^:const fossil-grammar
  "Here's the instaparse grammar for a fossil entry.
 Note that we parse the entries one by one (Instaparse memory optimization).

 Currently, fossil does not display information about number of changed lines.
 "
  "
    entry = commit author date comment changes
    commit    = <'Commit:'> <ws> #'[\\da-f]+' <nl>
    author    = <'Author: '> #'[^\\n]*' <nl>
    date      = <'Date: '> #'\\d{4}-\\d{2}-\\d{2}' <ws #'\\d{2}:\\d{2}:\\d{2}'> <nl>
    comment   = <'Comment: '> #'.+' <nl?>
    changes   = file* <lastentry?>
    file      = <ws> <action> #'\\S+' <nl>
    action    = ('EDITED ' | 'ADDED ' | 'DELETED ')
    lastentry = #'\\+\\+\\+ end of timeline \\(\\d+\\) \\+\\+\\+' <nl>
    ws        = #'\\s+'
    nl        = '\\n'")

(def as-common-time-format (tp/time-string-converter-from "YYYY-MM-dd"))

(def positional-extractors
  "Specify a set of functions to extract the parsed values."
  {:rev     #(get-in % [1 1])
   :author  #(get-in % [2 1])
   :date    #(as-common-time-format (get-in % [3 1]))
   :message #(get-in % [4 1])
   :changes #(rest (get-in % [5]))
   })

(defn parse-log
  "Transforms the given input fossil log into an
   Incanter dataset suitable for the analysis modules."
  [input-file-name options]
  (hbp/parse-log input-file-name
                 options
                 fossil-grammar
                 positional-extractors))

(defn parse-read-log
  [input-text options]
  (hbp/parse-read-log input-text
                      options
                      fossil-grammar
                      positional-extractors))