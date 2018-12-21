package main.de.unitrier.fst.alessandrowalter.sdraytracer;

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

    void apply(List<Figures> f)
    { for(Figures figures: f)
    { figures.setP1(this.mult(figures.getP1()));
        figures.setP2(this.mult(figures.getP2()));
        figures.setP3(this.mult(figures.getP3()));
        Vec3D e1=figures.getP2().minus(figures.getP1()),
                e2=figures.getP3().minus(figures.getP1());
        figures.setNormal(e1.cross(e2));
        figures.getNormal().normalize();
    }
    }
}