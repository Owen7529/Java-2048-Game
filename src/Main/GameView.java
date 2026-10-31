package Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 遊戲的主要視窗控制類別。
 * 使用 JFrame 顯示標題、分數與操作提示，並以內部類別 GameBoard 管理棋盤。
 */
public class GameView {
    // Swing 視窗大小，以像素為單位；固定尺寸由目前的絕對定位版面配合。
    private static final int jframeWidth = 405;
    private static final int jframeHeight = 530;
    // 目前遊戲分數；每一次合併時由 mergeLine() 累加。
    private static int score = 0;

    // 畫面使用的三種文字樣式：標題、分數與操作提示。
    Font topicFont = new Font("微軟正黑體", Font.BOLD, 50);
    Font scoreFont = new Font("微軟正黑體", Font.BOLD, 28);
    Font explainFont = new Font("微軟正黑體", Font.PLAIN, 20);

    // 主視窗及各個標籤；gameBoard 是負責遊戲繪圖和鍵盤控制的面板。
    private JFrame jframeMain;
    private JLabel jlblTitle;
    private JLabel jlblScoreName;
    private JLabel jlblScore;
    private JLabel jlblTip;
    private GameBoard gameBoard;

    // 建立 GameView 時配置畫面元件，但由 showView() 決定何時顯示。
    public GameView() {
        init();
    }

    /**
     * 建立並設定所有 Swing 視覺元件。
     */
    public void init() {
        // 建立主視窗，設定關閉行為、中央位置及不可調整尺寸。
        jframeMain = new JFrame("2048");
        jframeMain.setSize(jframeWidth, jframeHeight);
        jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframeMain.setLocationRelativeTo(null);
        jframeMain.setResizable(false);
        jframeMain.setLayout(null);

        // 左上方的遊戲標題。
        jlblTitle = new JLabel("2048", JLabel.CENTER);
        jlblTitle.setFont(topicFont);
        jlblTitle.setForeground(Color.black);
        jlblTitle.setBounds(50, 0, 150, 60);
        jframeMain.add(jlblTitle);

        // 右上方分數欄的標題，Opaque 讓 JLabel 可以顯示設定的底色。
        jlblScoreName = new JLabel("得 分", JLabel.CENTER);
        jlblScoreName.setFont(scoreFont);
        jlblScoreName.setForeground(Color.white);
        jlblScoreName.setOpaque(true);
        jlblScoreName.setBackground(Color.gray);
        jlblScoreName.setBounds(250, 0, 120, 30);
        jframeMain.add(jlblScoreName);

        // 分數數值會在每次有效的方向鍵處理後同步更新。
        jlblScore = new JLabel("0", JLabel.CENTER);
        jlblScore.setFont(scoreFont);
        jlblScore.setForeground(Color.white);
        jlblScore.setOpaque(true);
        jlblScore.setBackground(Color.gray);
        jlblScore.setBounds(250, 30, 120, 30);
        jframeMain.add(jlblScore);

        // 顯示使用者可操作的方向鍵和重新開始快捷鍵。
        jlblTip = new JLabel("操作：↑ ↓ ← →，按ESC鍵重新開始　", JLabel.CENTER);
        jlblTip.setFont(explainFont);
        jlblTip.setForeground(Color.DARK_GRAY);
        jlblTip.setBounds(0, 60, 400, 40);
        jframeMain.add(jlblTip);

        // 建立 400×400 的遊戲棋盤，並允許面板取得鍵盤焦點。
        gameBoard = new GameBoard();
        gameBoard.setBounds(0, 100, 400, 400);
        gameBoard.setBackground(Color.gray);
        gameBoard.setFocusable(true);
        gameBoard.setLayout(new FlowLayout());
        jframeMain.add(gameBoard);
    }

