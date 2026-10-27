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

public class GameView {
	private static final int jframeWidth = 405; // 視窗寬高
	private static final int jframeHeight = 530;
	private static int score = 0;

	Font topicFont = new Font("微軟正黑體", Font.BOLD, 50); // 主題字體
	Font scoreFont = new Font("微軟正黑體", Font.BOLD, 28); // 得分字體
	Font explainFont = new Font("微軟正黑體", Font.PLAIN, 20); // 提示字體

	private JFrame jframeMain;
	private JLabel jlblTitle;
	private JLabel jlblScoreName;
	private JLabel jlblScore;
	private JLabel jlblTip;
	private GameBoard gameBoard;

	public GameView() {
		init();
	}

	public void init() {
		// 1.建立視窗
		jframeMain = new JFrame("2048");
		jframeMain.setSize(jframeWidth, jframeHeight);
		jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframeMain.setLocationRelativeTo(null);// 視窗顯示位置居中
		jframeMain.setResizable(false);
		jframeMain.setLayout(null);// 設置絕對布局，以便後面可以用setBounds設置位置

		jlblTitle = new JLabel("2048", JLabel.CENTER);
		jlblTitle.setFont(topicFont);
		jlblTitle.setForeground(Color.black);
		jlblTitle.setBounds(50, 0, 150, 60);
		jframeMain.add(jlblTitle);

		// 2.視窗內容
		// 字體顏色及位置
		jlblScoreName = new JLabel("得 分", JLabel.CENTER);
		jlblScoreName.setFont(scoreFont);
		jlblScoreName.setForeground(Color.white);
		jlblScoreName.setOpaque(true);
		jlblScoreName.setBackground(Color.gray);
		jlblScoreName.setBounds(250, 0, 120, 30);
		jframeMain.add(jlblScoreName);

		// 3.得分區
		jlblScore = new JLabel("0", JLabel.CENTER);
		jlblScore.setFont(scoreFont);
		jlblScore.setForeground(Color.white);
		jlblScore.setOpaque(true);
		jlblScore.setBackground(Color.gray);
		jlblScore.setBounds(250, 30, 120, 30);
		jframeMain.add(jlblScore);

		// 4.提示說明區
		jlblTip = new JLabel("操作：↑ ↓ ← →，按ESC鍵重新開始　", JLabel.CENTER);
		jlblTip.setFont(explainFont);
		jlblTip.setForeground(Color.DARK_GRAY);
		jlblTip.setBounds(0, 60, 400, 40);
		jframeMain.add(jlblTip);

		// 5.主遊戲面板
		gameBoard = new GameBoard();
		gameBoard.setBounds(0, 100, 400, 400);
		gameBoard.setBackground(Color.gray);
		gameBoard.setFocusable(true);// 設立焦點在正在操作的物件
		gameBoard.setLayout(new FlowLayout());
		jframeMain.add(gameBoard);
	}

	// 遊戲面板
	class GameBoard extends JPanel implements KeyListener {
		private static final long serialVersionUID = 1L;
		
		private static final int CHECK_GAP = 10;// 方格之間的空隙
		private static final int CHECK_SIZE = 85;// 方格大小
		private static final int CHECK_ARC = 20;// 方格弧度

		private Check[][] checks = new Check[4][4];
		private boolean isadd = true;

		public GameBoard() {
			initGame();
			addKeyListener(this);
		}

		private void initGame() {
		    // 重設遊戲分數
		    score = 0;

		    // 若分數標籤已建立，立即更新畫面上的分數
		    if (jlblScore != null) {
		        jlblScore.setText("0");
		    }
		    
			for (int indexRow = 0; indexRow < 4; indexRow++) {
				for (int indexCol = 0; indexCol < 4; indexCol++) {
					checks[indexRow][indexCol] = new Check();
				}
			}			
			// 一開始生成兩個數
			isadd = true;
			createCheck();
			isadd = true;
			createCheck();
		}

		@Override
		public void keyPressed(KeyEvent e) {
		    // 記錄是否按下方向鍵
		    boolean directionPressed = true;

		    switch (e.getKeyCode()) {
		        case KeyEvent.VK_ESCAPE:
		            // 重新開始遊戲
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
		            // 其他按鍵不執行移動
		            directionPressed = false;
		            break;
		    }
		    
		    if (directionPressed) {
		        // 根據本次移動結果決定是否產生新方塊
		        createCheck();
		        // 移動與合併完成後更新分數顯示
		        jlblScore.setText(String.valueOf(score));
		    }
		    // 重新繪製遊戲畫面
		    repaint();
		}

		private void createCheck() {
			List<Check> list = getEmptyChecks();

			if (!list.isEmpty() && isadd) {
				Random random = new Random();
				int index = random.nextInt(list.size());
				Check check = list.get(index);
				// 產生數字 2 的機率為 75%，數字 4 為 25%
				check.value = random.nextInt(4) < 3 ? 2 : 4;
				isadd = false;
			}
		}

		// 獲取空白方格
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

