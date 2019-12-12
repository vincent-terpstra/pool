package com.vdt.poolgame.game.draw;

import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;

/**
 * @author Vincent Terpstra
 * @date 20-08-2019
 * @file SunkDisplay.java
 *  displays a icon on the screen when a Poolball is sunk
 */

public class SunkDisplay {
    private Corner[] corners = new Corner[] {
            new Corner(-1,-1), new Corner(0,-1), new Corner(1,-1),
            new Corner(-1, 1), new Corner(0, 1), new Corner(1, 1)
    };

    public void draw(TableDraw shader){
        for(Corner c : corners){
            c.draw(shader);
        }
    }

    public void zero() {
        for(Corner c : corners)
            c.idx = 0;
    }


    public void add(int ballID, int flipX, int flipY){
        corners[flipX + 1 + ((flipY + 1) / 2 * 3)].add(ballID);
    }

    private PointXY
            angle32 = new PointXY().degrees(32),
            angle16 = new PointXY().degrees(16);

    private class Corner {
        int x, y;
        float dx, dy;
        int[] sunkList = new int[3];

        int idx = 0;

        Corner(int _x, int _y){
            x =  _x;
            y =  _y;

            dx = PoolTable.WIDTH * _x;
            dy = PoolTable.HEIGHT * _y;

            if(_x == 0)
                dy += _y;
        }

        void draw(TableDraw shader){
            float rad = -3.5f;
            PointXY deg = new PointXY().set(x * rad, y * rad);
            if(x == 0)
                deg.set(0, y * -5f);

            if(idx == 3)
                deg.rotateCC(angle32);
            if(idx == 2)
                deg.rotateCC(angle16);


            for(int j = 0; j < idx; j++) {
                int idx = sunkList[j];
                shader.drawSunk(idx, dx + deg.x(), dy + deg.y(), 2.5f);
                deg.rotate(angle32);
            }
        }

        void add(int num){
            if(idx < sunkList.length){
                sunkList[idx++] = num;
            }
        }

    }
}
