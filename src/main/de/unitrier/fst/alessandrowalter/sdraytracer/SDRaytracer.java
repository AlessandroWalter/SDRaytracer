package main.de.unitrier.fst.alessandrowalter.sdraytracer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/* Implementation of a very simple Raytracer
   Stephan Diehl, Universitaet Trier, 2010-2016
*/

public class SDRaytracer{
   private boolean profiling=false;
   private int width=1000;
   private int height=1000;
   private JFrame jFrame;

   private Future[] futureList= new Future[width];
   private int nrOfProcessors = Runtime.getRuntime().availableProcessors();
   private ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);
   
   private int maxRec=3;
   private int rayPerPixel=1;
   private int startX, startY, startZ;

   private List<Figures> figuresList;

   private Light mainLight  = new Light(new Vec3D(0,100,0), new RGB(0.1f,0.1f,0.1f));

   private Light lights[]= new Light[]{ mainLight
                                ,new Light(new Vec3D(100,200,300), new RGB(0.5f,0,0.0f))
                                ,new Light(new Vec3D(-100,200,300), new RGB(0.0f,0,0.5f))
                              };

   private RGB [][] image= new RGB[width][height];
   
   private float fovx=(float) 0.628;
   private float fovy=(float) 0.628;
   private RGB ambientColor =new RGB(0.01f,0.01f,0.01f);
   private RGB black=new RGB(0.0f,0.0f,0.0f);
   private int yAngleFactor =4, xAngleFactor =-4;

void profileRenderImage(){
  long end, start, time;

  renderImage();
  
  for(int procs=1; procs<6; procs++) {

   maxRec=procs-1;
   System.out.print(procs);
   for(int i=0; i<10; i++)
     { start = System.currentTimeMillis();

       renderImage();

       end = System.currentTimeMillis();
       time = end - start;
       System.out.print(";"+time);
     }
    System.out.println();
   }
}

SDRaytracer(){
    jFrame = new JFrame("SDRaytracer");
   createScene();

   if (!profiling) renderImage(); else profileRenderImage();

    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container contentPane = jFrame.getContentPane();
   contentPane.setLayout(new BorderLayout());
   JPanel area = new JPanel() {
            public void paint(Graphics g) {
              System.out.println("fovx="+fovx+", fovy="+fovy+", xangle="+ xAngleFactor +", yangle="+ yAngleFactor);
              if (image==null) return;
              for(int i=0;i<width;i++)
               for(int j=0;j<height;j++)
                { g.setColor(image[i][j].color());
                  // zeichne einzelnen Pixel
                  g.drawLine(i,height-j,i,height-j);
                }
            }
           };
    jFrame.addKeyListener((new KeyAdapter(){
       public void keyPressed(KeyEvent e){
           boolean redraw=false;
           switch (e.getKeyCode()){
               case KeyEvent.VK_DOWN:
                   xAngleFactor--;
                   redraw=true;
                   break;
               case KeyEvent.VK_UP:
                   xAngleFactor--;
                   redraw=true;
                   break;
               case KeyEvent.VK_LEFT:
                   yAngleFactor--;
                   redraw=true;
                   break;
               case KeyEvent.VK_RIGHT:
                   yAngleFactor++;
                   redraw=true;
           }
          if (redraw)
           { createScene();
             renderImage();
             jFrame.repaint();
           }
       }
   }));
         
        area.setPreferredSize(new Dimension(width,height));
        contentPane.add(area);
        jFrame.pack();
        jFrame.setVisible(true);
}

double tanFovx;
double tanFovy;
 
void renderImage(){
   tanFovx = Math.tan(fovx);
   tanFovy = Math.tan(fovy);
   for(int i=0;i<width;i++)
   { futureList[i]=  eservice.submit(new RaytraceTask(this,i));
   }
   
    for(int i=0;i<width;i++)
       { try {
          RGB [] col = (RGB[]) futureList[i].get();
          for(int j=0;j<height;j++)
            image[i][j]=col[j];
         }
   catch (InterruptedException e) {}
   catch (ExecutionException e) {}
    }
   }
 


