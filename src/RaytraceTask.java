import java.util.concurrent.Callable;


class RaytraceTask implements Callable{
    private SDRaytracer tracer;
    private int i;
    RaytraceTask(SDRaytracer t, int ii) { tracer=t; i=ii; }

    public RGB[] call()
    { RGB[] col=new RGB[tracer.getHeight()];
        for (int j=0;j<tracer.getHeight();j++)
        {  tracer.getImage()[i][j]=new RGB(0,0,0);
            for(int k=0;k<tracer.getRayPerPixel();k++)
            { double di=i+(Math.random()/2-0.25);
                double dj=j+(Math.random()/2-0.25);
                if (tracer.getRayPerPixel()==1) { di=i; dj=j; }
                Ray eye_ray=new Ray();
                eye_ray.setStart(tracer.getStartX(), tracer.getStartY(), tracer.getStartZ());   // ro
                eye_ray.setDir  ((float) (((0.5 + di) * tracer.tan_fovx * 2.0) / tracer.getWidth() - tracer.tan_fovx),
                        (float) (((0.5 + dj) * tracer.tan_fovy * 2.0) / tracer.getHeight() - tracer.tan_fovy),
                        (float) 1f);    // rd
                eye_ray.normalize();
                col[j]= tracer.addColors(tracer.getImage()[i][j],tracer.rayTrace(eye_ray,0),1.0f/tracer.getRayPerPixel());
            }
        }
        return col;
    }
}