		// 是否格子全滿，是則GameOver
		private boolean judgeGameOver() {		    
		    // 只要還有空格，就不算 Game Over
		    if (!getEmptyChecks().isEmpty()) {
		        return false;
		    }

		    // 檢查水平方向是否還可以合併
		    for (int i = 0; i < 4; i++) {
		        for (int j = 0; j < 3; j++) {
		            if (checks[i][j].value == checks[i][j + 1].value) {
		                return false;
		            }
		        }
		    }

		    // 檢查垂直方向是否還可以合併
		    for (int i = 0; i < 3; i++) {
		        for (int j = 0; j < 4; j++) {
		            if (checks[i][j].value == checks[i + 1][j].value) {
		                return false;
		            }
		        }
		    }
		    return true;
		}

		private void moveLeft() {
		    isadd = false;

		    for (int i = 0; i < 4; i++) {
		        int[] line = new int[4];
		        // 取得目前這一列
		        for (int j = 0; j < 4; j++) {
		            line[j] = checks[i][j].value;
		        }

		        // 整理並合併
		        int[] newLine = mergeLine(line);

		        // 判斷這次是否真的有移動
		        if (!Arrays.equals(line, newLine)) {
		            isadd = true;
		        }

		        // 寫回棋盤
		        for (int j = 0; j < 4; j++) {
		            checks[i][j].value = newLine[j];
		        }
		    }
		}

		private void moveRight() {
		    isadd = false;

		    for (int i = 0; i < 4; i++) {
		        int[] line = new int[4];

		        // 反方向取得這一列
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

		private void moveUp() {
		    isadd = false;

		    for (int i = 0; i < 4; i++) {
		        int[] line = new int[4];

		        // 取得目前這一欄
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

		private void moveDown() {
		    isadd = false;

		    for (int i = 0; i < 4; i++) {

		        int[] line = new int[4];

		        // 反方向取得這一欄
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
		
		private int[] mergeLine(int[] line) {
		    int[] result = new int[4];

		    int writeIndex = 0;
		    int previousValue = 0;

		    // 依序讀取原本的一列
		    for (int value : line) {
		        // 跳過空白方格
		        if (value == 0) {
		            continue;
		        }
		        
		        // 如果前一個數字與目前數字相同，就合併
		        if (previousValue == value) {
		            int mergedValue = value * 2;
		            result[writeIndex++] = mergedValue;
		            // 分數增加本次合併後的數值
		            score += mergedValue;
		            // 清除暫存，避免同一次移動重複合併
		            previousValue = 0;
		        } else {
		            // 如果已有尚未合併的數字，先放入結果
		            if (previousValue != 0) {
		                result[writeIndex++] = previousValue;
		            }
		            // 暫存目前數字，等待下一個數字比較
		            previousValue = value;
		        }
		    }

		    // 處理最後一個尚未寫入的數字
		    if (previousValue != 0) {
		        result[writeIndex] = previousValue;
		    }
		    return result;
		}

		@Override
		protected void paintComponent(Graphics g) {
		    // 先讓 JPanel 繪製背景
		    super.paintComponent(g);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					drawCheck(g, i, j);
				}
			}

			// GameOver
			if (judgeGameOver()) {
				g.setColor(new Color(64, 64, 64, 100));// RGBA最後一個A為透明度
				g.fillRect(0, 0, getWidth(), getHeight());// 填充矩形(遊戲面板)，將黑色填充上去
				g.setColor(Color.WHITE);
				g.setFont(topicFont);
				FontMetrics fms = getFontMetrics(topicFont);
				String value = "Game Over!";
				g.drawString(value, (getWidth() - fms.stringWidth(value)) / 2, getHeight() / 2);
			}
		}

		// 繪製方格
		private void drawCheck(Graphics g, int i, int j) {
			Graphics2D g2 = (Graphics2D) g;
			// 計算和消除文字鋸齒
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			// 獲取方格
			Check check = checks[i][j];
			// 不同數字設定背景色
			g2.setColor(check.getBackground());
			// 繪製圓角
			g2.fillRoundRect(CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j, CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i,
					CHECK_SIZE, CHECK_SIZE, CHECK_ARC, CHECK_ARC);
			// 繪製字體及字體顏色
			g2.setColor(check.getForeground());
			g2.setFont(check.getCheckFont());

			// 文字測量，並繪製文字
			FontMetrics fms = getFontMetrics(check.getCheckFont());
			String value = String.valueOf(check.value);
			g2.drawString(value, CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j + (CHECK_SIZE - fms.stringWidth(value)) / 2,
					CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i + (CHECK_SIZE - fms.getAscent() - fms.getDescent()) / 2
							+ fms.getAscent());// 讓數字居中顯示
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}

	public void showView() {
	    // 顯示遊戲視窗
	    jframeMain.setVisible(true);

	    // 視窗顯示後，要求遊戲面板取得鍵盤焦點
	    // 避免使用者必須先點擊遊戲面板才能操作方向鍵
	    gameBoard.requestFocusInWindow();
	}
}
