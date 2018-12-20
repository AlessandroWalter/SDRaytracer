class Ray {
    private Vec3D start=new Vec3D(0,0,0);
    private Vec3D dir=new Vec3D(0,0,0);

    void setStart(float x, float y, float z) { start=new Vec3D(x,y,z); }
    void setDir(float dx, float dy, float dz) { dir=new Vec3D(dx, dy, dz); }
    void normalize() {  dir.normalize(); }

    // see M�ller&Haines, page 305
    IPoint intersect(Triangle t)
    { float epsilon=IPoint.epsilon;
        Vec3D e1 = t.getP2().minus(t.getP1());
        Vec3D e2 = t.getP3().minus(t.getP1());
        Vec3D p =  dir.cross(e2);
        float a = e1.dot(p);
        if ((a>-epsilon) && (a<epsilon)) return new IPoint(null,null,-1);
        float f = 1/a;
        Vec3D s = start.minus(t.getP1());
        float u = f*s.dot(p);
        if ((u<0.0) || (u>1.0)) return new IPoint(null,null,-1);
        Vec3D q = s.cross(e1);
        float v = f*dir.dot(q);
        if ((v<0.0) || (u+v>1.0)) return new IPoint(null,null,-1);
        float dist=f*e2.dot(q);
        if (dist<epsilon) return new IPoint(null,null,-1);
        Vec3D ip=t.getP1().mult(1-u-v).add(t.getP2().mult(u)).add(t.getP3().mult(v));
        return new IPoint(t,ip,dist);
    }

    void setStart(Vec3D start) {
        this.start = start;
    }

    Vec3D getDir() {
        return dir;
    }

    void setDir(Vec3D dir) {
        this.dir = dir;
    }
}