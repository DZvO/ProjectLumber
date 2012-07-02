package oz.wizards.lumber;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLSync;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import oz.wizards.lumber.gfx.ParticleEngine;
import oz.wizards.lumber.gfx.Shader;
import oz.wizards.lumber.gfx.Texture;
import oz.wizards.lumber.gfx.VertexBatch;
import oz.wizards.lumber.gfx.VertexBuffer;
import oz.wizards.lumber.io.KeyboardLayout;
import oz.wizards.lumber.math.Rectangle2f;
import oz.wizards.lumber.math.Vec2;
import oz.wizards.lumber.math.Vec3;

@SuppressWarnings("unused")
public class Game implements Runnable {

	public Vector3f translation = new Vector3f();
	public Vector3f rotation = new Vector3f();
	public Vector3f scale = new Vector3f(1, 1, 1);

	private boolean loop = true;

	Texture tex;
	VertexBatch vb;
	VertexBatch vbInterface;

	int prevx = -1, prevy = -1;
	int diffy = 0, diffx = 0;
	Vector2f m = new Vector2f();
	long deltaTime = 0;
	long lastPrinted = 0;
	long lastTicked = 0;

	Level level = new Level();
	Shader normalShader;
	Shader tintShader;
	Shader billboardShader;
	VertexBuffer normalBuffer;
	VertexBuffer entityBuffer;
	ParticleEngine particleEngine;
	
	boolean houseSelected = false;
	Vector2f housePosition = new Vector2f(-1,-1);
	long houseSelectedTimestamp = 0;

	KeyboardLayout kbl;

	@Override
	public void run() {
		kbl = new KeyboardLayout("keyboardlayout.txt");

		init();
		load();
		while (loop) {
			deltaTime = System.nanoTime();
			if (Display.isCloseRequested()
					|| Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				loop = false;
				break;
			}

			// try {
			// Thread.sleep(10);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			tick();
			render();
			Display.update();
			deltaTime = System.nanoTime() - deltaTime;
			if (lastPrinted < System.nanoTime()) {
				lastPrinted = System.nanoTime() + 5L * 1000000000L;
				System.out.println("dt: " + ((double) deltaTime / 1000000.0)
						+ " ms");
			}
		}
		Display.destroy();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW_MATRIX);
		glPushMatrix();
		glLoadIdentity();

		glRotatef(rotation.x, 1.f, 0.f, 0.f);
		glRotatef(rotation.y, 0.f, 1.f, 0.f);
		glScalef(scale.x, scale.y, scale.z);
		glTranslatef(-translation.x, -translation.y, -translation.z);

		/*
		 * vb.putQuad(tex, new Vec3(0.f, 0.f, -5.f), new Vec3(0.f, 1.f, -5.f),
		 * new Vec3(1.f, 0.f, -5.f), new Vec3(1.f, 1.f, -5.f), new Vec2( 0.f,
		 * 0.f), new Vec2(7.f, 7.f), new Vec3(1.f, 1.f, 1.f)); vb.putQuad(tex,
		 * new Vec3(0.f, 0.f, 5.f), new Vec3(0.f, 1.f, 5.f), new Vec3(1.f, 0.f,
		 * 5.f), new Vec3(1.f, 1.f, 5.f), new Vec2(0.f, 0.f), new Vec2(7.f,
		 * 7.f), new Vec3(1.f, 1.f, 1.f)); for(int x = 0; x < 256; x++) {
		 * for(int z = 0; z < 256; z++) { if(level[x+z*256] == 1) {
		 * vb.putQuad(tex, new Vec3(x, 0.f, z+1), new Vec3(x, 0.f, z), new
		 * Vec3(x+1, 0.f, z+1), new Vec3(x+1, 0.f, z), new Vec2(0.f, 0.f), new
		 * Vec2(7.f, 7.f), new Vec3(1.f, 1.f, 1.f)); } else if(level[x+z*256] ==
		 * 2) { vb.putQuad(tex, new Vec3(x, 0.f, z+1), new Vec3(x, 0.f, z), new
		 * Vec3(x+1, 0.f, z+1), new Vec3(x+1, 0.f, z), new Vec2(0.f, 0.f), new
		 * Vec2(7.f, 7.f), new Vec3(1.f, 1.f, 1.f)); } } }
		 */
		// vb.end();

