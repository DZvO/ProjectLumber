package oz.wizards.lumber.gfx;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ParticleEngine {
	class Particle {
		public Vector3f position, end;
		public int id, type, interpolationMethod;
		public float timeStepping, currentStep, scale;
		
		public Particle (Vector3f start, Vector3f end, float scale, int type, int im, float ts, int id) {
			position = start;
			this.end = end;
			this.scale = scale;
			this.type = type;
			interpolationMethod = im;
			timeStepping = ts;
			currentStep = 0;
			this.id = id;
		}
	}
	
	Texture tex;
	VertexBatch vb;
	List<Particle> particles;
	int currentId = 0;
	
	public ParticleEngine (Texture tex, VertexBatch vb) {
		this.tex = tex;
		this.vb = vb;
		particles = new ArrayList<Particle>();
	}
	
	public void add (Vector3f startPosition, Vector3f endPosition, float scale, int type, int interpolationMethod, float timeStepping) {
		particles.add(new Particle(startPosition, endPosition, scale, type, interpolationMethod, timeStepping, currentId++));
	}
	
	public void tick () {
	}
	
	public void render () {
		for(int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			
			float fact = (float) ((1.0 / 16.0) * 7);
			Vector3f d = new Vector3f(p.position.x + fact, p.position.y + fact, 1);
			Vector3f c = new Vector3f(p.position.x + fact, p.position.y - 0, 1);
			Vector3f b = new Vector3f(p.position.x - 0, p.position.y + fact, 1);
			Vector3f a = new Vector3f(p.position.x - 0, p.position.y - 0, 1);
			vb.putQuad(tex, a, b, c, d, new Vector2f(0, 48), new Vector2f(7,55), new Vector3f(1,1,1));
		}
	}
	
}
