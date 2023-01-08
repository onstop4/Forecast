(ns forecast.api
  (:require
   [cheshire.core :as json])
  (:import (java.net InetAddress)))

(defn download-and-parse [url]
  (json/parse-string (slurp url) true))

(defn get-ip-address []
  (-> (slurp "https://api64.ipify.org/")
      InetAddress/getByName
      .getHostAddress))

(defn get-coordinates-from-ip [ip-address]
  (-> (str "https://ipapi.co/" ip-address "/json/")
      download-and-parse
      (select-keys [:latitude :longitude])))

(defn get-nws-office [{:keys [latitude longitude]}]
  (-> (str "https://api.weather.gov/points/" latitude "," longitude)
      download-and-parse
      :properties
      (select-keys [:cwa :gridX :gridY])))

(def unit-conversion {"F" "us" "C" "si"})

(defn get-hourly-forecast [{:keys [cwa gridX gridY]} units]
  (->   (str "https://api.weather.gov/gridpoints/" cwa "/" gridX "," gridY "/forecast/hourly?units=" (unit-conversion units))
        download-and-parse
        (get-in [:properties :periods])))

(defn get-extended-forecast [{:keys [cwa gridX gridY]} units]
  (-> (str "https://api.weather.gov/gridpoints/" cwa "/" gridX "," gridY "/forecast?units=" (unit-conversion units))
      download-and-parse
      (get-in [:properties :periods])))
