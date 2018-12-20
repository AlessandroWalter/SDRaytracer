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
   Stephan Diehl, Universit�t Trier, 2010-2016
*/

public class SDRaytracer extends JFrame{
   private static final long serialVersionUID = 1L;
   private boolean profiling=false;
   private int width=1000;
   private int height=1000;
   
   private Future[] futureList= new Future[width];
   private int nrOfProcessors = Runtime.getRuntime().availableProcessors();
   private ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);
   
   private int maxRec=3;
   private int rayPerPixel=1;
   private int startX, startY, startZ;

   private List<Triangle> triangles;

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
    System.out.println("");
   }
}

SDRaytracer(){
   createScene();

   if (!profiling) renderImage(); else profileRenderImage();
   
   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   Container contentPane = this.getContentPane();
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
   addKeyListener(new KeyAdapter(){
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
             repaint();
           }
       }
   });
         
        area.setPreferredSize(new Dimension(width,height));
        contentPane.add(area);
        this.pack();
        this.setVisible(true);
}

double tanFovx;
double tanFovy;
 
void renderImage(){
   tanFovx = Math.tan(fovx);
   tanFovy = Math.tan(fovy);
   for(int i=0;i<width;i++)
   { futureList[i]=  (Future) eservice.submit(new RaytraceTask(this,i)); 
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
   IPoint ip = hitObject(ray);  // (ray, p, n, triangle);
   if (ip.getDist()>IPoint.epsilon)
     return lighting(ray, ip, rec);
   else
     return black;
}


IPoint hitObject(Ray ray) {
   IPoint isect=new IPoint(null,null,-1);
   float idist=-1;
   for(Triangle t : triangles)
     { IPoint ip = ray.intersect(t);
        if (ip.getDist()!=-1)
        if ((idist==-1)||(ip.getDist()<idist))
         { // save that intersection
          idist=ip.getDist();
          isect.setIpoint(ip.getIpoint());
          isect.setDist(ip.getDist());
          isect.setTriangle(t);
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
  Triangle triangle=ip.getTriangle();
  RGB color = addColors(triangle.getColor(), ambientColor,1);
  Ray shadowRay=new Ray();
   for(Light light : lights)
       { shadowRay.setStart(point);
         shadowRay.setDir(light.getPosition().minus(point).mult(-1));
         shadowRay.getDir().normalize();
         IPoint ip2=hitObject(shadowRay);
         if(ip2.getDist()<IPoint.epsilon)
         {
           float ratio=Math.max(0,shadowRay.getDir().dot(triangle.getNormal()));
           color = addColors(color,light.getColor(),ratio);
         }
       }
     Ray reflection=new Ray();
     Vec3D L=ray.getDir().mult(-1);
     reflection.setStart(point);
     reflection.setDir(triangle.getNormal().mult(2*triangle.getNormal().dot(L)).minus(L));
     reflection.getDir().normalize();
     RGB rcolor=rayTrace(reflection, rec+1);
     float ratio =  (float) Math.pow(Math.max(0,reflection.getDir().dot(L)), triangle.getShininess());
     color = addColors(color,rcolor,ratio);
     return(color);
  }

  void createScene()
   { triangles = new ArrayList<Triangle>();

   
     Cube.addCube(triangles, 0,35,0, 10,10,10,new RGB(0.3f,0,0),0.4f);       //rot, klein
     Cube.addCube(triangles, -70,-20,-20, 20,100,100,new RGB(0f,0,0.3f),.4f);
     Cube.addCube(triangles, -30,30,40, 20,20,20,new RGB(0,0.4f,0),0.2f);        // gr�n, klein
     Cube.addCube(triangles, 50,-20,-40, 10,80,100,new RGB(.5f,.5f,.5f), 0.2f);
     Cube.addCube(triangles, -70,-26,-40, 130,3,40,new RGB(.5f,.5f,.5f), 0.2f);


     Matrix mRx=Matrix.createXRotation((float) (xAngleFactor *Math.PI/16));
     Matrix mRy=Matrix.createYRotation((float) (yAngleFactor *Math.PI/16));
     Matrix mT=Matrix.createTranslation(0,0,200);
     Matrix m=mT.mult(mRx).mult(mRy);
     m.print();
     m.apply(triangles);
   }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
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


