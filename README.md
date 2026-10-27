# Java 2048 遊戲

使用 Java Swing 製作的桌面版 2048 數字益智遊戲。透過方向鍵移動方塊，將相同數字合併並累積分數。

> 本專案為 Java 物件導向與 GUI 事件處理的實作作品。可執行 JAR 將於完成驗收後提供於 GitHub Releases。

## 遊戲畫面

<!-- 完成實際遊戲截圖後，將圖片存為 screenshots/game.png，再取消下一行註解。 -->
![Java 2048 遊戲實際執行畫面](screenshots/game.png)

## 主要功能

- 4 × 4 遊戲棋盤
- 方向鍵（↑ ↓ ← →）控制方塊移動
- 相同數字合併並累積分數
- 只有有效移動才會產生新方塊
- ESC 重新開始
- 無法繼續移動時顯示 Game Over

## 使用技術

- Java（Eclipse 專案執行環境設定為 JavaSE-17）
- Java Swing / AWT
- 物件導向程式設計（OOP）
- 鍵盤事件處理與自訂繪圖

## 操作方式

| 按鍵 | 功能 |
| --- | --- |
| ↑ / ↓ / ← / → | 移動方塊 |
| ESC | 重新開始 |

## 執行方式

### 使用 Eclipse

1. 下載或複製本專案。
2. 使用 Eclipse 匯入現有 Java 專案，或建立 Java 專案並保留 `src/Main/` 原始碼結構。
3. 確認專案的 Java 執行環境相容（建議 Java 17）。
4. 執行 `Main.Main` 類別中的 `main()` 方法。

### 使用可執行 JAR（規劃中）

完成正式發布後，從 GitHub Releases 下載 `Java2048.jar`，並在已安裝相容 Java 執行環境的電腦執行：

```bash
java -jar Java2048.jar
```

**注意：目前尚未發布 JAR，以上為預計的使用方式。**

## 專案架構

```text
src/
└── Main/
    ├── Main.java       # 程式入口
    ├── GameView.java   # Swing 介面、鍵盤操作、棋盤與合併邏輯
    └── Check.java      # 方格數值、顏色與字型
```

架構說明參見 [docs/architecture.md](docs/architecture.md)。

## 遊戲規則示例

向左移動時：

```text
[2, 2, 2, 2] → [4, 4, 0, 0]  （增加 8 分）
[2, 2, 4, 0] → [4, 4, 0, 0]  （增加 4 分）
```

同一方塊在單次移動中最多合併一次。

## 開發與驗收狀態

使用者已確認遊戲可以在 Eclipse 正常執行。特定邊界案例、自動化測試與 JAR 發布仍待進一步驗證；本 README 不代表上述項目已測試通過。
