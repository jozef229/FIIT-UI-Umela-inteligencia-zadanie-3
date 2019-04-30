import java.util.Random;

public class Jedinec{
	int[] gen;
	public int fitnes = 0;
	public Jedinec(int pocet_genov){
		fitnes = 0;
		gen = new int[pocet_genov];
	}
	public void random_napln(int pocet_genov,int X,int Y) {
		int[] duplicita = new int[X*2 + Y*2];
		Random rand = new Random();
		for(int i = 0; i< pocet_genov ; i++) {
			int n = rand.nextInt(X*2 + Y*2);
			while(duplicita[n] == 1) n = rand.nextInt(X*2 + Y*2);
			gen[i] = n;
			duplicita[n] = 1;
		}
	}
	public int[] getGen() {
		return gen;
	}
	public void setGen(int[] gen) {
		this.gen = gen;
	}
	public int getFitnes() {
		return fitnes;
	}
	public void setFitnes(int fitnes) {
		this.fitnes = fitnes;
	}
	public void setGenI(int gen_i, int i) {
		gen[i] = gen_i;
	}
	public int getGenI(int i) {
		return gen[i];
	}
}
