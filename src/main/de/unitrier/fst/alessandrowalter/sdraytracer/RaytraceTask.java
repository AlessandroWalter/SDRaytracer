package main.de.unitrier.fst.alessandrowalter.sdraytracer;

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
                Ray eyeRay=new Ray();
                eyeRay.setStart(tracer.getStartX(), tracer.getStartY(), tracer.getStartZ());   // ro
                eyeRay.setDir  ((float) (((0.5 + di) * tracer.tanFovx * 2.0) / tracer.getWidth() - tracer.tanFovx),
                        (float) (((0.5 + dj) * tracer.tanFovy * 2.0) / tracer.getHeight() - tracer.tanFovy),
                        1f);    // rd
                eyeRay.normalize();
                col[j]= tracer.addColors(tracer.getImage()[i][j],tracer.rayTrace(eyeRay,0),1.0f/tracer.getRayPerPixel());
            }
        }
        return col;
    }
}
