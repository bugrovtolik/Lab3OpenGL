package com.mycompany.lab3opengl;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Dimension;
import java.awt.event.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import java.nio.FloatBuffer;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.swing.JFrame;

public class Render implements GLEventListener, KeyListener, MouseMotionListener, MouseWheelListener {
    private int prevMouseX, prevMouseY;
    private double x, y, z = -2.0, lx, ly, lz, defaultZ = -2.0, angleY;
    private boolean fly = false;
    private int fogMode;

    void drawGround(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glColor3d(0, 0, 1);

        gl.glBegin(GL2.GL_LINES);

        for(double line = -1.0; line <= 1.0; line += 0.1)
        {
            gl.glVertex3d(line, -0.15, 1.0);
            gl.glVertex3d(line, -0.15, -1.0);

            gl.glVertex3d(1.0, -0.15, line);
            gl.glVertex3d(-1.0, -0.15, line);
        }
        gl.glEnd();
    }

    void picture(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        final GLUT glut = new GLUT();

        float mat_solid[] = {0.75f, 0.75f, 0.0f, 1.0f};
        float mat_zero[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float mat_transparent[] = {0.0f, 0.8f, 0.8f, 0.6f};
        float mat_emission[] = {0.0f, 0.3f, 0.3f, 0.6f};

        gl.glPushMatrix();
            gl.glTranslated(-0.15, 0, -0.15);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, FloatBuffer.wrap(mat_zero));
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, FloatBuffer.wrap(mat_solid));
            glut.glutSolidTeapot(0.2);
        gl.glPopMatrix();

        gl.glPushMatrix();
            gl.glTranslated(0.3, 0.15, 0.3);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, FloatBuffer.wrap(mat_emission));
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, FloatBuffer.wrap(mat_transparent));
            gl.glEnable(GL2.GL_BLEND);
            gl.glDepthMask(false);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            glut.glutSolidTorus(0.1, 0.2, 50, 50);
            gl.glDepthMask(true);
            gl.glDisable(GL2.GL_BLEND);
        gl.glPopMatrix();
	}
	
	public static void main(String[] args) {       
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Render r = new Render();
        glcanvas.addGLEventListener(r);
        glcanvas.setSize(800, 800);
        glcanvas.addKeyListener(r);
        glcanvas.addMouseMotionListener(r);
        glcanvas.addMouseWheelListener(r);

        final FPSAnimator animator = new FPSAnimator(glcanvas, 100, true);

        JFrame frame = new JFrame("OpenGL");
        frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			if(animator.isStarted()) {
					animator.stop();
			}
			System.exit(0);
			}
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(glcanvas);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        final GLU glu = new GLUgl2();
        gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_POLYGON_BIT);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if(fly) {
			x = defaultZ * sin(toRadians(--angleY));
			z = defaultZ * cos(toRadians(angleY));
			y = defaultZ * cos(toRadians(angleY));
			if(y < 0) {
				y = -y;
			}
        }

        glu.gluLookAt(x, y, z, lx, ly, lz, 0.0, 1.0, 0.0);

        drawGround(drawable);
        picture(drawable);

        gl.glPopAttrib();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glClearDepth(1.0);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        float[] position = {(float)x, (float)y, (float)z, 2.0f};
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        
           float[] mat = {0.1745f, 0.01175f, 0.01175f};
//		   gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, FloatBuffer.wrap(mat));
//		   mat[0] = 0.61424f; mat[1] = 0.04135f; mat[2] = 0.04136f;
//		   gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, FloatBuffer.wrap(mat));
           mat[0] = 0.727811f; mat[1] = 0.626959f; mat[2] = 0.626959f;
           gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, FloatBuffer.wrap(mat));
           gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.6f * 128.0f);
        
        gl.glEnable(GL2.GL_FOG);
        
           float[] fogColor = {0.5f, 0.5f, 0.5f, 1.0f};
           fogMode = GL2.GL_EXP;
           gl.glFogi(GL2.GL_FOG_MODE, fogMode);
           gl.glFogfv(GL2.GL_FOG_COLOR, FloatBuffer.wrap(fogColor));
           gl.glFogf(GL2.GL_FOG_DENSITY, 0.35f);
           gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);
           gl.glFogf(GL2.GL_FOG_START, 1.0f);
           gl.glFogf(GL2.GL_FOG_END, 5.0f);
        
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int xx, int yy, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        final GLU glu = new GLUgl2();
        double aspectRatio = height == 0 ? width : width / (double)height;
        gl.glViewport(0, 0, width, height); 
        gl.glMatrixMode(GL2.GL_PROJECTION);

        glu.gluPerspective(50.0, aspectRatio, 1.0, 100.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
}

	@Override
	public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            fly = !fly;
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
        int nx = e.getX();
        int ny = e.getY();
        Dimension size = e.getComponent().getSize();

        double mouseDeltaXInDegrees = 360.0 * ((nx - prevMouseX) / size.getWidth());
        double mouseDeltaYInScreenPercent = ((prevMouseY - ny) / size.getHeight());

        prevMouseX = nx;
        prevMouseY = ny;

        angleY += mouseDeltaXInDegrees;
        defaultZ -= (mouseDeltaYInScreenPercent * defaultZ) ;

        x = defaultZ * sin(toRadians(angleY));
        z = defaultZ * cos(toRadians(angleY));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		y -= e.getWheelRotation();
	}
}
