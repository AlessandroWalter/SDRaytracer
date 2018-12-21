package main.de.unitrier.fst.alessandrowalter.sdraytracer;

import java.util.List;

public class Figures {

    private Vec3D p1,p2,p3;
    private Light color;
    private Vec3D normal;
    private float shininess;

    Figures(Vec3D pp1, Vec3D pp2, Vec3D pp3, Light col, float sh)
    { p1=pp1; p2=pp2; p3=pp3; color=col; shininess=sh;
        Vec3D e1=p2.minus(p1),
                e2=p3.minus(p1);
        normal=e1.cross(e2);
        normal.normalize();
    }

    static void addCube(List<Figures> triangles, int x, int y, int z, int w, int h, int d, Light c, float sh)
    {  //front
        triangles.add(new Figures(new Vec3D(x,y,z), new Vec3D(x+w,y,z), new Vec3D(x,y+h,z), c, sh));
        triangles.add(new Figures(new Vec3D(x+w,y,z), new Vec3D(x+w,y+h,z), new Vec3D(x,y+h,z), c, sh));
        //left
        triangles.add(new Figures(new Vec3D(x,y,z+d), new Vec3D(x,y,z), new Vec3D(x,y+h,z), c, sh));
        triangles.add(new Figures(new Vec3D(x,y+h,z), new Vec3D(x,y+h,z+d), new Vec3D(x,y,z+d), c, sh));
        //right
        triangles.add(new Figures(new Vec3D(x+w,y,z), new Vec3D(x+w,y,z+d), new Vec3D(x+w,y+h,z), c, sh));
        triangles.add(new Figures(new Vec3D(x+w,y+h,z), new Vec3D(x+w,y,z+d), new Vec3D(x+w,y+h,z+d), c, sh));
        //top
        triangles.add(new Figures(new Vec3D(x+w,y+h,z), new Vec3D(x+w,y+h,z+d), new Vec3D(x,y+h,z), c, sh));
        triangles.add(new Figures(new Vec3D(x,y+h,z), new Vec3D(x+w,y+h,z+d), new Vec3D(x,y+h,z+d), c, sh));
        //bottom
        triangles.add(new Figures(new Vec3D(x+w,y,z), new Vec3D(x,y,z), new Vec3D(x,y,z+d), c, sh));
        triangles.add(new Figures(new Vec3D(x,y,z+d), new Vec3D(x+w,y,z+d), new Vec3D(x+w,y,z), c, sh));
        //back
        triangles.add(new Figures(new Vec3D(x,y,z+d),  new Vec3D(x,y+h,z+d), new Vec3D(x+w,y,z+d), c, sh));
        triangles.add(new Figures(new Vec3D(x+w,y,z+d), new Vec3D(x,y+h,z+d), new Vec3D(x+w,y+h,z+d), c, sh));

    }

    Vec3D getP1() {
        return p1;
    }

    void setP1(Vec3D p1) {
        this.p1 = p1;
    }

    Vec3D getP2() {
        return p2;
    }

    void setP2(Vec3D p2) {
        this.p2 = p2;
    }

    Vec3D getP3() {
        return p3;
    }

    void setP3(Vec3D p3) {
        this.p3 = p3;
    }

    Light getColor() {
        return color;
    }

    Vec3D getNormal() {
        return normal;
    }

    void setNormal(Vec3D normal) {
        this.normal = normal;
    }

    float getShininess() {
        return shininess;
    }
}
