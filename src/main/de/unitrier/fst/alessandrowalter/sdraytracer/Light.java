/**
 * Alessandro Walter - 1450293
 */

package main.de.unitrier.fst.alessandrowalter.sdraytracer;

import java.awt.*;

public class Light {

    private Vec3D position;
    private float red,green,blue;
    private Color color;

    // Light
    Light(Vec3D pos, float r, float g, float b) {
        position=pos;
        if (r>1) r=1; else if (r<0) r=0;
        if (g>1) g=1; else if (g<0) g=0;
        if (b>1) b=1; else if (b<0) b=0;
        red=r; green=g; blue=b;
    }
    // RGB
    Light(float r, float g, float b)
    { if (r>1) r=1; else if (r<0) r=0;
        if (g>1) g=1; else if (g<0) g=0;
        if (b>1) b=1; else if (b<0) b=0;
        red=r; green=g; blue=b;
    }

    Vec3D getPosition() {
        return position;
    }

    Color color()
    { if (color!=null) return color;
        color=new Color((int) (red*255),(int) (green*255), (int) (blue*255));
        return color;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public void setPosition(Vec3D pos){
        this.position = pos;
    }
}
