/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jyendor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;

/**
 *
 * @author otso
 */
public class Game implements Runnable {

    public final int WIDTH_PIXELS = 600;
    public final int HEIGHT_PIXELS = 400;
    public final int WIDTH_CELLS = 60;
    public final int HEIGHT_CELLS = 40;

    public final float TIME_BETWEEN_MOVING = 35; //ms
    public final int REFRESH_RATE = 59;
    public long timeSinceMoving = 9999;
    public long lastMoveTime = 0;

    public final int WORM_START_LENGTH = 4;
    public final int NUMBER_OF_EATABLES = 3;

    public boolean playing = true;
    public AtomicBoolean movingRight = new AtomicBoolean(false);
    public AtomicBoolean movingLeft = new AtomicBoolean(false);
    public AtomicBoolean movingUp = new AtomicBoolean(true);
    public AtomicBoolean movingDown = new AtomicBoolean(false);
    private boolean ate = false;
    private boolean changedDirectionThisTurn = false;
    public Painter painter;
    private Random random = new Random();
    private Thread thread;

    private final String TITLE = "Nibbles3";

    private final ConcurrentLinkedDeque<Coordinate> worm = new ConcurrentLinkedDeque<>();
    private ArrayList<Coordinate> eatables = new ArrayList<>();
    int fps = 0;
    long lastFPSCheck;
    JFrame frame;
    int count = 0;

    public Game() {
        frame = new JFrame(TITLE);
        frame.setSize(new Dimension(WIDTH_PIXELS + 6, HEIGHT_PIXELS + 28));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        painter = new Painter(WIDTH_PIXELS, HEIGHT_PIXELS, WIDTH_CELLS, HEIGHT_CELLS, this);
        frame.add(painter);
        frame.validate();
        createWorm();
        createNewEatable();

        lastMoveTime = System.currentTimeMillis();
        lastFPSCheck = System.nanoTime();
        thread = new Thread(this, "Nibbles");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    @Override
    public void run() {
        while (playing) {
            tick();
            painter.repaint();
            Toolkit.getDefaultToolkit().sync();
            fps++;
            if (System.nanoTime() - lastFPSCheck > 1000000000) {
                System.out.println("FPS: " + fps);
                fps = 0;
                lastFPSCheck = System.nanoTime();
            }
            try {
                Thread.sleep(1000 / REFRESH_RATE);
            } catch (InterruptedException ex) {
                ex.getStackTrace();
            }
        }
    }

    private void tick() {
        count++;
        if (System.currentTimeMillis() - lastMoveTime > TIME_BETWEEN_MOVING) {
            lastMoveTime = System.currentTimeMillis();
            count = 0;
            moveWorm();
            checkIfAte();
            changedDirectionThisTurn = false;
        }
    }

    private void moveWorm() {
        if (ate) {
            ate = false;
        } else {
            worm.remove();
        }
        Coordinate head = worm.peekLast();

        int newX = head.x;
        int newY = head.y;
        if (movingRight.get()) {
            newX++;
            if (newX > WIDTH_CELLS - 1) {
                newX = 0;
            }
        }
        if (movingLeft.get()) {
            newX--;
            if (newX < 0) {
                newX = WIDTH_CELLS - 1;
            }
        }
        if (movingUp.get()) {
            newY--;
            if (newY < 0) {
                newY = HEIGHT_CELLS - 1;
            }
        }
        if (movingDown.get()) {
            newY++;
            if (newY > HEIGHT_CELLS - 1) {
                newY = 0;
            }
        }

        worm.add(new Coordinate(newX, newY));

    }

    private void createWorm() {
        int startX = WIDTH_CELLS / 2;
        int startY = HEIGHT_CELLS / 2 - WORM_START_LENGTH / 2;
        for (int y = 0; y < WORM_START_LENGTH; y++) {
            worm.offer(new Coordinate(startX, startY + y - WORM_START_LENGTH));
        }
    }

    private void checkIfAte() {
        Coordinate head = worm.peekLast();
        for (Coordinate eatable : eatables) {
            if (head.x == eatable.x && head.y == eatable.y) {
                ate = true;
                eatables.remove(eatable);
                createNewEatable();
                break;
            }
        }
    }

    public void resetMovingDirections() {
        movingUp.set(false);
        movingDown.set(false);
        movingLeft.set(false);
        movingRight.set(false);
    }

    public void setMoveDirectionToUp() {
        if (!movingDown.get() && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingUp.set(true);
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToDown() {
        if (!movingUp.get() && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingDown.set(true);
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToLeft() {
        if (!movingRight.get() && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingLeft.set(true);
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToRight() {
        if (!movingLeft.get() && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingRight.set(true);
            changedDirectionThisTurn = true;
        }
    }

    public void createNewEatable() {

        while (eatables.size() < NUMBER_OF_EATABLES) {
            boolean isCreated = false;
            while (!isCreated) {
                boolean isValid = true;
                Coordinate potentialEatable = new Coordinate(random.nextInt(WIDTH_CELLS - 2) + 1, random.nextInt(HEIGHT_CELLS - 2) + 1);
                for (Coordinate wormCell : worm) {
                    if (wormCell.x == potentialEatable.x && wormCell.y == potentialEatable.y) {
                        isValid = false;
                        break;
                    }
                }
                for (Coordinate existingEatable : eatables) {
                    if (existingEatable.x == potentialEatable.x && existingEatable.y == potentialEatable.y) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    eatables.add(potentialEatable);
                    isCreated = true;
                }
            }
        }
    }

    public ConcurrentLinkedDeque<Coordinate> getWorm() {
        return worm;
    }

    public ArrayList<Coordinate> getEatables() {
        return eatables;
    }

}
