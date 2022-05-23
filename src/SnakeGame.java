import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.util.*;

public class SnakeGame extends JFrame {
    Container c = getContentPane();
    public static SnakeGame window = new SnakeGame();
    public static Snake snake = new Snake();
    Timer t = new Timer();
    int appleX, appleY;

    enum gameStatus {
        Continue,
        Lost,
        Won
    };

    gameStatus status = gameStatus.Continue;

    public static void main(String[] args) {
        window.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        GlobalKeyboardHook h = new GlobalKeyboardHook(true);
        h.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                window.handleKeys(event.getVirtualKeyCode(), snake);
            }
        });
        for (int i = 0; i < snake.length; i++) {
            snake.x[i] = (int) (window.getHeight() / 2 + (i * 10)) / 10 * 10;
            snake.y[i] = (int) (window.getHeight() / 2) / 10 * 10;
            snake.facing[i] = 0;
        }
        window.Spawn();
        TimerTask tt = new TimerTask() {
            public void run() {
                window.UpdateSnake(snake);
            }
        };
        window.t.scheduleAtFixedRate(tt, 0, 100);
    }

    public SnakeGame() {
        super("Snake Game - By Andrew Langan");
        c.setBackground(Color.WHITE);
        c.setLayout(new FlowLayout());
        c.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void handleKeys(int keyCode, Snake snake) {
        int up = 38;
        int down = 40;
        int left = 37;
        int right = 39;
        int w = 87;
        int s = 83;
        int a = 65;
        int d = 68;
        int esc = 27;
        if ((keyCode == up || keyCode == w) && snake.facing[0] != 270) {
            snake.facing[0] = 90;
        } else if ((keyCode == down || keyCode == s) && snake.facing[0] != 90) {
            snake.facing[0] = 270;
        } else if ((keyCode == left || keyCode == a) && snake.facing[0] != 0) {
            snake.facing[0] = 180;
        } else if ((keyCode == right || keyCode == d) && snake.facing[0] != 180) {
            snake.facing[0] = 0;
        }
    }

    public void UpdateSnake(Snake snake) {
        Graphics g = c.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, window.getWidth(), window.getHeight());
        CheckCollision(snake);
        if (status == gameStatus.Continue) {
            if (snake.facing[0] == 0) {
                snake.x[0] += snake.speed;
            } else if (snake.facing[0] == 90) {
                snake.y[0] -= snake.speed;
            } else if (snake.facing[0] == 180) {
                snake.x[0] -= snake.speed;
            } else if (snake.facing[0] == 270) {
                snake.y[0] += snake.speed;
            }
            int headX = snake.x[0];
            int headY = snake.y[0];
            for (int i = 0; i < snake.length - 2; i++) {
                if (snake.facing[i] % 180 == 0) {
                    if (headY > snake.y[i]) {
                        snake.y[i] += snake.speed;
                        snake.facing[i] = 90;
                    } else if (headY < snake.y[i]) {
                        snake.y[i] -= snake.speed;
                        snake.facing[i] = 270;
                    } else if (headX > snake.x[i]) {
                        snake.x[i] += snake.speed;
                        snake.facing[i] = 0;
                    } else if (headX < snake.x[i]) {
                        snake.x[i] -= snake.speed;
                        snake.facing[i] = 180;
                    }
                } else {
                    if (headX > snake.x[i]) {
                        snake.x[i] += snake.speed;
                        snake.facing[i] = 0;
                    } else if (headX < snake.x[i]) {
                        snake.x[i] -= snake.speed;
                        snake.facing[i] = 180;
                    } else if (headY > snake.y[i]) {
                        snake.y[i] += snake.speed;
                        snake.facing[i] = 90;
                    } else if (headY < snake.y[i]) {
                        snake.y[i] -= snake.speed;
                        snake.facing[i] = 270;
                    }
                }
                headX = snake.x[i];
                headY = snake.y[i];
            }
            for (int i = snake.length - 1; i > -1; i--) {
                g2d.setPaint(Color.GREEN);
                g2d.fillRect(snake.x[i], snake.y[i], 10, 10);
            }
            g2d.setPaint(Color.RED);
            g2d.fillRect(appleX, appleY, 10, 10);
        } else {
            EndGame();
        }
    }

    public void CheckCollision(Snake snake) {
        if (snake.x[0] <= 0 || snake.x[0] >= c.getWidth()
                || snake.y[0] <= 0
                || snake.y[0] >= c.getHeight())
            status = gameStatus.Lost;
        for (int i = 0; i < snake.length; i++) {
            for (int j = 0; j < snake.length; j++) {
                if (i != j && (snake.x[i] == snake.x[j] && snake.y[i] == snake.y[j]))
                    status = gameStatus.Lost;
                System.out.println();
            }
            if (snake.x[i] == appleX && snake.y[i] == appleY) {
                Spawn();
                snake.length += 1;
                snake.x[snake.length - 1] = snake.x[snake.length - 2];
                snake.y[snake.length - 1] = snake.y[snake.length - 2];
                if (snake.facing[snake.length - 1] == 0) {
                    snake.x[snake.length - 1] -= snake.speed;
                } else if (snake.facing[snake.length - 1] == 90) {
                    snake.y[snake.length - 1] += snake.speed;
                } else if (snake.facing[snake.length - 1] == 180) {
                    snake.x[snake.length - 1] += snake.speed;
                } else if (snake.facing[snake.length - 1] == 270) {
                    snake.y[snake.length - 1] -= snake.speed;
                }
            }
        }

    }

    public void Spawn() {
        Graphics g = c.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        int width = window.getWidth();
        int height = window.getHeight();
        boolean inSnake = true;
        do {
            inSnake = false;
            appleX = (int) (Math.random() * (window.getWidth() - 100)) + 50;
            appleX /= 10;
            appleX *= 10;
            appleY = (int) (Math.random() * (window.getHeight() - 100)) + 50;
            appleY /= 10;
            appleY *= 10;
            for (int i = 0; i < snake.length - 1; i++) {
                if (appleX == snake.x[i] && appleY == snake.y[i]) {
                    inSnake = true;
                }
            }
        } while (inSnake);
        System.out.println(appleX);
        System.out.println(appleY);
        g2d.setPaint(Color.RED);
        g2d.fillRect(appleX, appleY, 10, 10);
    }

    public void EndGame() {
        Graphics g = c.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        Button exit = new Button("Exit");
        ActionListener exitListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (status == gameStatus.Lost) {
                    System.exit(1);
                } else {
                    System.exit(0);
                }
            }

        };
        if (status == gameStatus.Lost) {
            t.cancel();
            t.purge();
            repaint();
            g2d.setPaint(Color.RED);
            g2d.setFont(new Font("Arial", Font.PLAIN, 50));
            g2d.drawString("You Lost", (c.getWidth() - g2d.getFontMetrics().stringWidth("You Lost")) / 2,
                    (c.getHeight() - g2d.getFontMetrics().getHeight() - 20) / 2);
            exit.addActionListener(exitListener);
            exit.setBackground(Color.RED);
            exit.setForeground(Color.WHITE);
            exit.setSize(c.getWidth() / 10, c.getHeight() / 10);
            exit.setLocation(new Point((c.getWidth() - exit.getWidth()) / 2,
                    c.getHeight() / 2));
            c.add(exit);
        } else if (status == gameStatus.Won) {
            t.cancel();
            t.purge();
            repaint();
            g2d.setPaint(Color.GREEN);
            g2d.setFont(new Font("Arial", Font.PLAIN, 50));
            g2d.drawString("You Won", (c.getWidth() - g2d.getFontMetrics().stringWidth("You Won")) / 2,
                    (c.getHeight() - g2d.getFontMetrics().getHeight() - 20) / 2);
            exit.addActionListener(exitListener);
            exit.setBackground(Color.RED);
            exit.setForeground(Color.WHITE);
            exit.setSize(c.getWidth() / 10, c.getHeight() / 10);
            exit.setLocation(new Point((c.getWidth() - exit.getWidth()) / 2,
                    c.getHeight() / 2));
            c.add(exit);
        }
    }
}

class Snake {
    int length;
    double speed;
    int score;
    int[] facing = new int[256];
    int[] x = new int[256];
    int[] y = new int[256];

    Snake() {
        this.speed = 10.0;
        this.score = 0;
        this.length = 10;
    }
}