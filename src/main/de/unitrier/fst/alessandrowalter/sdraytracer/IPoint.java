package main.de.unitrier.fst.alessandrowalter.sdraytracer;

class IPoint {
    final static float epsilon=0.0001f;
    private Figures figures;
    private Vec3D ipoint;

    private float dist;

    IPoint(Figures tt, Vec3D ip, float d) { figures =tt; ipoint=ip; dist=d; }


    Figures getFigures() {
        return figures;
    }

    Vec3D getIpoint() {
        return ipoint;
    }

    float getDist() {
        return dist;
    }

    void setFigures(Figures figures) {
        this.figures = figures;
    }

    void setIpoint(Vec3D ipoint) {
        this.ipoint = ipoint;
    }

    void setDist(float dist) {
        this.dist = dist;
    }
}