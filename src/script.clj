#!/usr/bin/env bb

(require '[babashka.curl :as curl]
         '[clojure.tools.cli :refer [parse-opts]])

(def cli-options
  ;; An option with a required argument
  [["-t" "--text TEXT" "The text to read aloud"
    :default "Hello World"]
   ["-v" "--voice NAME" "The voice to render speach with"
    :default "es-LA_SofiaV3Voice"]
   ["-l" "--lang LANG" "The language"
    :default "es"]
   ["-h" "--help"]])

(-> (Runtime/getRuntime)
    (.addShutdownHook
     (Thread. #(println "Exiting."))))

(def output-file "/tmp/audio.opus")

(def talk-endpoint "https://text-to-speech-demo.ng.bluemix.net/api/v3/synthesize")


(def voice-vector ["[af] Translate Afrikaans (beta)"
                   "[ar-AR] Omar (male) (Watson)"
                   "[ar] Translate Arabic (beta)"
                   "[bn] Translate Bengali (beta)"
                   "[bs] Translate Bosnian (beta)"
                   "[ca] Translate Catalan (beta)"
                   "[cs] Translate Czech (beta)"
                   "[cy] Translate Welsh (beta)"
                   "[da] Translate Danish (beta)"
                   "[de-DE] Birgit (female) (Watson)"
                   "[de-DE] BirgitV3 (female, enhanced dnn) (Watson)"
                   "[de-DE] Dieter (male) (Watson)"
                   "[de-DE] DieterV3 (male, enhanced dnn) (Watson)"
                   "[de-DE] ErikaV3 (female, enhanced dnn) (Watson)"
                   "[de] Translate German (beta)"
                   "[el] Translate Greek (beta)"
                   "[en-US] Allison (female, expressive, transformable) (Watson)"
                   "[en-US] AllisonV3 (female, enhanced dnn) (Watson)"
                   "[en-US] EmilyV3 (female, enhanced dnn) (Watson)"
                   "[en-US] HenryV3 (male, enhanced dnn) (Watson)"
                   "[en-US] KevinV3 (male, enhanced dnn) (Watson)"
                   "[en-US] Lisa (female, transformable) (Watson)"
                   "[en-US] LisaV3 (female, enhanced dnn) (Watson)"
                   "[en-US] Michael (male transformable) (Watson)"
                   "[en-US] MichaelV3 (male, enhanced dnn) (Watson)"
                   "[en-US] OliviaV3 (female, enhanced dnn) (Watson)"
                   "[en-GB] CharlotteV3 (female, enhanced dnn) (Watson)"
                   "[en-GB] JamesV3 (male, enhanced dnn) (Watson)"
                   "[en-GB] Kate (female) (Watson)"
                   "[en-GB] KateV3 (female, enhanced dnn) (Watson)"
                   "[en] Translate English (beta)"
                   "[eo] Translate Esperanto (beta)"
                   "[es-ES] Enrique (male) (Watson)"
                   "[es-ES] EnriqueV3 (male, enhanced dnn) (Watson)"
                   "[es-ES] Laura (female) (Watson)"
                   "[es-ES] LauraV3 (female, enhanced dnn) (Watson)"
                   "[es-LA] Sofia (female) (Watson)"
                   "[es-LA] SofiaV3 (female, enhanced dnn) (Watson)"
                   "[es-US] Sofia (female) (Watson)"
                   "[es-US] SofiaV3 (female, enhanced dnn) (Watson)"
                   "[es] Translate Spanish (beta)"
                   "[fi] Translate Finnish (beta)"
                   "[fil] Translate Filipino (beta)"
                   "[fr-FR] NicolasV3 (male, enhanced dnn) (Watson)"
                   "[fr-FR] Renee (female) (Watson)"
                   "[fr-FR] ReneeV3 (female, enhanced dnn) (Watson)"
                   "[fr] Translate French (beta)"
                   "[he] Translate Hebrew (beta)"
                   "[hi] Translate Hindi (beta)"
                   "[hr] Translate Croatian (beta)"
                   "[hu] Translate Hungarian (beta)"
                   "[hy] Translate Armenian (beta)"
                   "[id] Translate Indonesian (beta)"
                   "[is] Translate Icelandic (beta)"
                   "[it-IT] Francesca (female) (Watson)"
                   "[it-IT] FrancescaV3 (female, enhanced dnn) (Watson)"
                   "[it] Translate Italian (beta)"
                   "[ja-JP] Emi (female) (Watson)"
                   "[ja-JP] EmiV3 (female, enhanced dnn) (Watson)"
                   "[ja] Translate Japanese (beta)"
                   "[km] Translate Khmer (beta)"
                   "[ko-KR] Youngmi (female) (Watson)"
                   "[ko-KR] Yuna (female) (Watson)"
                   "[ko] Translate Korean (beta)"
                   "[la] Translate Latin (beta)"
                   "[lv] Translate Latvian (beta)"
                   "[mk] Translate Macedonian (beta)"
                   "[ml] Translate Malayalam (beta)"
                   "[ne] Translate Nepali (beta)"
                   "[nl-NL] Emma (female) (Watson)"
                   "[nl-NL] Liam (male) (Watson)"
                   "[nl] Translate Dutch (beta)"
                   "[no] Translate Norwegian (beta)"
                   "[pl] Translate Polish (beta)"
                   "[pt-BR] Isabela (female) (Watson)"
                   "[pt-BR] IsabelaV3 (female, enhanced dnn) (Watson)"
                   "[pt] Translate Portuguese (beta)"
                   "[ro] Translate Romanian (beta)"
                   "[ru] Translate Russian (beta)"
                   "[si] Translate Sinhala (beta)"
                   "[sk] Translate Slovak (beta)"
                   "[sq] Translate Albanian (beta)"
                   "[sr] Translate Serbian (beta)"
                   "[sv] Translate Swedish (beta)"
                   "[sw] Translate Swahili (beta)"
                   "[ta] Translate Tamil (beta)"
                   "[te] Translate Telugu (beta)"
                   "[th] Translate Thai (beta)"
                   "[tl] Translate Tagalog (beta)"
                   "[tr] Translate Turkish (beta)"
                   "[uk] Translate Ukrainian (beta)"
                   "[vi] Translate Vietnamese (beta)"
                   "[zh-CN] LiNa (female) (Watson)"
                   "[zh-CN] WangWei (Male) (Watson)"
                   "[zh-CN] ZhangJing (female) (Watson)"
                   "[zh-CN] Translate Chinese (beta)"])

(def voices {:en {:kate    "en-GB_KateV3Voice"
                  :allison "en-US_AllisonV3Voice"}
             :es {:sofia "es-LA_SofiaV3Voice"
                  :laura "es-ES_LauraV3Voice"}})
#_(def resp  (curl/get talk-endpoint
                     {:as           :stream
                      :headers      {"Connection" "keep-alive"
                                     "Accept"     "*/*"}
                      :query-params {"text"     (apply str *command-line-args*)
                                     "voice"    (get-in voices [:es :sofia])
                                     "download" "true"
                                     "accept"   "audio/ogg;codec=opus"}}))


(defn download-speech
  [text voice]
  (-> (curl/get talk-endpoint
                {:as           :stream
                 :headers      {"Connection" "keep-alive"
                                "Accept"     "*/*"}
                 :query-params {"text"     text
                                "voice"    voice
                                "download" "true"
                                "accept"   "audio/ogg;codec=opus"}})
      :body
      (io/copy (io/file output-file))))


(defn -main []
  (download-speech (apply str *command-line-args*)
                   (get-in voices [:es :sofia]))
  (shell/sh "play" output-file)
  (println "file size: " (.length (io/file output-file)))
  (println (:summary (parse-opts *command-line-args* cli-options))))

(-main)