RGB rayTrace(Ray ray, int rec) {
   if (rec>maxRec) return black;
   IPoint ip = hitObject(ray);
   if (ip.getDist()>IPoint.epsilon)
     return lighting(ray, ip, rec);
   else
     return black;
}


IPoint hitObject(Ray ray) {
   IPoint isect=new IPoint(null,null,-1);
   float idist=-1;
   for(Figures figures : figuresList)
     { IPoint ip = ray.intersect(figures);
        if (ip.getDist()!=-1)
        if ((idist==-1)||(ip.getDist()<idist))
         { // save that intersection
          idist=ip.getDist();
          isect.setIpoint(ip.getIpoint());
          isect.setDist(ip.getDist());
          isect.setFigures(figures);
         }
     }
   return isect;  // return intersection point and normal
}


RGB addColors(RGB c1, RGB c2, float ratio)
 { return new RGB( (c1.getRed()+c2.getRed()*ratio),
           (c1.getGreen()+c2.getGreen()*ratio),
           (c1.getBlue()+c2.getBlue()*ratio));
  }
  
RGB lighting(Ray ray, IPoint ip, int rec) {
  Vec3D point=ip.getIpoint();
  Figures figures=ip.getFigures();
  RGB color = addColors(figures.getColor(), ambientColor,1);
  Ray shadowRay=new Ray();
   for(Light light : lights)
       { shadowRay.setStart(point);
         shadowRay.setDir(light.getPosition().minus(point).mult(-1));
         shadowRay.getDir().normalize();
         IPoint ip2=hitObject(shadowRay);
         if(ip2.getDist()<IPoint.epsilon)
         {
           float ratio=Math.max(0,shadowRay.getDir().dot(figures.getNormal()));
           color = addColors(color,light.getColor(),ratio);
         }
       }
     Ray reflection=new Ray();
     Vec3D L=ray.getDir().mult(-1);
     reflection.setStart(point);
     reflection.setDir(figures.getNormal().mult(2*figures.getNormal().dot(L)).minus(L));
     reflection.getDir().normalize();
     RGB rcolor=rayTrace(reflection, rec+1);
     float ratio =  (float) Math.pow(Math.max(0,reflection.getDir().dot(L)), figures.getShininess());
     color = addColors(color,rcolor,ratio);
     return(color);
  }

  void createScene()
   { figuresList = new ArrayList<Figures>();

   
     Figures.addCube(figuresList, 0,35,0, 10,10,10,new RGB(0.3f,0,0),0.4f);       //rot, klein
     Figures.addCube(figuresList, -70,-20,-20, 20,100,100,new RGB(0f,0,0.3f),.4f);
     Figures.addCube(figuresList, -30,30,40, 20,20,20,new RGB(0,0.4f,0),0.2f);        // gr�n, klein
     Figures.addCube(figuresList, 50,-20,-40, 10,80,100,new RGB(.5f,.5f,.5f), 0.2f);
     Figures.addCube(figuresList, -70,-26,-40, 130,3,40,new RGB(.5f,.5f,.5f), 0.2f);


     Matrix mRx=Matrix.createXRotation((float) (xAngleFactor *Math.PI/16));
     Matrix mRy=Matrix.createYRotation((float) (yAngleFactor *Math.PI/16));
     Matrix mT=Matrix.createTranslation(0,0,200);
     Matrix m=mT.mult(mRx).mult(mRy);
     m.print();
     m.apply(figuresList);
   }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    int getNrOfProcessors() {
        return nrOfProcessors;
    }

    int getRayPerPixel() {
        return rayPerPixel;
    }

    int getStartX() {
        return startX;
    }

    int getStartY() {
        return startY;
    }

    int getStartZ() {
        return startZ;
    }

    RGB[][] getImage() {
        return image;
    }
}


