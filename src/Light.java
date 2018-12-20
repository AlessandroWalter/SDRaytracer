class Light {
    private RGB color;
    private Vec3D position;
    Light(Vec3D pos, RGB c) { position=pos; color=c; }

    RGB getColor() {
        return color;
    }

    Vec3D getPosition() {
        return position;
    }
}