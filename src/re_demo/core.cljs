(ns re-demo.core
  (:require-macros [re-com.core            :refer [handler-fn]]
                   [cljs.core.async.macros :refer [go]])
  (:require [figwheel.client         :as    fw]
            [reagent.core            :as    reagent]
            [alandipert.storage-atom :refer [local-storage]]
            [re-demo.utils           :refer [panel-title]]
            [re-com.util             :as    util]
            [re-com.core             :as    core]
            [re-com.box              :refer [h-box v-box box gap line scroller border]]

            [re-demo.welcome         :as    welcome]
            [re-demo.basics          :as    basics]
            [re-demo.buttons         :as    buttons]
            [re-demo.dropdowns       :as    dropdowns]
            [re-demo.alerts          :as    alerts]
            [re-demo.tabs            :as    tabs]
            [re-demo.popovers        :as    popovers]
            [re-demo.date            :as    date-picker]
            [re-demo.lists           :as    lists]
            [re-demo.time            :as    time]
            [re-demo.layouts         :as    layouts]
            [re-demo.tour            :as    tour]
            [re-demo.modals          :as    modals]
            [re-demo.boxes           :as    boxes]))

(enable-console-print!)

(def tabs-definition
  [ {:id ::welcome   :label "Welcome"     :panel welcome/panel}
    {:id ::basics    :label "Basics"      :panel basics/panel}
    {:id ::buttons   :label "Buttons"     :panel buttons/panel}
    {:id ::dropdown  :label "Dropdowns"   :panel dropdowns/panel}
    {:id ::alerts    :label "Alerts"      :panel alerts/panel}
    {:id ::tabs      :label "Tabs"        :panel tabs/panel}
    {:id ::popovers  :label "Popovers"    :panel popovers/panel}
    {:id ::date      :label "Dates"       :panel date-picker/panel}
    {:id ::time      :label "Time"        :panel time/panel}
    {:id ::lists     :label "List"        :panel lists/panel}
    {:id ::tour      :label "Tour"        :panel tour/panel}
    {:id ::modals    :label "Modals"      :panel modals/panel}
    {:id ::boxes1    :label "Boxes-1"     :panel boxes/panel1}
    {:id ::boxes2    :label "Boxes-2"     :panel boxes/panel2}
    {:id ::layouts   :label "Layouts"     :panel layouts/panel}
    ])


(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id]
      (let [selected (= @selected-tab-id (:id tab))]
      [:div
       {:style {:color            (if selected "#111")
                :border-right     (if selected "4px #e8e8e8 solid")
                :background-color (if (or
                                        (= @selected-tab-id (:id tab))
                                        @mouse-over?) "#f4f4f4")}

        :class "nav-item"
        :on-mouse-over (handler-fn (reset! mouse-over? true))
        :on-mouse-out  (handler-fn (reset! mouse-over? false))
        :on-click      (handler-fn (reset! selected-tab-id (:id tab)))
        }
       [:span
        {:style {:cursor "default"}}    ;; removes the I-beam over the label
        (:label tab)]]))))


(defn left-side-nav-bar
  [selected-tab-id]
    [v-box
     :children (for [tab tabs-definition]
                 [nav-item tab selected-tab-id])])


(defn re-com-title
  []
  [h-box
   :justify  :center
   :align    :center
   :height   "57px"
   :style  {:color "#FEFEFE"
            :background-color "#888"}
   :children [[core/label
               :label     "Re-com"
               :style {:font-size "28px"
                       :font-family "Ubuntu"
                       :font-weight "300"
                      }]]])

(defn main
  []
  (let [id-store        (local-storage (atom nil) ::id-store)
        selected-tab-id (reagent/atom (if  (nil? @id-store) (:id (first tabs-definition)) @id-store))  ;; id of the selected tab
        _               (add-watch selected-tab-id nil #(reset! id-store %4))]                         ;; remember the current navigation item being viewed.
    (fn _main
      []
      [h-box
       ;; TODO: EXPLAIN both lines below with more clarity
       ;; Outer-most box height must be 100% to fill the entrie client area
       ;; (height is 100% of body, which must have already had it's height set to 100%)
       ;; width doesn't need to be initially set
       :height   "100%"
       :gap      "60px"
       ;:padding  "0px 10px 5px 0px"     ;; top right botton left TODO: [GR] Review whether we want this. I don't think so
       :children [[scroller
                   :size  "none"
                   :v-scroll :auto
                   :h-scroll :off
                   :child [v-box
                           :children [[re-com-title]
                                      [left-side-nav-bar selected-tab-id]]]]
                  [scroller
                   :child [box
                           :size      "auto"
                           ;:padding   "15px 0px 5px 0px"         ;; top right bottom left
                           :child     [(:panel (util/item-for-id @selected-tab-id tabs-definition))]]]    ;; the tab panel to show, for the selected tab
                  ]])))


;; ---------------------------------------------------------------------------------------
;;  EXPERIMENT START - TODO: REMOVE
;; ---------------------------------------------------------------------------------------

(defn green-box
  [markup]
  [:div
   {:style {:width            "200px"
            :height           "40px"
            :margin           "10px 0px 10px"
            :padding          "5px"
            :text-align       "center"
            :background-color "lightgreen"}}
   markup])

(defn green-message-box-bad
  [msg]
  [:div
   [:h3 "Component 1"]
   [green-box [:p "Message: " [:span @msg]]]])

(defn green-message-box-good
  [msg]
  [:div
   [:h3 "Component 2"]
   [green-box [:p "Message: " [(fn [] [:span @msg])]]]])

(defn main1
  [msg show?]
  [:div
   {:style {:padding "20px"}}
   [green-message-box-bad  msg]
   [green-message-box-good msg]
   [:br]
   [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
   [:span " ==> "]
   [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]])

(defn main2
  []
  (let [msg   (reagent/atom "initial text")
        show? (reagent/atom true)]
    (fn []
      [:div
       {:style {:padding "20px"}}
       [green-message-box-bad  msg]
       [green-message-box-good msg]
       [:br]
       [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
       [:span " ==> "]
       [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]])))

(defn display-green-messages
  []
  (let [msg   (reagent/atom "initial text")
        show? (reagent/atom true)]
    (fn []
      #_[:div
       {:style {:padding "20px"}}
       [green-message-box-bad  msg]
       [green-message-box-good msg]
       [:br]
       [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
       [:span " ==> "]
       [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]]

      #_[main1 msg show?]

      [main2]
      )))

;; ---------------------------------------------------------------------------------------
;;  EXPERIMENT END
;; ---------------------------------------------------------------------------------------


(fw/start {;; configure a websocket url if yor are using your own server
           ;; :websocket-url "ws://localhost:3449/figwheel-ws"

           ;; optional callback
           :jsload-callback (fn [] (reagent/force-update-all))

           ;; CSS handled automatically from project.clj > :figwheel > :css-dirs
           ;:on-cssload (fn [] (println "figwheel CSS updated"))

           ;; The heads up display is enabled by default
           ;; to disable it:
           ;; :heads-up-display false

           ;; when the compiler emits warnings figwheel
           ;; blocks the loading of files.
           ;; To disable this behavior:
           ;; :load-warninged-code true
           })

(defn ^:export  mount-demo
  []
  (reagent/render-component [main] (util/get-element-by-id "app"))
  ;(reagent/render-component [display-green-messages] (util/get-element-by-id "app")) ;; TODO: EXPERIMENT - REMOVE
  )   ;; 0.5.0 rename render-component ==> render.
