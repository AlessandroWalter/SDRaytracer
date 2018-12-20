class Triangle{
    private Vec3D p1,p2,p3;
    private RGB color;
    private Vec3D normal;
    private float shininess;

    Triangle(Vec3D pp1, Vec3D pp2, Vec3D pp3, RGB col, float sh)
    { p1=pp1; p2=pp2; p3=pp3; color=col; shininess=sh;
        Vec3D e1=p2.minus(p1),
                e2=p3.minus(p1);
        normal=e1.cross(e2);
        normal.normalize();
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

    RGB getColor() {
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