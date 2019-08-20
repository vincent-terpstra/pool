package com.vdt.poolgame.game.table;

import com.vdt.poolgame.game.draw.TableShader;
import com.vdt.poolgame.library.PointXY;

public class Edge implements PoolTable.TableObject {
    final PointXY first, angle;
    private final float length;
    public Edge(PointXY first, PointXY second) {
    	this(first.x(), first.y(), second.x(), second.y());
    }
    public Edge(float x0, float y0, float x1, float y1){
        first = new PointXY().set(x0, y0);
        angle = new PointXY().set(x1, y1).move(-1, first);
        length = angle.normalize();
    }

    public void draw(TableShader shader){
        shader.drawLine(first, angle, length, .1f);
    //	shader.drawCircle(first, .5f);
    //	shader.drawCircle(first.clone().move(length, angle), .5f);
    }

    public boolean checkCollide(PoolBall ball){
        PointXY tmp = new PointXY().set(ball).move(-1, first).rotate(angle);
        PointXY _vel = new PointXY().set(ball.velocity).rotate(angle);
        float x = tmp.x() - Math.min(length, Math.max(0,tmp.x()));

        if(x * x + tmp.y() * tmp.y() < 1
            && tmp.y() * _vel.y() < 0){
            _vel.scale(1, -1).rotateCC(angle);
            ball.velocity.set(_vel);
            return true;
        }
        return false;
    }

}
