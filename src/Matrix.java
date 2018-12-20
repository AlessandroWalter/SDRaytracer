import java.util.List;

class Matrix {
    private float val [][] = new float[4][4];

    Matrix() { }
    Matrix(float [][] vs) { val=vs; }

    void print()
    { for(int i=0;i<4;i++)
    { for(int j=0;j<4;j++)
    { System.out.print(" "+(val[i][j]+"       ").substring(0,8)); }
        System.out.println();
    }
    }


    Matrix mult(Matrix m)
    { Matrix r=new Matrix();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            { float sum=0f;
                for(int k=0;k<4;k++) sum=sum+val[i][k]*m.val[k][j];
                r.val[i][j]=sum;
            }
        return r;
    }

    Vec3D mult(Vec3D v)
    { Vec3D temp = new Vec3D( val[0][0]*v.getX()+val[0][1]*v.getY()+val[0][2]*v.getZ()+val[0][3]*v.getW(),
            val[1][0]*v.getX()+val[1][1]*v.getY()+val[1][2]*v.getZ()+val[1][3]*v.getW(),
            val[2][0]*v.getX()+val[2][1]*v.getY()+val[2][2]*v.getZ()+val[2][3]*v.getW(),
            val[3][0]*v.getX()+val[3][1]*v.getY()+val[3][2]*v.getZ()+val[3][3]*v.getW() );
        temp.setX(temp.getX()/temp.getW());
        temp.setY(temp.getY()/temp.getW());
        temp.setZ(temp.getZ()/temp.getW());
        temp.setW(1);
        return temp;
    }

    static Matrix createXRotation(float angle)
    { return new Matrix(new float[][]{
            { 1, 0, 0 , 0},
            { 0, (float)Math.cos(angle), (float)-Math.sin(angle), 0 },
            { 0, (float)Math.sin(angle), (float)Math.cos(angle),0 },
            { 0 , 0, 0, 1 } });
    }

    static Matrix createYRotation(float angle)
    { return new Matrix(new float[][]{
            { (float)Math.cos(angle), 0, (float)Math.sin(angle), 0 },
            { 0, 1, 0, 0 },
            { (float)-Math.sin(angle), 0, (float)Math.cos(angle), 0 },
            { 0, 0, 0, 1 } });
    }

    static Matrix createTranslation(float dx, float dy, float dz)
    { return new Matrix(new float[][]{
            { 1, 0, 0, dx },
            { 0, 1, 0, dy },
            { 0, 0, 1, dz },
            { 0, 0, 0, 1  } });
    }

    void apply(List<Triangle> ts)
    { for(Triangle t: ts)
    { t.setP1(this.mult(t.getP1()));
        t.setP2(this.mult(t.getP2()));
        t.setP3(this.mult(t.getP3()));
        Vec3D e1=t.getP2().minus(t.getP1()),
                e2=t.getP3().minus(t.getP1());
        t.setNormal(e1.cross(e2));
        t.getNormal().normalize();
    }
    }
}