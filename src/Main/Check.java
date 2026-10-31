package Main;

import java.awt.Color;
import java.awt.Font;

/**
 * 代表 2048 棋盤上的單一方格。
 * value = 0 表示空格，其他數值代表方格中的數字（2、4、8……）。
 * 這個類別提供繪圖時需要的文字顏色、背景顏色與字型。
 */
public class Check {
    // 方格中的數值，GameView 會直接讀取及修改。
    public int value;

    // 依數字位數準備不同大小的字型，避免四位數以上超出方格範圍。
    Font font1 = new Font("微軟正黑體", Font.BOLD, 46);
    Font font2 = new Font("微軟正黑體", Font.BOLD, 40);
    Font font3 = new Font("微軟正黑體", Font.BOLD, 34);
    Font font4 = new Font("微軟正黑體", Font.BOLD, 28);
    Font font5 = new Font("微軟正黑體", Font.BOLD, 22);

    // 建立方格時預設為空白。
    public Check() {
        value = 0; // 0 代表這一格目前沒有數字。
    }

    /**
     * 按目前數值選擇文字顏色。
     * 空格使用與空白方格相同的顏色，讓畫面上的 0 看不見。
     * @return 繪製數字所使用的 Color。
     */
    public Color getForeground() {
        switch (value) {
        case 0:
            return new Color(0xcdc1b4); // 與空格底色相同，隱藏 0。
        case 2:
        case 4:
            return Color.black; // 淺色背景搭配黑色字體。
        default:
            return Color.white; // 較大的數值採用白色文字。
        }
    }

    /**
     * 依數字回傳對應的方格背景顏色。
     * 顏色常數以十六進位 RGB 形式表示；未列出的更大數值使用預設顏色。
     * @return 方格底色。
     */
    public Color getBackground() {
        switch (value) {
        case 0:
            return new Color(0xcdc1b4); // 空白方格
        case 2:
            return new Color(0xeee4da);
        case 4:
            return new Color(0xede0c8);
        case 8:
            return new Color(0xf2b179);
        case 16:
            return new Color(0xf59563);
        case 32:
            return new Color(0xf67c5f);
        case 64:
            return new Color(0xf65e3b);
        case 128:
            return new Color(0xedcf72);
        case 256:
            return new Color(0xedcc61);
        case 512:
            return new Color(0xedc850);
        case 1024:
            return new Color(0xedc53f);
        case 2048:
            return new Color(0xedc22e);
        case 4096:
            return new Color(0x65da92);
        case 8192:
            return new Color(0x5abc65);
        case 16384:
            return new Color(0x248c51);
        default:
            return new Color(0x248c51); // 其他數值沿用最高級距顏色。
        }
    }

    /**
     * 按數字的位數決定字體大小。
     * 數字越長，字體越小，讓 GameView 可以把數字置中在方格內。
     * @return 當前方格適用的 Font。
     */
    public Font getCheckFont() {
        if(value < 10) {
            return font1; // 一位數
        }
        if(value < 100) {
            return font2; // 兩位數
        }
        if(value < 1000) {
            return font3; // 三位數
        }
        if(value < 10000) {
            return font4; // 四位數
        }
        return font5; // 五位數以上
    }
}
