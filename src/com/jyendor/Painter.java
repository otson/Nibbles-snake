/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jyendor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author otso
 */
public class Painter extends JPanel implements KeyListener {

    

    public final int CELL_HEIGHT;
    public final int CELL_WIDTH;

    public final Color WORM_COLOR = Color.GREEN;
    public final Color BACKGROUND_COLOR = Color.BLACK;
    public final Color EATABLE_COLOR = Color.RED;
    private final Color WORM_HEAD_COLOR = Color.BLUE;
    private final Game game;

    Painter(int WIDTH_PIXELS, int HEIGHT_PIXELS, int WIDTH_CELLS, int HEIGHT_CELLS, Game game) {
        this.game = game;

        this.CELL_WIDTH = WIDTH_PIXELS / WIDTH_CELLS;
        this.CELL_HEIGHT = HEIGHT_PIXELS / HEIGHT_CELLS;
        setFocusable(true);
        requestFocus();
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH_PIXELS, HEIGHT_PIXELS));
        setVisible(true);
        validate();
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        // Clear board
        super.paintComponent(g);
        // Draw worm
        g.setColor(WORM_COLOR);
        Coordinate head = game.getWorm().peekLast();
        for (Coordinate wormCell : game.getWorm()) {
            if (wormCell == head) {
                g.setColor(WORM_HEAD_COLOR);
            }
            g.fillRect(wormCell.x * CELL_WIDTH, wormCell.y * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
        }
        // Draw eatable
        if (game.getEatable() != null) {
            Coordinate eatable = game.getEatable();
            g.setColor(EATABLE_COLOR);
            g.fillRect(eatable.x * CELL_WIDTH, eatable.y * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                game.setMoveDirectionToUp();
                break;
            case KeyEvent.VK_DOWN:
                game.setMoveDirectionToDown();
                break;
            case KeyEvent.VK_LEFT:
                game.setMoveDirectionToLeft();
                break;
            case KeyEvent.VK_RIGHT:
                game.setMoveDirectionToRight();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
