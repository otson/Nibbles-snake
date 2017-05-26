/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jyendor;

import javax.swing.SwingUtilities;

/**
 *
 * @author otso
 */
public class Nibbles {

    public Nibbles() {
        new Game();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Nibbles();
            }
        });

    }
}
