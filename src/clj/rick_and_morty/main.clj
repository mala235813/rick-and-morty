(ns rick-and-morty.main
  (:require [clojure.pprint :as pp]
            [crux.api :as crux]
            [io.pedestal.http :as http]))

(declare query-stuff)
(declare crux-node)

(defn hello
  [request]
  {:status 200
   :body "Rick and morty app!"})

(def echo
  {:name :echo
   :enter (fn [context]
          (let [response {:status 200
                          :headers {"Content-Type" "text/plain"}
                          :body (with-out-str (pp/pprint context))}]
            (assoc context :response response)))})

(def get-stuff
  {:name :get-stuff
   :enter (fn [context]
            (let [stuff (query-stuff @crux-node)
                  response {:status 200
                            :headers {"Content-Type" "text/plain"}
                            :body (with-out-str (pp/pprint stuff))}]
            (assoc context :response response)))})

(def routes #{["/" :get #'hello :route-name ::root]
              ["/echo" :any #'echo :route-name ::echo]
              ["/api/v1/stuff" :get get-stuff :route-name ::get-stuff]})

(defonce instance (atom nil))

(defonce crux-node (atom nil))

(defn start-crux
 []
 (when (nil? @crux-node)
   (reset! crux-node (crux/start-node {}))))

(defn stop-crux
 []
 (when (some? @crux-node)
   (.close @crux-node)))

(defn start
  ([] (start false))
  ([do-join]
   (start-crux)
   (let [server-map {::http/type :jetty
                     ::http/host "0.0.0.0"
                     ::http/port 80
                     ::http/join? do-join
                     ::http/routes routes
                     ::http/resource-path "/public"}
         server (http/create-server server-map)]
     (http/start server)
     (reset! instance server))))

(defn stop
  []
  (when (some? (deref instance))
    (http/stop (deref instance))
    (reset! instance nil))
  (stop-crux))

(defn restart
  ([] (restart false))
  ([do-join]
   (stop)
   (start do-join)))

(defn query-stuff
  [node]
  (crux/q (crux/db node)
          '{:find [name desc]
            :where [[e :name name]
                    [e :desc desc]]}))

(comment
  (start)

  (stop)

  (restart)

  (crux/submit-tx @crux-node [[:crux.tx/put {:crux.db/id :foo
                                             :name "foo"
                                             :desc "A foo!"}]])

  (query-stuff @crux-node)


  )

(defn -main
  [& args]
  (start true))
