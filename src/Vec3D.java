class Vec3D {
    private float x, y, z, w=1;
    Vec3D(float xx, float yy, float zz) { x=xx; y=yy; z=zz; }
    Vec3D(float xx, float yy, float zz, float ww) { x=xx; y=yy; z=zz; w=ww; }
    Vec3D add(Vec3D v)
    { return new Vec3D(x+v.x, y+v.y, z+v.z); }
    Vec3D minus(Vec3D v)
    { return new Vec3D(x-v.x, y-v.y, z-v.z); }
    Vec3D mult(float a)
    { return new Vec3D(a*x, a*y, a*z); }

    void normalize()
    {  float dist = (float) Math.sqrt( (x * x)+(y * y)+(z * z) );
        x = x / dist;
        y = y / dist;
        z = z / dist;
    }

    float dot(Vec3D v) { return x*v.x+y*v.y+z*v.z; }

    Vec3D cross(Vec3D v) {
        return new Vec3D( y*v.z-z*v.y, z*v.x-x*v.z, x*v.y-y*v.x);
    }

    float getX() {
        return x;
    }

    void setX(float x) {
        this.x = x;
    }

    float getY() {
        return y;
    }

    void setY(float y) {
        this.y = y;
    }

    float getZ() {
        return z;
    }

    void setZ(float z) {
        this.z = z;
    }

    float getW() {
        return w;
    }

    void setW(float w) {
        this.w = w;
    }
}
