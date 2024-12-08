# PokeMeng-OldManGO

可在「build.gradle.kts(Module :app)」檔案查詢與設定版本號

資料庫請統一使用「PokeMeng OldManGO」資料庫，不同模組請使用此資料庫下的不同集合。

v1 - 2024/?/? 建立專案，並加入娛樂遊戲與獎品兌換部分

v2 - 2024/?/? 加入每日任務與運動挑戰部分

v3 - 2024/09/06 將每日任務部分連接FireStore

v4 - 2024/09/13 加入個人資料部分，並整理所有檔案

v5 - 2024/09/13 加入定位、確認用戶為老人端 or 家長端 (MapMainActivity、login)，並連接到個人資料FireStore地址

v6 - 2024/09/13 測試

v7 - 2024/09/22 將運動挑戰部分連接FireStore，小修每日任務與運動挑戰

v8 - 2024/09/22 加入每日任務完成後積分獎勵，連結用戶積分

v9 - 2024/09/23 加入Gmail登入系統、登出系統、每日簽到，都已接上Firebase

v10 - 2024/09/24 修改Gmail登入系統、登出系統檔名

v11 - 2024/09/24 修改Gmail登入系統、每日簽到連接FireStore

v12 - 2024/09/24 修改Gmail登入系統、每日簽到連接FireStore(修回)

v13 - 2024/09/24 每日任務修改資料庫儲存方式

v14 - 2024/09/24 每日任務使用者獎勵、任務狀態修改資料庫儲存方式

v15 - 2024/09/25 修改專案名稱，並整理build.gradle.kts(Module :app)檔案

v16 - 2024/09/25 修改依賴項補充說明 每日簽到外觀修改

v17 - 2024/09/25 Google登入加上SHA-1憑證

v18 - 2024/09/25 定位系統完善 資料庫已連上 個人資料重新排版

v19 - 2024/09/25 解決衝突

v20 - 2024/09/25 整理Build.gradle.kts

v21 - 2024/09/25 個人資料資料庫已連結FireStore

v22 - 2024/09/26 主介面六大功能按鍵新增語音

v23 - 2024/09/26 Manifest修正、新增運動挑戰錯誤提示、整理Build.gradle.kts

v24 - 2024/09/27 運動挑戰步數紀錄修改資料庫儲存方式

v25 - 2024/09/28 版面更改、字體更換、新增封面、新款式按鈕效果以及六大功能語音調降為70%

V26 - 2024/09/28 每日簽到已可連接每日任務，主頁面連接每日簽到及登出

v27 - 2024/09/29 測試Google登入登出(帳號重新選擇)

v28 - 2024/09/30 個人資料完善另加可修改功能、一鍵清空功能

v29 - 2024/09/29 加入用藥

v30 - 2024/09/30 每日任務圖片更改、跳轉連結、運動挑戰日期更改

v31 - 2024/09/30 版面更新，獎品兌換待完成版面

v32 - 2024/09/30 修復運動挑戰紀錄錯誤、整理Build.gradle.kts及Manifest

v33 - 2024/10/01 版面完整更新

v34 - 2024/10/01 整理Theme及Manifest

v35 - 2024/10/01 修正每日任務下拉資料讀取問題

v36 - 2024/10/01 修正獎品兌換BUG與版面完善、個人資料填寫版面更改字體

v37 - 2024/10/02 新增各介面調用任務狀態查看與上傳功能、TaskAll版面修正、整理檔案名稱

v38 - 2024/10/09 每日任務及運動挑戰可以連接使用者UID，並上傳到相關用戶資料

v39 - 2024/10/09 修復運動挑戰錯誤、獎勵兌換可取得使用者目前積分

v40 - 2024/10/16 運動挑戰可上傳新活動資料，並將目前活動與未來活動資料呈現及應用在目標設定

v41 - 2024/10/17 用藥debug 加入資料庫

v41 - 2024/10/17 用藥debug 加入資料庫

v42 - 2024/10/18 每日簽到加入積分、簽到後按鈕鎖死

v43 - 2024/10/18 獎勵兌換資料庫成功連結FireStore與每日任務連動、登入介面程式碼修改

v44 - 2024/10/22 每日簽到加入積分(修改)

v45 - 2024/10/31 運動挑戰可對每季活動分別判斷狀態、存儲完成用戶、每日任務版面及Manifest修正
！因前幾版實體機無法測試！

v46 - 2024/11/12 用藥連任務 加入回診提醒功能

v47 - 2024/11/19 修復每日任務可顯示不同日期的固定任務狀態
請修正TODO的部分

v48 - 2024/11/30 修正用藥細節

v49 - 2024/12/07 完整修復運動挑戰

v50 - 2024/12/07 修正個人資料

v51 - 2024/12/08 部分版面調整，觀看運動影片任務回傳、定位系統資料庫回傳正確、電鍋圖片修改、用藥提醒功能部分按鈕圖示更換

v52 - 2024/12/08 修正用藥UID