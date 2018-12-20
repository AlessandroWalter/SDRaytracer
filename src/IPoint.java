class IPoint {
    final static float epsilon=0.0001f;
    private Triangle triangle;
    private Vec3D ipoint;

    private float dist;

    IPoint(Triangle tt, Vec3D ip, float d) { triangle=tt; ipoint=ip; dist=d; }


    Triangle getTriangle() {
        return triangle;
    }

    Vec3D getIpoint() {
        return ipoint;
    }

    float getDist() {
        return dist;
    }

    void setTriangle(Triangle triangle) {
        this.triangle = triangle;
    }

    void setIpoint(Vec3D ipoint) {
        this.ipoint = ipoint;
    }

    void setDist(float dist) {
        this.dist = dist;
    }
}