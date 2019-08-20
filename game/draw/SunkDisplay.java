package com.vdt.poolgame.game.draw;

import com.vdt.poolgame.library.SpriteArray;

/**
 * @author Vincent Terpstra
 * @date 20-08-2019
 * @file SunkDisplay.java
 *  displays a icon on the screen when a poolball is sunk
 */

public class SunkDisplay {
    final float[] solid, stripe;

    int idx = 0;
    int[] sunkList = new int[16];

    public SunkDisplay(SpriteArray array){
        solid = array.get("solid");
        stripe = array.get("stripe");

        reset();
    }

    public void draw(TableShader shader){
        final float space = 5;
        float x = -(idx -1) * space * .5f;
        for(int j = 0; j < idx; j++) {
            shader.draw(sunkList[j] > 8 ? stripe : solid, x, 0, 1, 0, space, space);
            x += space;
        }
    }

    public void reset(){
        idx = 0;
    }

    public void add(int ballID){
        sunkList[idx++] = ballID;
    }
}