		/*
		 * vb.putQuad(tex, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), new
		 * Vector3f(1, 0, 0), new Vector3f(1, 1, 0), new Vector2f(0, 0), new
		 * Vector2f(1, 1), new Vector3f(1,0,0)); vb.putQuad(tex, new
		 * Vector3f(0+3, 0, 0), new Vector3f(0+3, 1, 0), new Vector3f(1+3, 0,
		 * 0), new Vector3f(1+3, 1, 0), new Vector2f(0, 0), new Vector2f(1, 1),
		 * new Vector3f(0,1,0));
		 */
		// normalBuffer.add(new Vector3f(0, 0, +0.5f), new Vector2f(0, 0), 1);
		// normalBuffer.add(new Vector3f(0, 1, +0.5f), new Vector2f(0, 1), 1);
		// normalBuffer.add(new Vector3f(1, 1, +0.5f), new Vector2f(1, 1), 1);
		// normalBuffer.add(new Vector3f(1, 0, +0.5f), new Vector2f(1, 0), 1);

		Vector2f offset = new Vector2f(0, 0);
		Vector2f uvmin = new Vector2f(0, 0);
		Vector2f uvmax = new Vector2f(0, 0);
		Vector2f bguvmin = new Vector2f(0, 32);
		Vector2f bguvmax = new Vector2f(16, 48);
		Vector3f color = new Vector3f(1, 1, 1);
		float ratio = (float) 800 / (float) 600;
		Vector2f c = new Vector2f((1.f / ((1.f / ratio)
				* (Display.getWidth() / 2) * scale.x))
				* (m.x - Display.getWidth() / 2) + translation.x,
				(1.f / ((Display.getHeight() / 2) * scale.y))
						* (m.y - Display.getHeight() / 2) + translation.y);
		Rectangle2f r = new Rectangle2f(new Vector2f(), new Vector2f());
		for (int x = 0; x < Level.dim; x++) {
			for (int y = 0; y < Level.dim; y++) {
				// grass-background
				vb.putQuad(tex, new Vector3f(0 + x, 0 + y, -.1f), new Vector3f(
						0 + x, 1 + y, -.1f), new Vector3f(1 + x, 0 + y, -.1f),
						new Vector3f(1 + x, 1 + y, -.1f), bguvmin, bguvmax,
						new Vector3f(1, 1, 1));

				// r = new Rectangle2f(new Vector2f(x, y), new Vector2f(x + 1,
				// y + 1));
				// r.min.x = x;
				// r.min.y = y;
				// r.max.x = x+1;
				// r.max.y = y+1;

				// if (r.contains(c)) {
				// color.x = 1;
				// color.y = color.z = 0;
				// } else {
				// color.x = color.y = color.z = 1;
				// }

				// if (level.get(x, y) == Level.FOREST) {
				// uvmin.x = 0 + 16;
				// uvmin.y = 0;
				// uvmax.x = 15 + 16;
				// uvmax.y = 16;
				// } else if (level.get(x, y) == Level.VILLAGE) {
				// uvmin.x = 0 + 16;
				// uvmin.y = 16;
				// uvmax.x = 15 + 16;
				// uvmax.y = 32;
				// } else if (level.get(x, y) == Level.VILLAGE_DESTROYED) {
				// uvmin.x = 0 + 16;
				// uvmin.y = 16+16;
				// uvmax.x = 15 + 16;
				// uvmax.y = 32+16;
				// //color = new Vector3f(1, 0, 0);
				// } else if (level.get(x, y) == Level.NOTHING) {
				// continue;
				// } else {
				// continue;
				// }

				// uvmin.x = 16;
				// uvmin.y = 0 + 16*level.get(x,y);
				// uvmax.x = 15+16;
				// uvmax.y = 16 + 16*level.get(x,y);
				//
				// vb.putQuad(tex, new Vector3f(0 + x, 0 + y, 0), new Vector3f(
				// 0 + x, 1 + y, 0), new Vector3f(1 + x, 0 + y, 0),
				// new Vector3f(1 + x, 1 + y, 0), uvmin, uvmax, color);
			}
		}
		
		
		//interface
		{
			//cursor
			if(houseSelected) {
				long diff = (System.nanoTime()/1000 - houseSelectedTimestamp/1000);
				int x = (int) housePosition.x;
				int y = (int) housePosition.y;
				long speed = 50000;
				if(diff < speed*1) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(0, 96), new Vector2f(16,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*2) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(16, 96), new Vector2f(32,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*3) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(32, 96), new Vector2f(48,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*4) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(48, 96), new Vector2f(64,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*5) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(64, 96), new Vector2f(80,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*6) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(80, 96), new Vector2f(96,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*7) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(96, 96), new Vector2f(112,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*8) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(112, 96), new Vector2f(128,112), new Vector3f(1,1,1));
				}
				else if(diff < speed*9) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(0, 112), new Vector2f(16,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*10) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(16, 112), new Vector2f(32,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*11) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(32, 112), new Vector2f(48,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*12) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(48, 112), new Vector2f(64,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*13) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(64, 112), new Vector2f(80,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*14) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(80, 112), new Vector2f(96,128), new Vector3f(1,1,1));
				}
				else if(diff < speed*15) {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(96, 112), new Vector2f(112,128), new Vector3f(1,1,1));
				}
				else {
					vbInterface.putQuad(tex, new Vector3f(0+x, 0+y, 1), new Vector3f(0+x, 1+y, 1), 
							new Vector3f(1+x,0+y,1), new Vector3f(1+x,1+y,1), 
							new Vector2f(112, 112), new Vector2f(128,128), new Vector3f(1,1,1));
					houseSelectedTimestamp = System.nanoTime();
				}
			}
			
		}
		
		particleEngine.render();
		
		tintShader.enable();
		// normalBuffer.render(GL_QUADS, translation);
		// entityBuffer.render(GL_QUADS, translation);
		vb.render();
		vbInterface.render();
		Shader.disable();
		
		normalShader.enable();
		level.render();
		Shader.disable();
		
		glPopMatrix();
	}

	boolean isUpdating = false;

	private void tick() {
		if (lastTicked < System.nanoTime() && !isUpdating) {
			lastTicked = System.nanoTime() + 1000000000L;
			new Thread(new Runnable() {
				@Override
				public void run() {
					isUpdating = true;
					level.tick();
					isUpdating = false;
				}
			}).start();
			System.out.println("ticked");
		}

		diffx = Mouse.getX() - prevx;
		diffy = Mouse.getY() - prevy;
		prevx = Mouse.getX();
		prevy = Mouse.getY();
		m.x = Mouse.getX();
		m.y = Mouse.getY();

		/*
		 * if (Mouse.isButtonDown(1)) { rotation.x += -diffy * 1.f; rotation.y
		 * += diffx * 1.f;
		 * 
		 * if (rotation.x > 90.0f) rotation.x = 90.0f; if (rotation.x < -90.0f)
		 * rotation.x = -90.0f; }
		 */


		float vel = 0.1f;
		if (m.y < Display.getHeight() / 10) {
			translation.y += -vel * (1.f/scale.y);
		} else if (m.y > (Display.getHeight() / 10) * 9) {
			translation.y += vel * (1.f/scale.y);
		}
		if (m.x < Display.getWidth() / 10) {
			translation.x -= vel * (1.f/scale.x);
		} else if (m.x > (Display.getWidth() / 10) * 9) {
			translation.x += vel * (1.f/scale.x);
		}
		
		int md = Mouse.getDWheel();
		if (md > 0) {
			scale.x *= 1.1f;
			scale.y *= 1.1f;
		} else if (md < 0) {
			scale.x *= 0.9f;
			scale.y *= 0.9f;
		}
			
		while (Mouse.next()) {
			
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() == true) {
				float ratio = (float) 800 / (float) 600;
				Vector2f c = new Vector2f((1.f / ((1.f / ratio)
						* (Display.getWidth() / 2) * scale.x))
						* (m.x - Display.getWidth() / 2) + translation.x,
						(1.f / ((Display.getHeight() / 2) * scale.y))
								* (m.y - Display.getHeight() / 2)
								+ translation.y);
				int x = (int) Math.floor(c.x);
				int y = (int) Math.floor(c.y);
				//level.set((int) Math.floor(c.x), (int) Math.floor(c.y),
						//Level.NOTHING);
				System.out.println("x: " + Math.floor(c.x) + ", y: "
						+ Math.floor(c.y));
				
				if(level.get(x,y) == Level.VILLAGE) {
					houseSelected = true;
					housePosition.x = x;
					housePosition.y = y;
					houseSelectedTimestamp = System.nanoTime();
				} else if (level.get(x,y) == Level.FOREST) {
					System.out.print("LUMBER!\n");
					particleEngine.add(new Vector3f(x,y,1), new Vector3f(housePosition.x,housePosition.y,1), 1.0f, 1, 1, 0.05f);
				} else if (level.get(x,y) == Level.NOTHING) {
					houseSelected = false;
				}
			} else if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() == true) {
				float ratio = (float) 800 / (float) 600;
				Vector2f c = new Vector2f((1.f / ((1.f / ratio)
						* (Display.getWidth() / 2) * scale.x))
						* (m.x - Display.getWidth() / 2) + translation.x,
						(1.f / ((Display.getHeight() / 2) * scale.y))
								* (m.y - Display.getHeight() / 2)
								+ translation.y);
				int x = (int) Math.floor(c.x);
				int y = (int) Math.floor(c.y);
				System.out.print("x: " + x + ", y: "
						+ y);
				System.out.print(" id: "
						+ level.get(x, y));
				System.out.print(", tex: V[(" + 
						(level.buffer.get((x+y*63) * 6 * 5) + 3) +
						" | " + (level.buffer.get((x+y*63) * 6 * 5) + 4) + ")] \n");
				
			}

		}

		float velTranslation = 1.f;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			translation.z -= velTranslation;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			translation.z += velTranslation;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			translation.x -= velTranslation;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			translation.x += velTranslation;
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState() == true) {
				if (Keyboard.getEventKey() == kbl.get("reset")) {
					rotation = translation = new Vector3f(0, 0, 0);
				}
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_P) {
				Vector2f mouse = new Vector2f(Mouse.getX(), Mouse.getY());
				FloatBuffer mvMat = ByteBuffer.allocateDirect(4 * (4 * 4))
						.order(ByteOrder.nativeOrder()).asFloatBuffer(), projMat = ByteBuffer
						.allocateDirect(4 * (4 * 4))
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
				glGetFloat(GL_MODELVIEW_MATRIX, mvMat);
				glGetFloat(GL_PROJECTION_MATRIX, projMat);
				IntBuffer view = ByteBuffer.allocateDirect(4 * (4 * 4))
						.order(ByteOrder.nativeOrder()).asIntBuffer();
				glGetInteger(GL_VIEWPORT, view);

				FloatBuffer obj_pos = ByteBuffer.allocateDirect(4 * (4 * 4))
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
				// float _mouseY = view.get(3) - mouse.y;
				float _mouseY = mouse.y;
				FloatBuffer fbuf = ByteBuffer.allocateDirect(4 * 2)
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
				glReadPixels((int) mouse.x, (int) _mouseY, 1, 1,
						GL_DEPTH_COMPONENT, GL_FLOAT, fbuf);
				GLU.gluUnProject(mouse.x, _mouseY, fbuf.get(0), mvMat, projMat,
						view, obj_pos);
				System.out.print(obj_pos.get(0) + ", ");
				System.out.print(obj_pos.get(1) + ", ");
				System.out.print(obj_pos.get(2) + "\n");
				System.out.println("Scale(" + scale.toString() + ")");
			}
		}
		particleEngine.tick();
		// System.out.println("" + rotation.y);
	}

	private void init() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
			Display.setTitle("Tiny World");
			// Display.setLocation(-1680, 0);

			// init OpenGL
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glViewport(0, 0, 800, 600);
			float ratio = (float) 800 / (float) 600;
			// Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
			// glFrustum(-ratio, ratio, -1, 1, 1, 100);
			glOrtho(-ratio, ratio, -1, 1, -1, 1);

			glMatrixMode(GL_MODELVIEW);

			glEnable(GL_TEXTURE_2D);
			glDisable(GL_SMOOTH);
			// glEnable(GL_CULL_FACE);
			glEnable(GL_CULL_FACE);
			glFrontFace(GL_CW);

			glEnable(GL_ALPHA_TEST);
			glAlphaFunc(GL_EQUAL, 1.0f);
			// glDepthMask(false);

			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL_BLEND);

			glEnable(GL_DEPTH_TEST);

			float k = 1.f / 255.f;
			glClearColor(k * 0x80, k * 0xa6, k * 0xa9, 1.0f);

			try {
				tex = new Texture("res/tiles.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void load() {
		normalShader = new Shader("res/shaders/normal");
		tintShader = new Shader("res/shaders/tint");

		normalBuffer = new VertexBuffer(tintShader, tex);
		entityBuffer = new VertexBuffer(tintShader, tex);
		vb = new VertexBatch(tintShader);
		
		vbInterface = new VertexBatch(tintShader);
		particleEngine = new ParticleEngine(tex, vbInterface);

		level.init(normalShader, tex);
	}
}