    /**
     * 遊戲棋盤：負責存放 4×4 方格、接收按鍵、移動合併、產生隨機數及繪製。
     * extends JPanel 表示本類別是一個可自行繪圖的 Swing 面板。
     * implements KeyListener 表示必須實作按下、輸入、放開三種鍵盤事件方法。
     */
    class GameBoard extends JPanel implements KeyListener {
        // JPanel 可序列化；此識別碼供 Java 序列化版本檢查使用。
        private static final long serialVersionUID = 1L;

        // 每格大小與間距：4 格 × 85 + 5 處 × 10 = 390，置於 400 像素面板內。
        private static final int CHECK_GAP = 10;
        private static final int CHECK_SIZE = 85;
        private static final int CHECK_ARC = 20; // 圓角的水平與垂直弧度。

        // checks[列][欄]：每個位置都存放一個 Check 物件。
        private Check[][] checks = new Check[4][4];
        // 控制本次方向鍵操作後是否產生新方塊；未變動時不應增加方塊。
        private boolean isadd = true;

        /** 初始化棋盤內容，並將本面板註冊為鍵盤事件接收者。 */
        public GameBoard() {
            initGame();
            addKeyListener(this);
        }

        /**
         * 重設一局遊戲：分數歸零、建立 16 個空方格、隨機生成兩個起始數字。
         * 同時供初次啟動及使用者按 ESC 重新開始時使用。
         */
        private void initGame() {
            score = 0;
            // 建構 GameBoard 時分數標籤已建立；保留 null 檢查以防元件尚未就緒。
            if (jlblScore != null) {
                jlblScore.setText("0");
            }

            // 每一格重新建立物件，清除上一局的盤面。
            for (int indexRow = 0; indexRow < 4; indexRow++) {
                for (int indexCol = 0; indexCol < 4; indexCol++) {
                    checks[indexRow][indexCol] = new Check();
                }
            }
            // createCheck() 成功產生一格後會將 isadd 設為 false，因此需重新開啟。
            isadd = true;
            createCheck();
            isadd = true;
            createCheck();
        }

        /**
         * 使用者按下鍵盤按鍵時由 Swing 呼叫。
         * 方向鍵先移動，再視棋盤是否改變產生新方塊，最後更新分數與重繪。
         * ESC 只重設遊戲，不被當作有效方向鍵。
         */
        @Override
        public void keyPressed(KeyEvent e) {
            boolean directionPressed = true;
            // 根據事件攜帶的鍵碼，選擇對應的移動方法。
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    initGame();
                    directionPressed = false;
                    break;
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight();
                    break;
                case KeyEvent.VK_UP:
                    moveUp();
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown();
                    break;
                default:
                    // 不處理其他按鍵，避免無意義操作產生新方塊。
                    directionPressed = false;
                    break;
            }

            if (directionPressed) {
                // 是否真的新增由 moveXXX() 更新的 isadd 決定。
                createCheck();
                jlblScore.setText(String.valueOf(score));
            }
            // repaint() 通知 Swing 稍後重新呼叫 paintComponent()。
            repaint();
        }

        /**
         * 在有效移動後，從全部空格隨機選一格放入 2 或 4。
         * random.nextInt(4) 產生 0～3；小於 3 的三種結果產生 2 (75%)，
         * 剩餘一種結果產生 4 (25%)。
         */
        private void createCheck() {
            List<Check> list = getEmptyChecks();
            if (!list.isEmpty() && isadd) {
                Random random = new Random();
                int index = random.nextInt(list.size());
                Check check = list.get(index);
                check.value = random.nextInt(4) < 3 ? 2 : 4;
                // 防止同次操作中重複新增；下一次 moveXXX() 會重新判斷。
                isadd = false;
            }
        }

