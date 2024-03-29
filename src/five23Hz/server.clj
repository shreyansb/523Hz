(ns five23Hz.server
  (:use noir.core)
  (:require [noir.server :as server]
            [noir.response :as response]
            [clj-http.client :as client]
            [tumblr_music.settings :as settings]
            [clojure.data.json :as json]))

(defn get_blog []
  (get
    (client/get
      (format
        "http://api.tumblr.com/v2/blog/%s/posts?api_key=%s&tag=music" 
        settings/tumblr_blog 
        settings/tumblr_key)
      {:accepts :json})
    :body))

(defn get_posts []
  (get (get (json/read-json (get_blog)) :response) :posts))

(defn get_link [post]
  (get post :permalink_url))

(defn get_links []
  (filter 
    (fn [x] (not= x nil)) 
    (map get_link (get_posts))))

;;;
;;; Routes
;;;

(defpage "/" []
  (response/json
    (get_links)))

;;;
;;; Main
;;;

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'tumblr_music})))
