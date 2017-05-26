/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jyendor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public final float TIME_BETWEEN_MOVING = 60; //ms
    public final int REFRESH_RATE = 60;
    public long timeSinceMoving = 9999;
    public long lastMoveTime = 0;

    public final int WORM_START_LENGTH = 4;

    public boolean playing = true;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = true;
    public boolean movingDown = false;
    private boolean ate = false;
    private boolean changedDirectionThisTurn = false;
    public Painter painter;
    private Random random = new Random();
    private Thread thread;
    
    private final String TITLE = "Nibbles3";

    private final LinkedList<Coordinate> worm = new LinkedList<>();
    private Coordinate eatable;
    int fps = 0;
    long lastFPSCheck;
    JFrame frame;
    int count = 0;
    
    public Game() {
        frame = new JFrame(TITLE);
        frame.setSize(new Dimension(WIDTH_PIXELS+6, HEIGHT_PIXELS+28));
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
        thread = new Thread(this, "Nibbles");
        thread.start();
    }

    @Override
    public void run() {
        while (playing) {
            tick();
            painter.repaint();
            Toolkit.getDefaultToolkit().sync();
            try {
                Thread.sleep(17);
            } catch (InterruptedException ex) {
                ex.getStackTrace();
            }
        }
    }

    private void tick() {
        count++;
        if (count>2) {
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
            worm.pop();
        }
        Coordinate head = worm.peekLast();

        int newX = head.x;
        int newY = head.y;
        if (movingRight) {
            newX++;
            if (newX > WIDTH_CELLS - 1) {
                newX = 0;
            }
        }
        if (movingLeft) {
            newX--;
            if (newX < 0) {
                newX = WIDTH_CELLS - 1;
            }
        }
        if (movingUp) {
            newY--;
            if (newY < 0) {
                newY = HEIGHT_CELLS - 1;
            }
        }
        if (movingDown) {
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
            worm.add(new Coordinate(startX, startY - y - WORM_START_LENGTH));
        }
    }

    private void checkIfAte() {
        Coordinate head = worm.peekLast();
        if (head.x == eatable.x && head.y == eatable.y) {
            ate = true;
            createNewEatable();
        }
    }

    public void resetMovingDirections() {
        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
    }

    public void setMoveDirectionToUp() {
        if (!movingDown && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingUp = true;
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToDown() {
        if (!movingUp && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingDown = true;
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToLeft() {
        if (!movingRight && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingLeft = true;
            changedDirectionThisTurn = true;
        }
    }

    public void setMoveDirectionToRight() {
        if (!movingLeft && !changedDirectionThisTurn) {
            resetMovingDirections();
            movingRight = true;
            changedDirectionThisTurn = true;
        }
    }

    public void createNewEatable() {
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
            if (isValid) {
                eatable = potentialEatable;
                isCreated = true;
            }
        }
    }

    public LinkedList<Coordinate> getWorm() {
        return worm;
    }

    public Coordinate getEatable() {
        return eatable;
    }

}