        /**
         * 掃描整個棋盤，把 value 為 0 的方格物件收集起來。
         * @return 目前所有空白 Check 的清單。
         */
        private List<Check> getEmptyChecks() {
            List<Check> checkList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (checks[i][j].value == 0) {
                        checkList.add(checks[i][j]);
                    }
                }
            }
            return checkList;
        }

        /**
         * 2048 Game Over 的條件是：沒有空格，且任何相鄰的兩格都無法合併。
         * 單純「棋盤已滿」並不代表結束，因為相同鄰格仍能移動合併。
         * @return true 表示沒有任何合法移動；false 表示還能繼續。
         */
        private boolean judgeGameOver() {
            if (!getEmptyChecks().isEmpty()) {
                return false; // 還有空位就可以繼續遊戲。
            }
            
            // 依序比較每列相鄰兩欄；最後一欄沒有右側鄰格。
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    if (checks[i][j].value == checks[i][j + 1].value) {
                        return false;
                    }
                }
            }
            
            // 再比較每欄相鄰兩列；最後一列沒有下方鄰格。
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    if (checks[i][j].value == checks[i + 1][j].value) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * 向左：逐列由左到右取出數字，合併後按相同順序寫回。
         * 例如 [2, 0, 2, 4] 變為 [4, 4, 0, 0]。
         */
        private void moveLeft() {
            isadd = false;
            for (int i = 0; i < 4; i++) {
                int[] line = new int[4];
                for (int j = 0; j < 4; j++) {
                    line[j] = checks[i][j].value;
                }
                int[] newLine = mergeLine(line);
                // 有任何一列的輸出不同，就表示這次移動有效。
                if (!Arrays.equals(line, newLine)) {
                    isadd = true;
                }
                for (int j = 0; j < 4; j++) {
                    checks[i][j].value = newLine[j];
                }
            }
        }

        /**
         * 向右：以「最右側優先」讀出每列，再沿用向前壓縮的 mergeLine()。
         * 透過 [3 - j] 倒序讀寫，不需要另外實作一套右向合併規則。
         */
        private void moveRight() {
            isadd = false;
            for (int i = 0; i < 4; i++) {
                int[] line = new int[4];
                for (int j = 0; j < 4; j++) {
                    line[j] = checks[i][3 - j].value;
                }
                int[] newLine = mergeLine(line);
                if (!Arrays.equals(line, newLine)) {
                    isadd = true;
                }
                for (int j = 0; j < 4; j++) {
                    checks[i][3 - j].value = newLine[j];
                }
            }
        }

        /**
         * 向上：逐欄從上往下讀取，合併後寫回原欄。
         * checks[列][欄]，因此 checks[j][i] 代表固定欄 i、變動列 j。
         */
        private void moveUp() {
            isadd = false;
            for (int i = 0; i < 4; i++) {
                int[] line = new int[4];
                for (int j = 0; j < 4; j++) {
                    line[j] = checks[j][i].value;
                }
                int[] newLine = mergeLine(line);
                if (!Arrays.equals(line, newLine)) {
                    isadd = true;
                }
                for (int j = 0; j < 4; j++) {
                    checks[j][i].value = newLine[j];
                }
            }
        }

        /**
         * 向下：逐欄由底端往上讀取，合併後倒序寫回。
         * checks[3 - j][i] 讓最底下一格成為 mergeLine() 的第一格。
         */
        private void moveDown() {
            isadd = false;
            for (int i = 0; i < 4; i++) {
                int[] line = new int[4];
                for (int j = 0; j < 4; j++) {
                    line[j] = checks[3 - j][i].value;
                }
                int[] newLine = mergeLine(line);
                if (!Arrays.equals(line, newLine)) {
                    isadd = true;
                }
                for (int j = 0; j < 4; j++) {
                    checks[3 - j][i].value = newLine[j];
                }
            }
        }

        /**
         * 核心合併演算法：將單列／單欄的數字往前壓縮，讓相同數字兩兩合併。
         * line 已依使用者移動方向調整順序，因此四方向共用此方法。
         * 例如 [2, 0, 2, 4] → [4, 4, 0, 0]，分數增加 4。
         *      [2, 2, 2, 2] → [4, 4, 0, 0]，分數增加 8。
         *      [4, 4, 8, 0] → [8, 8, 0, 0]，新的 8 不會立即再次合併。
         * @param line 本次要處理的四格原始數字（沿移動方向排列）。
         * @return 移除空格並完成一次兩兩合併後的四格結果。
         */
        private int[] mergeLine(int[] line) {
            int[] result = new int[4]; // int 預設為 0，尾端自然保留空白。
            int writeIndex = 0; // 下一個結果應寫入的位置。
            int previousValue = 0; // 尚未決定是否能與下一格合併的暫存數字。

            for (int value : line) {
                if (value == 0) {
                    continue; // 先略過空格，達到向前靠攏效果。
                }
                if (previousValue == value) {
                    // 相鄰的兩個非零數字相同，寫入合併後的新數字。
                    int mergedValue = value * 2;
                    result[writeIndex++] = mergedValue;
                    score += mergedValue; // 2048 每次合併的得分等於新數字。
                    // 歸零非常重要：已合併的方塊不可在同一步驟再合併。
                    previousValue = 0;
                } else {
                    if (previousValue != 0) {
                        // 前一個數字無法和目前數字合併，先確定它的位置。
                        result[writeIndex++] = previousValue;
                    }
                    previousValue = value; // 保留目前數字，等待與下一個比較。
                }
            }
            
            if (previousValue != 0) {
                // 迴圈結束時，最後一個尚未寫入的數字也要放入結果。
                result[writeIndex] = previousValue;
            }
            return result;
        }

        /**
         * Swing 需要重繪遊戲面板時呼叫。
         * 先清除舊背景，再逐格繪圖；若無法移動則覆蓋半透明 Game Over 提示。
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // 避免畫面殘影，先使用 JPanel 的背景繪製。
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    drawCheck(g, i, j);
                }
            }
            
            if (judgeGameOver()) {
                // Color 的第四參數 alpha = 100（範圍 0～255），產生半透明灰色遮罩。
                g.setColor(new Color(64, 64, 64, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.setFont(topicFont);
                FontMetrics fms = getFontMetrics(topicFont);
                String value = "Game Over!";
                // 使用字串實際像素寬度計算水平置中位置。
                g.drawString(value, (getWidth() - fms.stringWidth(value)) / 2, getHeight() / 2);
            }
        }

        /**
         * 繪製棋盤中指定列、欄的一個方格。
         * 背景色、數字顏色與字型都由 Check 依其 value 決定。
         * @param g Swing 提供的繪圖物件。
         * @param i 列索引（0～3）。
         * @param j 欄索引（0～3）。
         */
        private void drawCheck(Graphics g, int i, int j) {
            Graphics2D g2 = (Graphics2D) g;
            // 抗鋸齒設定使圓角與數字邊緣較平滑。
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            Check check = checks[i][j];
            g2.setColor(check.getBackground());
            // x 由欄 j 決定，y 由列 i 決定；各格間距相同。
            g2.fillRoundRect(CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j, CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i,
                    CHECK_SIZE, CHECK_SIZE, CHECK_ARC, CHECK_ARC);
            g2.setColor(check.getForeground());
            g2.setFont(check.getCheckFont());

            FontMetrics fms = getFontMetrics(check.getCheckFont());
            String value = String.valueOf(check.value);
            // 橫向扣除文字寬度的一半，縱向依 ascent/descent 修正基線位置。
            g2.drawString(value, CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j + (CHECK_SIZE - fms.stringWidth(value)) / 2,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i + (CHECK_SIZE - fms.getAscent() - fms.getDescent()) / 2
                            + fms.getAscent());
        }

        /** KeyListener 要求的事件方法；目前不使用字元輸入事件。 */
        @Override
        public void keyTyped(KeyEvent e) {
        }

        /** KeyListener 要求的事件方法；目前不使用按鍵放開事件。 */
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    /** 顯示遊戲主視窗，並讓棋盤取得鍵盤焦點以接收方向鍵。 */
    public void showView() {
        jframeMain.setVisible(true);
        // Swing 元件可見後才要求焦點，避免使用者必須先點擊面板。
        gameBoard.requestFocusInWindow();
    }
}
