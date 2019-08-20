package com.vdt.poolgame.game.table;

import com.vdt.poolgame.game.draw.TableShader;
import com.vdt.poolgame.library.PointXY;

public class Circle extends PointXY implements PoolTable.TableObject {

    private final float radius;
    private final float dist;
    public Circle(float radius){
        this.radius = radius;
        this.dist = (radius + 1) * (radius + 1);
    }

    public void draw(TableShader shader){
        shader.drawCircle(this, radius * 2);
    }

    public boolean checkCollide(PoolBall ball){
        PointXY normal = new PointXY().set(this).move( -1, ball);
        if(normal.dot(normal) < dist && normal.dot(ball.velocity) > 0){ //distance of ball & circle
            normal.normalize();
            ball.velocity
                    .rotate(normal)
                    .scale(-1,1)
                    .rotateCC(normal);
            return true;
        }
        return false;
    }
}
